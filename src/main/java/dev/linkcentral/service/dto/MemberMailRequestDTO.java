package dev.linkcentral.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberMailRequestDTO {
    private String address;
    private String title;
    private String message;
}
