package dev.linkcentral.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupIdsDTO {

    private List<Long> studyGroupIds;

}
