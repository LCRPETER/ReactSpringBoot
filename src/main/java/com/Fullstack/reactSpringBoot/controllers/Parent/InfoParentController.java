package com.Fullstack.reactSpringBoot.controllers.Parent;


import com.Fullstack.reactSpringBoot.controllers.Admin.userManagement.parentsManagement.ParentController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/parent")
@PreAuthorize("hasRole('PARENT')")
public class InfoParentController {

    private final ParentController parentController;

    @GetMapping("/{id}")
    public ResponseEntity<?> getParentById(@PathVariable Long id){
        return parentController.getParentById(id);
    }
    @GetMapping("/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchParentsByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName){
        return parentController.searchParentsByFirstNameAndLastName(firstName, lastName);
    }
}
