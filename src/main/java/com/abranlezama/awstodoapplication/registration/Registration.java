package com.abranlezama.awstodoapplication.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {
    @NotBlank
    private String username;

    @Email
    private String email;

    @ValidInvitationCode
    private String invitationCode;
}
