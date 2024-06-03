package com.Fullstack.reactSpringBoot.dto.student;


import com.Fullstack.reactSpringBoot.models.userManagement.Gender;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    private Integer matricule;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date birth_date;
    private String birthplace;
    private String city;
    private String street;
    private int zipCode;
    private String nameGroupe;
    private String level;
    private String photo;
    private String email;
    private int phoneNumber;
}
