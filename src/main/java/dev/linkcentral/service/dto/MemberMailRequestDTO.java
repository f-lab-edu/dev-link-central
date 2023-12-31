package dev.linkcentral.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberMailRequestDTO {

    private String address;
    private String title;
    private String message;
}