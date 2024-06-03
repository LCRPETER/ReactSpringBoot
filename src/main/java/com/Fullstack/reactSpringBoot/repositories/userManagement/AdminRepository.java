package com.Fullstack.reactSpringBoot.repositories.userManagement;

import com.Fullstack.reactSpringBoot.models.userManagement.Admin;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Users> findByMatricule(int matricule);

}
