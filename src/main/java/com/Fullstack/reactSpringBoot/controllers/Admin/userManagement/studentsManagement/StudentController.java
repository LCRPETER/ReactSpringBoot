package com.Fullstack.reactSpringBoot.controllers.Admin.userManagement.studentsManagement;


import com.Fullstack.reactSpringBoot.dto.student.StudentDTO;
import com.Fullstack.reactSpringBoot.dto.student.StudentInfoDTO;
import com.Fullstack.reactSpringBoot.models.Auth.ERole;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;
import com.Fullstack.reactSpringBoot.models.userManagement.Students;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.GroupRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.StudentsRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.UsersRepository;
import com.Fullstack.reactSpringBoot.services.students.StudentServiceImpl;
import com.Fullstack.reactSpringBoot.services.users.UserServiceImpl;
import com.Fullstack.reactSpringBoot.services.validation.ValidationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

    private final StudentsRepository studentsRepository;
    private final GroupRepository groupRepository;
    private final StudentServiceImpl studentService;
    private final UserServiceImpl userService;
    private final ValidationService validationService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    // Mapper un objet Students à un objet DTO
    private StudentDTO mapStudentToDTO(Students student) {
        return new StudentDTO(
                student.getMatricule(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getBirth_date(),
                student.getBirthPlace(),
                student.getAddress().getCity(),
                student.getAddress().getStreet(),
                student.getAddress().getZipCode(),
                student.getGroups().getNameGroup(),
                student.getGroups().getLevel(),
                student.getPhoto(),
                student.getInfoContacts().getEmail(),
                student.getInfoContacts().getPhoneNumber()
        );
    }

    // Mapper un objet Students (pour informations minimales)
    private StudentInfoDTO mapStudentsToDTO(Students student) {
        return new StudentInfoDTO(
                student.getMatricule(),
                student.getFirstName(),
                student.getLastName(),
                student.getGroups().getNameGroup(),
                student.getGroups().getLevel()
        );
    }

    @PostMapping("/students")
    public ResponseEntity<String> createStudent(@RequestBody Students student) {
        Long groupeId = student.getGroups().getGroup_id();
        String firstName = student.getFirstName();
        String lastName = student.getLastName();

        log.info("Création d'un étudiant: {}", student.getMatricule());
        int matricule = userService.generateUniqueMatricule();
        student.setMatricule(matricule);
        validationService.checkMatriculeAvailability(student.getMatricule());
        validationService.validateEmail(student.getInfoContacts().getEmail());

        try {
            if (!ERole.STUDENT.equals(student.getRole().getName())) {
                return ResponseEntity.badRequest().body("Erreur: Le rôle doit être STUDENT");
            }

            String encryptedPassword = passwordEncoder.encode(student.getPassword());
            student.setPassword(encryptedPassword);
            // Vérifier si la classe existe
            Optional<Groups> optionalGroupe = groupRepository.findById(groupeId);
            if (optionalGroupe.isPresent()) {
                 Groups groups = optionalGroupe.get();

                 // Récupérer le niveau de la classe associée à l'étudiant
                 String studentGroupeLevel = groups.getLevel();

                 // Vérifier si un étudiant avec le même lastName et firstName existe déjà dans la classe et au même niveau (ignoreCase)
                 Optional<Students> existingStudent = studentService.findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGroupsLevel(firstName, lastName, studentGroupeLevel);

                 if (existingStudent.isPresent()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("Un étudiant avec le même nom et prénom existe déjà dans ce groupe et ce niveau.");
                 } else {
                        // Enregistrer l'étudiant dans la table Students
                        student.setGroups(groups); // Associer l'étudiant à la Classe
                        studentsRepository.save(student);

                        // Inscrire l'étudiant dans la liste des étudiants de cette classe
                        groups.getStudents().add(student);
                        groupRepository.save(groups);

                        return ResponseEntity.ok("Étudiant inscrit avec succès.");
                 }
            } else {
                // Le cas où la classe n'existe pas
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La classe avec l'ID " + groupeId + " n'existe pas.");
            }
        }catch (RuntimeException e) {
            log.error("Erreur lors de la création de l'étudiant: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    // Obtenir tous les étudiants
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        List<Students> allStudents = studentsRepository.findAll();
        if (allStudents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun étudiant trouvé.");
        }

        List<StudentInfoDTO> studentDTOList = allStudents.stream()
                .map(this::mapStudentsToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOList);
    }

    // Obtenir un étudiant par ID
    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Optional<Students> studentOptional = studentsRepository.findById(id);

        if (studentOptional.isPresent()) {
            Students student = studentOptional.get();
            StudentDTO studentDTO = mapStudentToDTO(student);

            return ResponseEntity.ok(studentDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Étudiant non trouvé avec l'ID : " + id);
        }
    }

    // Rechercher des étudiants par prénom ou nom de famille
    @GetMapping("/students/searchByFirstNameOrLastName")
    public ResponseEntity<?> searchStudentsByFirstNameOrLastName(@RequestParam String firstName, @RequestParam String lastName) {

        List<Students> studentList = studentService.findStudentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
        if (studentList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun étudiant trouvé.");
        }

        List<StudentInfoDTO> studentDTOList = studentList.stream()
                .map(this::mapStudentsToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOList);
    }

    // Rechercher un étudiant par prénom et nom de famille
    @GetMapping("/students/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchStudentsByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        Optional<Students> studentOptional = studentService.findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

        if (studentOptional.isPresent()) {
            Students student = studentOptional.get();
            StudentDTO studentDTO = mapStudentToDTO(student);

            return ResponseEntity.ok(studentDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("L'étudiant " + lastName + " "+firstName+" n'existe pas.");
        }
    }

    // Mettre à jour un étudiant
    @PutMapping("/students")
    public ResponseEntity<String> updateStudent(@RequestBody Students student) {
        // Vérifier si le matricule existe déjà et appartient à un autre étudiant
        Optional<Users> existingStudentWithMatricule = usersRepository.findByMatricule(student.getMatricule());
        if (existingStudentWithMatricule.isPresent() && !existingStudentWithMatricule.get().getUser_id().equals(student.getUser_id())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Un autre étudiant avec le matricule " + student.getMatricule() + " existe déjà.");
        }

        Optional<Students> outOptional = studentsRepository.findById(student.getUser_id());
        if (outOptional.isPresent()) {
            studentsRepository.save(student);
            return ResponseEntity.ok("Étudiant avec l'ID " + student.getUser_id() + " mis à jour avec succès.");
        } else {
            // L'étudiant n'a pas été trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Étudiant avec l'ID " + student.getUser_id() + " non trouvé.");
        }
    }

    // Supprimer un étudiant par ID
    @DeleteMapping("/students/delete/{id}")
    public String deleteStudentById(@PathVariable Long id) {
        return studentService.deleteStudentsByUserId(id);
    }

    // Supprimer des étudiants par prénom et nom de famille
    @DeleteMapping("/students/deleteByFirstNameAndLastName")
    public String deleteStudentsByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return studentService.deleteStudentsByFirstNameAndLastName(firstName, lastName);
    }
}