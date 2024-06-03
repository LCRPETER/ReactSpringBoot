package com.Fullstack.reactSpringBoot.dto.subject;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectInfoDTO {
    private Long subject_id;
    private String nameSubject;
    private String description;
    private String nameGroupe;
    private String teacherFullName;
}
