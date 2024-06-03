package com.Fullstack.reactSpringBoot.controllers.Admin.userManagement;

import com.Fullstack.reactSpringBoot.dto.user.UserInfoDTO;
import com.Fullstack.reactSpringBoot.dto.user.UsersDTO;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import com.Fullstack.reactSpringBoot.repositories.userManagement.UsersRepository;
import com.Fullstack.reactSpringBoot.services.users.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UsersRepository usersRepository;
    private final UserServiceImpl userService;


    // Mapper un objet Users à un objet DTO
    private UserInfoDTO mapUserToDTO(Users user) {
        return new UserInfoDTO(
                user.getMatricule(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName(),
                user.getGender(),
                user.getBirth_date(),
                user.getBirthPlace(),
                user.getAddress().getCity(),
                user.getAddress().getStreet(),
                user.getAddress().getZipCode(),
                user.getPhoto(),
                user.getInfoContacts().getEmail(),
                user.getInfoContacts().getPhoneNumber()
        );
    }

    // Mapper un objet UsersDTO (pour informations minimales)
    private UsersDTO mapUsersToDTO(Users user) {
        return new UsersDTO(
                user.getMatricule(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName()
        );
    }

    // Obtenir tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<Users> allUsers = usersRepository.findAll();
        if (allUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun utilisateur trouvé.");
        }

        List<UsersDTO> usersDTOList = allUsers.stream()
                .map(this::mapUsersToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersDTOList);
    }

    // Obtenir un utilisateur par ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<Users> userOptional = usersRepository.findById(id);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            UserInfoDTO userDTO = mapUserToDTO(user);

            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé avec l'ID : " + id);
        }
    }

//    @GetMapping("/users/role/{role}")
//    public ResponseEntity<List<Object>> getUsersByRole(@PathVariable Role role) {
//        List<Object> users = userService.getUsersByRole(role);
//        if (users.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.ok(users);
//        }
//    }

    // Rechercher des utilisateurs par prénom ou nom de famille
    @GetMapping("/users/searchByFirstNameOrLastName")
    public ResponseEntity<?> searchUsersByFirstNameOrLastName(@RequestParam String firstName, @RequestParam String lastName) {
        List<Users> usersList = userService.findUsersByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName);
        if (usersList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun utilisateur trouvé.");
        }

        List<UsersDTO> usersDTOList = usersList.stream()
                .map(this::mapUsersToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersDTOList);
    }

    // Rechercher un utilisateur par prénom et nom de famille
    @GetMapping("/users/searchByFirstNameAndLastName")
    public ResponseEntity<?> searchUsersByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        Optional<Users> userOptional = userService.findUsersByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            UserInfoDTO userDTO = mapUserToDTO(user);

            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur " + firstName + " " + lastName + " n'existe pas.");
        }
    }

    // Supprimer un utilisateur par ID
    @DeleteMapping("/users/delete/{id}")
    public String deleteUserById(@PathVariable Long id) {
        return userService.deleteUsersByUserId(id);
    }

    // Supprimer des utilisateurs par prénom et nom de famille
    @DeleteMapping("/users/deleteByFirstNameAndLastName")
    public String deleteUsersByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return userService.deleteUsersByFirstNameAndLastName(firstName, lastName);
    }
}
