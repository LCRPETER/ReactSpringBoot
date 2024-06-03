package com.Fullstack.reactSpringBoot.services.users;


import com.Fullstack.reactSpringBoot.models.userManagement.Users;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface UserService extends Serializable {
    Optional<Users> findUsersByMatricule(int matricule);
    List<Users> findUsersByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    Optional<Users> findUsersByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
//    List<Object> getUsersByRole(Role role);
    String deleteUsersByUserId(long userId);
    String deleteUsersByFirstNameAndLastName(String firstName, String lastName);

}
