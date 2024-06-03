package com.Fullstack.reactSpringBoot.dto.student;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInfoDTO {
    private Integer matricule;
    private String firstName;
    private String lastName;
    private String nameClassroom;
    private String level;
}
