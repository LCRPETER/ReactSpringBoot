package com.Fullstack.reactSpringBoot.repositories.userManagement;

import com.Fullstack.reactSpringBoot.models.userManagement.Parents;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentsRepository extends JpaRepository<Parents, Long> {
    Optional<Users> findByMatricule(int matricule);
    List<Parents> findParentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    Optional<Parents> findParentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    @Modifying
    @Query("delete from Parents p where p.user_id = :user_id")
    void deleteParents(@Param("user_id") long id);
}
