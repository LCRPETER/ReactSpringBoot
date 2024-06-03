package com.Fullstack.reactSpringBoot.dto.group;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
    private long Group_Id;
    private String nameGroup;
    private String level;
    private String schoolYear;
}
