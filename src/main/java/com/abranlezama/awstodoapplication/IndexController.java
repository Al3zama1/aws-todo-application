package com.abranlezama.awstodoapplication;

import com.abranlezama.awstodoapplication.tracing.TracingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    public String getIndex(Principal principal) {
        this.eventPublisher.publishEvent(
                new TracingEvent(this, "index", principal != null ? principal.getName() : "anonymous")
        );

        return "index";
    }
}
