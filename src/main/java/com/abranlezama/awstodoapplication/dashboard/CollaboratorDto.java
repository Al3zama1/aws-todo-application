package com.abranlezama.awstodoapplication.dashboard;

import lombok.Builder;

@Builder
public record CollaboratorDto(
        Long id,
        String name
) {
}
