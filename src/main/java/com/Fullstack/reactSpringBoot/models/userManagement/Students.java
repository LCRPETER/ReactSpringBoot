package com.Fullstack.reactSpringBoot.models.userManagement;

import com.Fullstack.reactSpringBoot.models.GroupAndSubjectManagement.Groups;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Students extends Users implements Serializable {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups groups;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parents parent;
}

