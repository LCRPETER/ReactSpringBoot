package com.Fullstack.reactSpringBoot.dto.teacher;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherInfoDTO {
    private Integer matricule;
    private String firstName;
    private String lastName;
    private String nameSubject;
}
