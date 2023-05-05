package com.abranlezama.awstodoapplication.collaboration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoCollaborationController {

    private final TodoCollaborationService todoCollaborationService;

    @PostMapping("/{todoId}/collaborations/{collaboratorId}")
    public String shareTodoWithCollaborator(
            @PathVariable("todoId") Long todoId,
            @PathVariable("collaboratorId") Long collaboratorId,
            @AuthenticationPrincipal OidcUser user,
            RedirectAttributes redirectAttributes) {
        String collaboratorName = todoCollaborationService.shareWithCollaborators(user.getEmail(), todoId, collaboratorId);

        redirectAttributes.addFlashAttribute("message",
                String.format("You successfully share your todo with the user %s." +
                        "Once the user accepts the invitation, you will see them as a collaborator on your todo",
                        collaboratorName));

        return "redirect:/dashboard";
    }

    @GetMapping("/{todoId}/collaborations/{collaboratorId}/confirm")
    public String confirmCollaboration(@PathVariable("todoId") Long todoId,
                                       @PathVariable("collaboratorId") Long collaboratorId,
                                       @RequestParam("token") String token,
                                       @AuthenticationPrincipal OidcUser user,
                                       RedirectAttributes redirectAttributes) {
        if (todoCollaborationService.confirmCollaboration(user.getEmail(), todoId, collaboratorId, token)) {
            redirectAttributes.addFlashAttribute("message", "You've confirmed that you'd like to collaborate on this todo.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Invalid collaboration request.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/dashboard";
    }
}
