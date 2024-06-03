package com.Fullstack.reactSpringBoot.repositories.userManagement;

import com.Fullstack.reactSpringBoot.models.userManagement.Students;
import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StudentsRepository extends JpaRepository<Students, Long> {
     Optional<Users> findByMatricule(int matricule);
     List<Students> findStudentsByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);
     Optional<Students> findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
     Optional<Students> findStudentsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGroupsLevel(String firstName, String lastName, String studentClassLevel);

     @Modifying
     @Query("delete from Students s where s.user_id = :user_id")
     void deleteStudents(@Param("user_id") long id);
}
