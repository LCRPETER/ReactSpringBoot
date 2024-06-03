package com.Fullstack.reactSpringBoot.repositories.userManagement;

import com.Fullstack.reactSpringBoot.models.userManagement.Teachers;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teachers, Long> {
    Optional<Users> findByMatricule(int matricule);
    List<Teachers> findTeachersByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
    Optional<Teachers> findTeachersByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    @Modifying
    @Query("delete from Teachers p where p.user_id = :user_id")
    void deleteTeachers(@Param("user_id") long id);
}
