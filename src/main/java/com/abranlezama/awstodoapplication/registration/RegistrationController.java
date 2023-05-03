package com.abranlezama.awstodoapplication.registration;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @GetMapping
    public String getRegisterView(Model model) {
        model.addAttribute("registration", new Registration());
        return "register";
    }

    @PostMapping
    public String registerUser(
            @Valid Registration registration,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registration", registration);
            return "register";
        }

        try {
            registrationService.registerUser(registration);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully registered." +
                    "Please check your email inbox for further instructions.");
            return "redirect:/";
        } catch (AWSCognitoIdentityProviderException exception) {
            model.addAttribute("registration", registration);
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("messageType", "danger");

            return "register";
        }
    }
}
