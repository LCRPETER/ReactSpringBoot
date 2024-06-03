package com.Fullstack.reactSpringBoot.controllers.Student;


import com.Fullstack.reactSpringBoot.controllers.Admin.userManagement.studentsManagement.StudentController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class InfoStudentController {

    private final StudentController studentController;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id){
        return studentController.getStudentById(id);
    }
    @GetMapping("/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchStudentsByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName){
        return studentController.searchStudentsByFirstNameAndLastName(firstName, lastName);
    }
}
