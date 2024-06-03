package com.Fullstack.reactSpringBoot.controllers.Admin.userManagement.teachersManagement;


import com.Fullstack.reactSpringBoot.dto.teacher.TeacherDTO;
import com.Fullstack.reactSpringBoot.dto.teacher.TeacherInfoDTO;
import com.Fullstack.reactSpringBoot.models.Auth.ERole;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Subjects;
import com.Fullstack.reactSpringBoot.models.userManagement.Teachers;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.SubjectRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.TeacherRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.UsersRepository;
import com.Fullstack.reactSpringBoot.services.teachers.TeacherServiceImpl;
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
public class TeacherController {

    private final TeacherServiceImpl teacherService;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final UserServiceImpl userService;
    private final ValidationService validationService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;



    // Mapper un objet Teachers à un objet DTO
    private TeacherDTO mapTeacherToDTO(Teachers teacher) {
        List<Subjects> taughtSubjects = teacher.getTaughtSubjects();
        String nameSubject = taughtSubjects.stream()
                .map(Subjects::getNameSubject)
                .collect(Collectors.joining(", "));

        return TeacherDTO.builder()
                .matricule(teacher.getMatricule())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .nameSubject(nameSubject)
                .gender(teacher.getGender())
                .birth_date(teacher.getBirth_date())
                .birthplace(teacher.getBirthPlace())
                .city(teacher.getAddress().getCity())
                .street(teacher.getAddress().getStreet())
                .zipCode(teacher.getAddress().getZipCode())
                .photo(teacher.getPhoto())
                .email(teacher.getInfoContacts().getEmail())
                .phoneNumber(teacher.getInfoContacts().getPhoneNumber())
                .build();
    }

    // Mapper un objet Teachers (pour informations minimales)
    private TeacherInfoDTO mapTeachersToDTO(Teachers teacher) {
        List<Subjects> taughtSubjects = teacher.getTaughtSubjects();
        String nameSubject = taughtSubjects.stream()
                .map(Subjects::getNameSubject)
                .collect(Collectors.joining(", "));
        return TeacherInfoDTO.builder()
                .matricule(teacher.getMatricule())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .nameSubject(nameSubject)
                .build();

    }


    @PostMapping("/teachers")
    public ResponseEntity<String> createTeacher(@RequestBody Teachers teacher) {
        log.info("Création de l'enseignant: {}", teacher.getMatricule());
        int matricule = userService.generateUniqueMatricule();
        teacher.setMatricule(matricule);
        validationService.checkMatriculeAvailability(teacher.getMatricule());
        validationService.validateEmail(teacher.getInfoContacts().getEmail());
        try {
            if (!ERole.TEACHER.equals(teacher.getRole().getName())) {
                return ResponseEntity.badRequest().body("Erreur: Le rôle doit être TEACHER");
            }

            String encryptedPassword = passwordEncoder.encode(teacher.getPassword());
            teacher.setPassword(encryptedPassword);

            Long subjectId = teacher.getTaughtSubjects().getFirst().getSubject_id();
            Optional<Subjects> optionalSubject = subjectRepository.findById(subjectId);

            if (optionalSubject.isPresent()) {
                Subjects subject = optionalSubject.get();

                if (subject.getHeadTeacher() != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("La matière avec l'ID " + subjectId + " est déjà associée à un enseignant.");
                }

                teacher.getTaughtSubjects().add(subject);
                subject.setHeadTeacher(teacher);

                Teachers createdTeacher = teacherRepository.save(teacher);
                subjectRepository.save(subject);

                return ResponseEntity.ok("Enseignant créé avec succès: " + createdTeacher.getMatricule());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("La matière avec l'ID " + subjectId + " n'existe pas.");
            }
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de l'enseignant: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }


    // Obtenir tous les enseignants
    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers() {
        List<Teachers> allTeachers = teacherRepository.findAll();
        if (allTeachers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun enseignant trouvé.");
        }

        List<TeacherInfoDTO> teacherDTOList = allTeachers.stream()
                .map(this::mapTeachersToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(teacherDTOList);
    }

    // Obtenir un enseignant par ID
    @GetMapping("/teachers/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Optional<Teachers> teacherOptional = teacherRepository.findById(id);

        if (teacherOptional.isPresent()) {
            Teachers teacher = teacherOptional.get();
            TeacherDTO teacherDTO = mapTeacherToDTO(teacher);

            return ResponseEntity.ok(teacherDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Enseignant non trouvé avec l'ID : " + id);
        }
    }

    // Rechercher des enseignants par prénom ou nom de famille
    @GetMapping("/teachers/searchByFirstNameOrLastName")
    public ResponseEntity<?> searchTeachersByFirstNameOrLastName(@RequestParam String firstName, @RequestParam String lastName) {

        List<Teachers> teacherList = teacherService.findTeachersByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
        if (teacherList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun enseignant trouvé.");
        }

        List<TeacherInfoDTO> teacherDTOList = teacherList.stream()
                .map(this::mapTeachersToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(teacherDTOList);
    }

    // Rechercher un enseignant par prénom et nom de famille
    @GetMapping("/teachers/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchTeachersByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        Optional<Teachers> teacherOptional = teacherService.findTeachersByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

        if (teacherOptional.isPresent()) {
            Teachers student = teacherOptional.get();
            TeacherDTO teacherDTO = mapTeacherToDTO(student);

            return ResponseEntity.ok(teacherDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Enseignant " + lastName + " "+firstName+" n'existe pas.");
        }
    }

    // Mettre à jour un enseignant par ID
    @PutMapping("/teachers/{id}")
    public String updateTeacher(@PathVariable Long id, @RequestBody Teachers updatedTeacher) {
        Long subjectId = updatedTeacher.getTaughtSubjects().getFirst().getSubject_id(); // Récupérer l'ID de la matière associée

        // Vérifier si l'enseignant avec l'ID spécifié existe dans la base de données
        Optional<Teachers> teacherOptional = teacherRepository.findById(id);
        if (teacherOptional.isPresent()) {
            Teachers existingTeacher = teacherOptional.get();

            // Vérifier si le matricule existe et appartient à un autre enseignant
            if (usersRepository.existsByMatricule(updatedTeacher.getMatricule()) && !existingTeacher.getMatricule().equals(updatedTeacher.getMatricule())) {
                return "Le matricule " + updatedTeacher.getMatricule() + " existe déjà.";
            }

            // Mettre à jour les champs de l'enseignant avec les nouvelles valeurs
            existingTeacher.setFirstName(updatedTeacher.getFirstName());
            existingTeacher.setLastName(updatedTeacher.getLastName());
            existingTeacher.setGender(updatedTeacher.getGender());
            existingTeacher.setBirth_date(updatedTeacher.getBirth_date());
            existingTeacher.setBirthPlace(updatedTeacher.getBirthPlace());
            existingTeacher.setAddress(updatedTeacher.getAddress());
            existingTeacher.setRole(updatedTeacher.getRole());
            existingTeacher.setPassword(updatedTeacher.getPassword());
            existingTeacher.setPhoto(updatedTeacher.getPhoto());
            existingTeacher.setInfoContacts(updatedTeacher.getInfoContacts());
            existingTeacher.setMatricule(updatedTeacher.getMatricule());

            // Vérifier si la matière associée existe dans la base de données
            Optional<Subjects> optionalSubject = subjectRepository.findById(subjectId);
            if (optionalSubject.isPresent()) {
                Subjects subject = optionalSubject.get();

                // Vérifier si la matière est déjà associée à un autre enseignant
                if (subject.getHeadTeacher() != null && !subject.getHeadTeacher().equals(existingTeacher)) {
                    return "La matière avec l'ID " + subjectId + " est déjà associée à un autre enseignant.";
                }

                // Mettre à jour la matière associée à l'enseignant
                existingTeacher.getTaughtSubjects().clear(); // Effacer les matières existantes
                existingTeacher.getTaughtSubjects().add(subject); // Ajouter la nouvelle matière

                // Mettre à jour l'enseignant associé à la matière
                subject.setHeadTeacher(existingTeacher);
                subjectRepository.save(subject);
            } else {
                // La matière avec l'ID spécifié n'existe pas
                return "La matière avec l'ID " + subjectId + " n'existe pas.";
            }

            // Enregistrer les modifications de l'enseignant dans la base de données
            teacherRepository.save(existingTeacher);

            return "Enseignant avec l'ID " + id + " mis à jour avec succès.";
        } else {
            // L'enseignant avec l'ID spécifié n'a pas été trouvé
            return "L'enseignant avec l'ID " + id + " n'existe pas.";
        }
    }

    // Supprimer un enseignant par ID
    @DeleteMapping("/teachers/delete/{id}")
    public String deleteTeacherById(@PathVariable Long id) {
        return teacherService.deleteTeachersByUserId(id);
    }

    // Supprimer des enseignants par prénom et nom de famille
    @DeleteMapping("/teachers/deleteByFirstNameAndLastName")
    public String deleteTeachersByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return teacherService.deleteTeachersByFirstNameAndLastName(firstName, lastName);
    }

}
