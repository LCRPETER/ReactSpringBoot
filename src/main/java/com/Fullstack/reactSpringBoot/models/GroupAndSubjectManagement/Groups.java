package com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement;

import com.Fullstack.reactSpringBoot.models.userManagement.Students;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Groups implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long group_id;
    private String nameGroup;
    private String level;
    private String schoolYear;
    @OneToMany(mappedBy = "groups", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Students> students = new ArrayList<>();
    @ManyToMany(mappedBy = "groups")
    private List<Subjects> taughtSubjects;

}
