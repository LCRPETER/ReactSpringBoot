package com.Fullstack.reactSpringBoot.services.teachers;

import com.Fullstack.reactSpringBoot.models.userManagement.Teachers;
import com.Fullstack.reactSpringBoot.repositories.userManagement.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }


    @Override
    public Optional<Teachers> findTeachersByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName) {
        return teacherRepository.findTeachersByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
    }

    @Override
    public List<Teachers> findTeachersByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName) {
        return teacherRepository.findTeachersByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
    }

    @Transactional
    public String deleteTeachersByUserId(long userId) {
        Optional<Teachers> studentOptional = teacherRepository.findById(userId);
        if (studentOptional.isPresent()) {
            teacherRepository.deleteTeachers(userId);
            return "Enseignant with ID " + userId + " deleted successfully.";
        } else {
            return "Enseignant with ID " + userId + " not found.";
        }
    }

    @Transactional
    public String deleteTeachersByFirstNameAndLastName(String firstName, String lastName) {
        Optional<Teachers> teacherOptional = teacherRepository.findTeachersByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        if (teacherOptional.isPresent()) {
            Teachers student = teacherOptional.get();
            teacherRepository.deleteTeachers(student.getUser_id());
            return "Enseignant " + firstName + " " + lastName + " deleted successfully.";
        } else {
            return "Enseignant " + firstName + " " + lastName + " not found.";
        }
    }

}
