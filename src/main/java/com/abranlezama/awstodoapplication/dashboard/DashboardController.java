package com.abranlezama.awstodoapplication.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String getDashboard(@AuthenticationPrincipal OidcUser user,
                               Model model) {
        model.addAttribute("collaborators", List.of());

        if (user != null) {
            model.addAttribute("collaborators", dashboardService.getAvailableCollaborators(user.getEmail()));
            model.addAttribute("todos", dashboardService.getAllOwnedAndSharedTodos(user.getEmail()));
        }

        return "dashboard";
    }
}
