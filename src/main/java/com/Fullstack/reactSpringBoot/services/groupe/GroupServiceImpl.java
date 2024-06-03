package com.Fullstack.reactSpringBoot.services.groupe;

import com.Fullstack.reactSpringBoot.dto.group.GroupInfoDTO;
import com.Fullstack.reactSpringBoot.dto.subject.SubjectDTO;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Subjects;
import com.Fullstack.reactSpringBoot.models.userManagement.Students;
import com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Optional<Groups> findByNameGroupAndLevel(String nameGroupe, String level) {
        return groupRepository.findByNameGroupAndLevel(nameGroupe, level);
    }

    @Override
    public Groups findByLevelAndSchoolYear(String level, String schoolYear) {
        return groupRepository.findByLevelAndSchoolYear(level, schoolYear);
    }

    @Override
    public List<GroupInfoDTO> getStudentInfoByGroupId(Long group_id) {
        Optional<Groups> optionalGroup = groupRepository.findById(group_id);

        if (optionalGroup.isPresent()) {
            Groups groups = optionalGroup.get();
            List<Students> students = groups.getStudents();

            return students.stream()
                    .map(student -> GroupInfoDTO.builder()
                            .matricule(student.getMatricule())
                            .firstName(student.getFirstName())
                            .lastName(student.getLastName())
                            .schoolYear(student.getGroups().getSchoolYear())
                            .build())
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("La classe" + group_id +" n'existe pas");
        }
    }

    @Override
    public Stream<SubjectDTO> getSubjectInfoByGroupId(Long group_id) {
        Optional<Groups> optionalGroup = groupRepository.findById(group_id);

        if (optionalGroup.isPresent()) {
            Groups groups = optionalGroup.get();
            List<Subjects> subjects = groups.getTaughtSubjects();

            return subjects.stream()
                    .map(subject -> SubjectDTO.builder()
                            .subject_id(subject.getSubject_id())
                            .nameSubject(subject.getNameSubject())
                            .description(subject.getDescription())
                            .build());
        } else {
            // Gérer le cas où la classe avec l'ID spécifié n'est pas trouvée
            throw new RuntimeException("Aucune classe trouvée avec l'ID : " + group_id);
        }
    }

    @Override
    @Transactional
    public String deleteGroupById(long groupId) {
        Optional<Groups> groupeOptional = groupRepository.findById(groupId);
        if (groupeOptional.isPresent()) {
            groupRepository.deleteGroup(groupId);
            return "Groupe avec l'ID " + groupId + " supprimé avec succès.";
        } else {
            return "Groupe avec l'ID " + groupId + " n'existe pas.";
        }
    }

    @Override
    public Optional<Groups> findByLevel(String level) {
        return groupRepository.findByLevel(level);
    }
}
