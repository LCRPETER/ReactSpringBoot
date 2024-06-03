package com.Fullstack.reactSpringBoot.controllers.Admin.classroomAndSubjectManagement;

import com.Fullstack.reactSpringBoot.dto.group.GroupDTO;
import com.Fullstack.reactSpringBoot.dto.group.GroupInfoDTO;
import com.Fullstack.reactSpringBoot.dto.subject.SubjectDTO;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.GroupRepository;
import com.Fullstack.reactSpringBoot.services.groupe.GroupServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class GroupController {

    private final GroupRepository groupRepository;
    private final GroupServiceImpl groupService;


    @PostMapping("/groups")
    public ResponseEntity<String> postGroupes(@RequestBody Groups groups) {
        String level = groups.getLevel();
        String schoolYear = groups.getSchoolYear();

        // Vérifier si le niveau de la classe existe déjà pour cette année scolaire
        Groups existingGroups = groupService.findByLevelAndSchoolYear(level, schoolYear);

        if (existingGroups != null) {
            // Le niveau de classe existe déjà pour cette année scolaire, retourner une erreur
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ClassRoom with level '" + level + "' already exists for school year '" + schoolYear + "'");
        }

        // Le niveau de classe n'existe pas encore pour cette année scolaire, sauvegarder la nouvelle salle de classe
        groupRepository.save(groups);
        return ResponseEntity.status(HttpStatus.CREATED).body("ClassRoom Added Successfully");
    }

    @GetMapping("/groups")
    public List<GroupDTO> getAllGroupes() {
        List<Groups> allGroups = groupRepository.findAll();

        // Mapper les ClassRooms vers ClassRoomDTO

        return allGroups.stream()
                .map(Gr -> new GroupDTO(
                        Gr.getGroup_id(),
                        Gr.getNameGroup(),
                        Gr.getLevel(),
                        Gr.getSchoolYear()))
                .collect(Collectors.toList());
    }

    @GetMapping("/groups/{id}")
    public Optional<Groups> findGroupById(@PathVariable Long id) {
        Optional<Groups> outOptional = groupRepository.findById(id);

        if(outOptional.isPresent()){
            return groupRepository.findById(id);
        }else{
            return Optional.empty();
        }
    }

    @GetMapping("/groups/searchGroup")
    public ResponseEntity<?> searchGroupe(@RequestParam String name, @RequestParam String level) {
        Optional<Groups> optionalGroup = groupService.findByNameGroupAndLevel(name, level);

        if (optionalGroup.isPresent()) {
            // Mapper la salle de classe vers un DTO avec les informations minimales
            Groups groups = optionalGroup.get();
            GroupDTO groupDTO = new GroupDTO(
                    groups.getGroup_id(),
                    groups.getNameGroup(),
                    groups.getLevel(),
                    groups.getSchoolYear()
            );

            // Retourner le DTO avec statut OK (200)
            return ResponseEntity.ok(groupDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Le groupes '" + name + "' avec le niveau '" + level + "' n'existe pas");
        }
    }

    @GetMapping("/groups/{groupId}/student")
    public ResponseEntity<List<GroupInfoDTO>> getStudentsByGroupId(@PathVariable Long groupId) {
        List<GroupInfoDTO> studentInfoList = groupService.getStudentInfoByGroupId(groupId);
        return ResponseEntity.ok(studentInfoList);
    }

    @GetMapping("/groups/{groupId}/subjects")
    public ResponseEntity<?> getSubjectsByGroupId(@PathVariable Long groupId) {
        try {
            // Appeler getSubjectInfoByClassRoomId pour obtenir les informations sur les matières
            Stream<SubjectDTO> subjectDTOStream = groupService.getSubjectInfoByGroupId(groupId);

            // Convertir le flux en liste pour retourner dans ResponseEntity
            List<SubjectDTO> subjectInfoList = subjectDTOStream.collect(Collectors.toList());

            // Vérifier si la liste de matières est vide
            if (subjectInfoList.isEmpty()) {
                // Retourner une réponse 404 si aucune matière n'est trouvée pour la classe spécifiée
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("aucune matière n'est trouvée dans le groupe " + groupId);
            } else {
                // Retourner la liste des matières dans ResponseEntity. Ok
                return ResponseEntity.ok(subjectInfoList);
            }
        } catch (RuntimeException e) {
            // Capturer l'exception RuntimeException pour gérer le cas où la classe n'est pas trouvée
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun groupe trouvée avec l'ID : " + groupId);
        }
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<String> updateGroup(@PathVariable Long id, @RequestBody Groups updatedGroup) {

        // Vérifier si le groupe éxisté par son ID
        Optional<Groups> optionalGroup = groupRepository.findById(id);

        if (optionalGroup.isPresent()) {
            Groups existingGroup = optionalGroup.get();

            // Mettre à jour les champs de l'objet existant avec les valeurs de l'objet mis à jour
            existingGroup.setNameGroup(updatedGroup.getNameGroup());
            existingGroup.setLevel(updatedGroup.getLevel());
            existingGroup.setSchoolYear(updatedGroup.getSchoolYear());

            // Sauvegarder les modifications dans le repository
            groupRepository.save(existingGroup);

            return ResponseEntity.ok("Group updated successfully.");
        } else {
            // Si le groupe n'existe pas, retourner une réponse 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
        }
    }

    // Supprimer un groupe par ID
    @DeleteMapping("/groups/delete/{id}")
    public String deleteGroupById(@PathVariable Long id) {
        return groupService.deleteGroupById(id);
    }
}
