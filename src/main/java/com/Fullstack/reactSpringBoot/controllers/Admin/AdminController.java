package com.Fullstack.reactSpringBoot.controllers.Admin;


import com.Fullstack.reactSpringBoot.models.Auth.ERole;
import com.Fullstack.reactSpringBoot.models.userManagement.Admin;
import com.Fullstack.reactSpringBoot.repositories.userManagement.AdminRepository;
import com.Fullstack.reactSpringBoot.services.users.UserServiceImpl;
import com.Fullstack.reactSpringBoot.services.validation.ValidationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserServiceImpl userService;
    private final ValidationService validationService;

    @PostMapping(path = "/createAdmin")
    public ResponseEntity<String> createAdmin(@RequestBody Admin admin) {
        log.info("Création de l'admin: {}", admin.getMatricule());
        int matricule = userService.generateUniqueMatricule();
        admin.setMatricule(matricule);
        validationService.checkMatriculeAvailability(admin.getMatricule());
        validationService.validateEmail(admin.getInfoContacts().getEmail());
        try {

            if (!ERole.ADMIN.equals(admin.getRole().getName())) {
                return ResponseEntity.badRequest().body("Erreur: Le rôle doit être ADMIN");
            }

            String encryptedPassword = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(encryptedPassword);
            Admin createdAdmin = adminRepository.save(admin);
            return ResponseEntity.ok("Admin créé avec succès: " + createdAdmin.getMatricule());
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de l'admin: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
