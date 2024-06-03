package com.Fullstack.reactSpringBoot.repositories.userManagement;


import com.Fullstack.reactSpringBoot.models.Auth.Role;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByMatricule(int matricule);
    boolean existsByMatricule(int matricule);
    List<Users> findUsersByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    Optional<Users> findUsersByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    List<Users> findByRole(Role role);
    @Modifying
    @Query("delete from Users u where u.user_id = :user_id")
    void deleteUsers(@Param("user_id") long id);
}
