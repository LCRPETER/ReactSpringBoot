package com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement;

import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subjects, Long> {
    @Modifying
    @Query("delete from Subjects s where s.subject_id = :subject_id")
    void deleteSubjects(@Param("subject_id") long id);
}
