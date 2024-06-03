package com.Fullstack.reactSpringBoot.services.teachers;


import com.Fullstack.reactSpringBoot.models.userManagement.Teachers;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface TeacherService extends Serializable {
    Optional<Teachers> findTeachersByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    List<Teachers> findTeachersByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);

}
