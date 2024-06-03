package com.Fullstack.reactSpringBoot.controllers.Admin.classroomAndSubjectManagement;


import com.Fullstack.reactSpringBoot.dto.subject.SubjectDTO;
import com.Fullstack.reactSpringBoot.dto.subject.SubjectInfoDTO;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Subjects;
import com.Fullstack.reactSpringBoot.models.userManagement.Teachers;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.GroupRepository;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.SubjectRepository;
import com.Fullstack.reactSpringBoot.services.groupe.GroupServiceImpl;
import com.Fullstack.reactSpringBoot.services.subjects.SubjectServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final SubjectServiceImpl subjectService;
    private final GroupServiceImpl groupeService;


    private SubjectInfoDTO mapSubjectsToDTO(Subjects subject) {
        List<Groups> groups = subject.getGroups();

        String nameGroupe = groups.stream()
                .map(G -> G.getNameGroup() + " (" + G.getLevel() + ")")
                .collect(Collectors.joining(", "));

        Teachers teacher = subject.getHeadTeacher();
        String teacherFullName = teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "";

        return SubjectInfoDTO.builder()
                .subject_id(subject.getSubject_id())
                .nameSubject(subject.getNameSubject())
                .description(subject.getDescription())
                .nameGroupe(nameGroupe)
                .teacherFullName(teacherFullName)
                .build();
    }

    // Mapper un objet Subjects (pour informations minimales)
    private SubjectDTO mapSubjectToDTO(Subjects subject) {
        return new SubjectDTO(
                subject.getSubject_id(),
                subject.getNameSubject(),
                subject.getDescription()
        );
    }

    // Créer une matière
    @PostMapping("/subjects")
    public String createSubject(@RequestBody Subjects subject) {
        Long groupeId = subject.getGroups().getFirst().getGroup_id();
        String level = subject.getGroups().getFirst().getLevel();

        // Vérifier si le groupe existe
        Optional<Groups> optionalGroupe = groupRepository.findById(groupeId);
        Optional<Groups> levelGroupe = groupeService.findByLevel(level);

        if (optionalGroupe.isPresent() && levelGroupe.isPresent()) {
            Groups groups = optionalGroupe.get();

            // Vérifier si une matière du même nom et du même niveau existe déjà dans le groupe
            boolean subjectExistsForLevel = groups.getTaughtSubjects().stream()
                    .anyMatch(existingSubject -> existingSubject.getNameSubject().equalsIgnoreCase(subject.getNameSubject())
                            && existingSubject.getGroups().stream()
                            .anyMatch(groupeLevel -> groupeLevel.getLevel().equalsIgnoreCase(level)));

            if (subjectExistsForLevel) {
                return "La matière '" + subject.getNameSubject() + "' existe déjà pour le niveau '" + level + "' du groupe." + " '"+ groups.getNameGroup() + ".'";
            }

            // Enregistrer la matière dans la table Subjects
            subject.setGroups(Collections.singletonList(groups)); // Associer la matière au groupe
            subjectRepository.save(subject);

            // Ajouter la matière dans la liste des matières de ce niveau pour le groupe
            groups.getTaughtSubjects().add(subject);
            groupRepository.save(groups);

            return "Matière enregistrée avec succès.";

        } else {
            // Le cas où le groupe n'existe pas
            return "Le groupe " + groupeId + " ou le niveau '" + level + "' n'existe pas";
        }
    }

    // Obtenir toutes les matières
    @GetMapping("/subjects")
    public ResponseEntity<?> getAllSubjects() {
        List<Subjects> allSubject = subjectRepository.findAll();
        if (allSubject.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucune matière trouvé.");
        }

        List<SubjectDTO> subjectDTOList = allSubject.stream()
                .map(this::mapSubjectToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(subjectDTOList);
    }

    // Obtenir une matière par ID
    @GetMapping("/subjects/{id}")
    public ResponseEntity<?> getParentById(@PathVariable Long id) {
        Optional<Subjects> subjectOptional = subjectRepository.findById(id);

        if (subjectOptional.isPresent()) {
            Subjects subject = subjectOptional.get();
            SubjectInfoDTO subjectDTO = mapSubjectsToDTO(subject);

            return ResponseEntity.ok(subjectDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Matière non trouvé avec l'ID : " + id);
        }
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<String> updateSubject(@PathVariable Long id, @RequestBody Subjects updatedSubject) {
        Optional<Subjects> subjectOptional = subjectRepository.findById(id);

        if (subjectOptional.isPresent()) {
            Subjects existingSubject = subjectOptional.get();

            // Mettre à jour les champs modifiables de la matière existante
            existingSubject.setNameSubject(updatedSubject.getNameSubject());
            existingSubject.setDescription(updatedSubject.getDescription());

            // Vérifier si le groupe associé à la matière doit être modifié
            Long groupId = updatedSubject.getGroups().getFirst().getGroup_id();
            Optional<Groups> groupOptional = groupRepository.findById(groupId);
            if (groupOptional.isPresent()) {
                // Mettre à jour l'association de groupe uniquement si le groupe existe
                Groups group = groupOptional.get();
                existingSubject.getGroups().clear(); // Supprimer les anciennes associations de groupe
                existingSubject.getGroups().add(group); // Associer la matière au nouveau groupe
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Le groupe avec l'ID " + groupId + " n'existe pas.");
            }

            // Enregistrer la matière mise à jour
            subjectRepository.save(existingSubject);

            return ResponseEntity.ok("Matière mise à jour avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Matière non trouvée avec l'ID : " + id);
        }
    }

    // Supprimer une matière par ID
    @DeleteMapping("/subjects/delete/{id}")
    public String deleteSubjectById(@PathVariable Long id) {
        return subjectService.deleteSubjectById(id);
    }
}
