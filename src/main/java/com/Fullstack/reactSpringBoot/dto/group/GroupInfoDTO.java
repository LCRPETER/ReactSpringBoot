package com.Fullstack.reactSpringBoot.dto.group;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupInfoDTO {
    private Integer matricule;
    private String firstName;
    private String lastName;
    private String schoolYear;
}
