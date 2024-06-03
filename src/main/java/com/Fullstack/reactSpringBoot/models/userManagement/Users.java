package com.Fullstack.reactSpringBoot.models.userManagement;

import com.Fullstack.reactSpringBoot.models.Auth.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long user_id;

    private String firstName;
    private String lastName;

    @NotNull
    @MatriculeConstraint
    @Column(unique = true)
    private Integer matricule;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(TemporalType.DATE)
    private Date birth_date;
    private String birthPlace;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    private String password;
    private String photo;

    @OneToOne(cascade = CascadeType.ALL)
    private Contacts infoContacts;

    @OneToOne(cascade = CascadeType.ALL)
    private Role role;


}
