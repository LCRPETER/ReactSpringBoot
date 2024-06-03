package com.Fullstack.reactSpringBoot.dto.user;


import com.Fullstack.reactSpringBoot.models.Auth.ERole;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDTO {
    private Integer matricule;
    private String firstName;
    private String lastName;
    private ERole name;
}
