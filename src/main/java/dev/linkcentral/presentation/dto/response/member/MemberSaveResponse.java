package dev.linkcentral.presentation.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveResponse {

    private Long id;
    private String message;
}