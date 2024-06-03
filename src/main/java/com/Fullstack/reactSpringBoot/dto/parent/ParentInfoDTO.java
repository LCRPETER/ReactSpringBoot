package com.Fullstack.reactSpringBoot.dto.parent;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentInfoDTO {
    private Integer matricule;
    private String firstName;
    private String lastName;
}
