package com.Fullstack.reactSpringBoot.controllers.Admin.userManagement.parentsManagement;

import com.Fullstack.reactSpringBoot.dto.parent.ParentDTO;
import com.Fullstack.reactSpringBoot.dto.parent.ParentInfoDTO;
import com.Fullstack.reactSpringBoot.dto.student.StudentInfoDTO;
import com.Fullstack.reactSpringBoot.models.Auth.ERole;
import com.Fullstack.reactSpringBoot.models.userManagement.Parents;
import com.Fullstack.reactSpringBoot.models.userManagement.Students;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import com.Fullstack.reactSpringBoot.repositories.userManagement.ParentsRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.StudentsRepository;
import com.Fullstack.reactSpringBoot.repositories.userManagement.UsersRepository;
import com.Fullstack.reactSpringBoot.services.parents.ParentServiceImpl;
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
public class ParentController {

    private final ParentsRepository parentsRepository;
    private final ParentServiceImpl parentService;
    private final StudentsRepository studentsRepository;
    private final UserServiceImpl userService;
    private final ValidationService validationService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    // Mapper un objet Parents à un objet DTO
    private ParentDTO mapParentToDTO(Parents parent) {
        return new ParentDTO(
                parent.getMatricule(),
                parent.getFirstName(),
                parent.getLastName(),
                parent.getGender(),
                parent.getBirth_date(),
                parent.getBirthPlace(),
                parent.getAddress().getCity(),
                parent.getAddress().getStreet(),
                parent.getAddress().getZipCode(),
                parent.getPhoto(),
                parent.getInfoContacts().getEmail(),
                parent.getInfoContacts().getPhoneNumber()
        );
    }

    // Mapper un objet Parents (pour informations minimales)
    private ParentInfoDTO mapParentsToDTO(Parents parent) {
        return new ParentInfoDTO(
                parent.getMatricule(),
                parent.getFirstName(),
                parent.getLastName()
        );
    }

    @PostMapping("/parents")
    public ResponseEntity<String> postParents(@RequestBody Parents parent) {

        log.info("Création d'un étudiant: {}", parent.getMatricule());
        int matricule = userService.generateUniqueMatricule();
        parent.setMatricule(matricule);
        validationService.checkMatriculeAvailability(parent.getMatricule());
        validationService.validateEmail(parent.getInfoContacts().getEmail());

        try {
            if (!ERole.PARENT.equals(parent.getRole().getName())) {
                return ResponseEntity.badRequest().body("Erreur: Le rôle doit être PARENT");
            }

            String encryptedPassword = passwordEncoder.encode(parent.getPassword());
            parent.setPassword(encryptedPassword);
            // Vérifier si le parent existe déjà par son firstName et lastName (ignorer la casse)
            Optional<Parents> existingParent = parentService.findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(parent.getFirstName(), parent.getLastName());

            if (existingParent.isPresent()) {
                // Le parent existe déjà, renvoyer un message approprié
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Le parent existe déjà avec le même prénom et nom.");
            }

                // Le parent n'existe pas encore, donc on peut l'ajouter
                parentsRepository.save(parent);
                return ResponseEntity.status(HttpStatus.CREATED)
                      .body("Parent ajouté avec succès.");
        }catch (RuntimeException e) {
              log.error("Erreur lors de la création d'un parent: {}", e.getMessage());
              return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }

    }

    // Associé un étudiant à un parent
    @PostMapping("/parents/{parentId}/students/{studentId}")
    public ResponseEntity<String> addStudentToParent(@PathVariable Long parentId, @PathVariable Long studentId) {

        // Rechercher le parent par ID
        Optional<Parents> optionalParent = parentsRepository.findById(parentId);
        if (optionalParent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Rechercher l'étudiant par ID
        Optional<Students> optionalStudent = studentsRepository.findById(studentId);
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Parents parent = optionalParent.get();
        Students student = optionalStudent.get();

        // Vérifier si l'étudiant appartient déjà à un parent
        if (student.getParent() != null) {
            return ResponseEntity.badRequest().body("Cet étudiant appartient déjà à un parent.");
        }

        try {
            // Associer l'étudiant au parent
            student.setParent(parent);
            parent.getStudents().add(student);

            // Mettre à jour le parent et l'étudiant
            parentsRepository.save(parent);
            studentsRepository.save(student);

            return ResponseEntity.ok("Étudiant ajouté avec succès au parent.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'association de l'étudiant au parent : " + e.getMessage());
        }
    }

    // Obtenir tous les parents
    @GetMapping("/parents")
    public ResponseEntity<?> getAllStudents() {
        List<Parents> allParents = parentsRepository.findAll();
        if (allParents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun étudiant trouvé.");
        }

        List<ParentInfoDTO> parentDTOList = allParents.stream()
                .map(this::mapParentsToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(parentDTOList);
    }

    // Obtenir un parent par ID
    @GetMapping("/parents/{id}")
    public ResponseEntity<?> getParentById(@PathVariable Long id) {
        Optional<Parents> parentOptional = parentsRepository.findById(id);

        if (parentOptional.isPresent()) {
            Parents parent = parentOptional.get();
            ParentDTO parentDTO = mapParentToDTO(parent);

            return ResponseEntity.ok(parentDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Parent non trouvé avec l'ID : " + id);
        }
    }

    // Rechercher des parents par prénom ou nom de famille
    @GetMapping("/parents/searchByFirstNameOrLastName")
    public ResponseEntity<?> searchParentsByFirstNameOrLastName(@RequestParam String firstName, @RequestParam String lastName) {

        List<Parents> ParentList = parentService.findParentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
        if (ParentList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun parent trouvé.");
        }

        List<ParentInfoDTO> parentsDTOList = ParentList.stream()
                .map(this::mapParentsToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(parentsDTOList);
    }

    // Rechercher un parent par prénom et nom de famille
    @GetMapping("/parents/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchParentsByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        Optional<Parents> parentOptional = parentService.findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

        if (parentOptional.isPresent()) {
            Parents parent = parentOptional.get();
            ParentDTO parentDTO = mapParentToDTO(parent);

            return ResponseEntity.ok(parentDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Parent " + lastName + " "+firstName+" n'existe pas.");
        }
    }

    // retourner la liste des étudiants liés à un parent.
    @GetMapping("/parents/{parentId}/students")
    public ResponseEntity<?> getStudentsByParentId(@PathVariable Long parentId) {
        // Rechercher le parent par ID
        Optional<Parents> optionalParent = parentsRepository.findById(parentId);

        if (optionalParent.isPresent()) {
            Parents parent = optionalParent.get();
            List<Students> students = parent.getStudents();

            if (students.isEmpty()) {
                return ResponseEntity.ok("Ce parent n'a aucun enfant associé.");
            } else {
                List<StudentInfoDTO> studentDTOs = students.stream()
                        .map(student -> new StudentInfoDTO(
                                student.getMatricule(),
                                student.getLastName(),
                                student.getFirstName(),
                                student.getGroups().getNameGroup(),
                                student.getGroups().getLevel()
                        ))
                        .collect(Collectors.toList());

                return ResponseEntity.ok(studentDTOs);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parent non trouvé avec l'ID : " + parentId);
        }
    }

    @PutMapping("/parents/{id}")
    public ResponseEntity<String> updateParent(@PathVariable Long id, @RequestBody Parents updatedParent) {
        Optional<Parents> optionalParent = parentsRepository.findById(id);

        // Vérifier si le matricule existe déjà et appartient à un autre étudiant
        Optional<Users> existingParentWithMatricule = usersRepository.findByMatricule(updatedParent.getMatricule());
        if (existingParentWithMatricule.isPresent() && !existingParentWithMatricule.get().getUser_id().equals(updatedParent.getUser_id())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Un autre étudiant avec le matricule " + updatedParent.getMatricule() + " existe déjà.");
        }

        if (optionalParent.isPresent()) {
            Parents existingParent = optionalParent.get();

            // Mettre à jour les champs modifiables du parent existant
            existingParent.setFirstName(updatedParent.getFirstName());
            existingParent.setLastName(updatedParent.getLastName());
            existingParent.setMatricule(updatedParent.getMatricule());
            existingParent.setGender(updatedParent.getGender());
            existingParent.setBirth_date(updatedParent.getBirth_date());
            existingParent.setBirthPlace(updatedParent.getBirthPlace());
            existingParent.getAddress().setCity(updatedParent.getAddress().getCity());
            existingParent.getAddress().setStreet(updatedParent.getAddress().getStreet());
            existingParent.getAddress().setZipCode(updatedParent.getAddress().getZipCode());
            existingParent.setPhoto(updatedParent.getPhoto());
            existingParent.getInfoContacts().setEmail(updatedParent.getInfoContacts().getEmail());
            existingParent.getInfoContacts().setPhoneNumber(updatedParent.getInfoContacts().getPhoneNumber());

            // Enregistrer les modifications du parent
            parentsRepository.save(existingParent);

            return ResponseEntity.ok("Parent mis à jour avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Parent non trouvé avec l'ID : " + id);
        }
    }

    // Supprimer un parent par ID
    @DeleteMapping("/parents/delete/{id}")
    public String deleteParentById(@PathVariable Long id) {
        return parentService.deleteParentsByUserId(id);
    }

    // Supprimer des étudiants par prénom et nom de famille
    @DeleteMapping("/parents/deleteByFirstNameAndLastName")
    public String deleteParentsByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return parentService.deleteParentsByFirstNameAndLastName(firstName, lastName);
    }


}
