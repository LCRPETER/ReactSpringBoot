package com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement;

import com.Fullstack.reactSpringBoot.models.userManagement.Teachers;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Subjects implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subject_id;
    private String nameSubject;
    private String description;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teachers headTeacher;
    @ManyToMany
    private List<Groups> groups;

}
