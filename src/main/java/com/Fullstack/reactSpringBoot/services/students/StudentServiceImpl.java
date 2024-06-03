package com.Fullstack.reactSpringBoot.services.students;

import com.Fullstack.reactSpringBoot.models.userManagement.Students;
import com.Fullstack.reactSpringBoot.repositories.userManagement.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentsRepository studentsRepository;
    @Autowired
    public StudentServiceImpl(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }


    @Override
    public Optional<Students> findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName) {
        return studentsRepository.findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
    }

    @Override
    public List<Students> findStudentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName) {
        return studentsRepository.findStudentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
    }

    @Override
    public Optional<Students> findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGroupsLevel(String firstName, String lastName, String studentGroupeLevel) {
        return studentsRepository.findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGroupsLevel(firstName, lastName, studentGroupeLevel);
    }

    @Override
    @Transactional
    public String deleteStudentsByUserId(long userId) {
        Optional<Students> studentOptional = studentsRepository.findById(userId);
        if (studentOptional.isPresent()) {
            studentsRepository.deleteStudents(userId);
            return "Étudiant avec l'ID " + userId + " supprimé avec succès.";
        } else {
            return "Étudiant avec l'ID " + userId + " introuvable.";
        }
    }

    @Transactional
    public String deleteStudentsByFirstNameAndLastName(String firstName, String lastName) {
        Optional<Students> studentOptional = studentsRepository.findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        if (studentOptional.isPresent()) {
            Students student = studentOptional.get();
            studentsRepository.deleteStudents(student.getUser_id());
            return "Étudiant " + firstName + " " + lastName + " supprimé avec succès.";
        } else {
            return "Étudiant " + firstName + " " + lastName + " introuvable.";
        }
    }

}
