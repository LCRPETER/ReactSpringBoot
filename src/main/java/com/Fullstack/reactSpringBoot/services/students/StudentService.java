package com.Fullstack.reactSpringBoot.services.students;

import com.Fullstack.reactSpringBoot.models.userManagement.Students;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface StudentService extends Serializable {
    Optional<Students> findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    List<Students> findStudentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    Optional<Students> findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGroupsLevel(String firstName, String lastName, String studentClassLevel);
    String deleteStudentsByUserId(long noteId);
}

