package com.Fullstack.reactSpringBoot.models.userManagement;

import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Subjects;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@NoArgsConstructor
public class Teachers extends Users implements Serializable {
    @OneToMany(mappedBy = "headTeacher", fetch = FetchType.LAZY)
    private List<Subjects> taughtSubjects = new ArrayList<>();
}
