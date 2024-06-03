package com.Fullstack.reactSpringBoot.services.groupe;

import com.Fullstack.reactSpringBoot.dto.group.GroupInfoDTO;
import com.Fullstack.reactSpringBoot.dto.subject.SubjectDTO;
import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface GroupService {
    Optional<Groups> findByNameGroupAndLevel(String nameGroup, String level);
    Groups findByLevelAndSchoolYear(String level, String schoolYear);
    List<GroupInfoDTO> getStudentInfoByGroupId(Long groupId);
    Stream<SubjectDTO> getSubjectInfoByGroupId(Long groupId);
    String deleteGroupById(long noteId);
    Optional<Groups> findByLevel(String level);

}
