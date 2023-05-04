package com.abranlezama.awstodoapplication.todo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Priority {
    HIGH(1),
    DEFAULT(2),
    LOW(3);

    private final int displayValue;
}
