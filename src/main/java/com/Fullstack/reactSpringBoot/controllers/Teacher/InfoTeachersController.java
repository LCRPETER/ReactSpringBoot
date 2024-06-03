package com.Fullstack.reactSpringBoot.controllers.Teacher;


import com.Fullstack.reactSpringBoot.controllers.Admin.userManagement.teachersManagement.TeacherController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class InfoTeachersController {

    private final TeacherController teacherController;


    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Long id){
        return teacherController.getStudentById(id);
    }
    @GetMapping("/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchTeacherByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName){
        return teacherController.searchTeachersByFirstNameAndLastName(firstName, lastName);
    }
}
