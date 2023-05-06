package com.abranlezama.awstodoapplication.tracing;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class TracingEvent extends ApplicationEvent {

    private final String uri;
    private final String username;

    public TracingEvent(Object source, String uri, String username) {
        super(source);
        this.uri = uri;
        this.username = username;
    }

    public String getUri() {
        return uri;
    }

    public String getUsername() {
        return username;
    }
}
