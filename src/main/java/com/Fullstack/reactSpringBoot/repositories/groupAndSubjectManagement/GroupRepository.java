package com.Fullstack.reactSpringBoot.repositories.groupAndSubjectManagement;

import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {
    Groups findByLevelAndSchoolYear(String level, String schoolYear);
    Optional<Groups> findByNameGroupAndLevel(String nameGroup, String level);
    @Modifying
    @Query("delete from Groups g where g.group_id = :group_id")
    void deleteGroup(@Param("group_id") long id);

    Optional<Groups> findByLevel(String level);
}
