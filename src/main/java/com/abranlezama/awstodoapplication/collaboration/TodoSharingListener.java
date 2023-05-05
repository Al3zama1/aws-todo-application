package com.abranlezama.awstodoapplication.collaboration;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TodoSharingListener {

    private final MailSender mailSender;
    private final TodoCollaborationService todoCollaborationService;
    private final Environment environment;
    private final boolean autoConfigureCollaborations;


    public TodoSharingListener(
            MailSender mailSender,
            TodoCollaborationService todoCollaborationService,
            Environment environment,
            @Value("${custom.auto-confirm-collaborations}") boolean autoConfigureCollaborations) {
        this.mailSender = mailSender;
        this.todoCollaborationService = todoCollaborationService;
        this.environment = environment;
        this.autoConfigureCollaborations = autoConfigureCollaborations;
    }

    @SqsListener(value = "${custom.sharing-queue}")
    public void listenToSharingMessages(TodoCollaborationNotification payload) {
        log.info("Incoming todo sharing payload: {}", payload);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply.showcasecloudproject.com");
        message.setTo(payload.getCollaboratorEmail());
        message.setSubject("A todo was shared with your");
        message.setText(String.format(
                """
                Hi %s
                someone shared a Todo from https://app.showcasecloudproject.com with you.
                Information about the shared Todo item:
                Title: %s
                Description: %s
                Priority: %s
                You can accept the collaboration by clicking this link
                https://app.showcasecloudproject.com/todo/%s/collaborations/%s/confirm?token=%s
                Kind regards,
                Todo application Team
                """,
                payload.getCollaboratorEmail(),
                payload.getTodoTitle(),
                payload.getTodoDescription(),
                payload.getTodoPriority(),
                payload.getTodoId(),
                payload.getCollaboratorId(),
                payload.getToken()
        ));
        /* TODO: 5/4/23 Make SQS messages idempotent, meaning that collaborators should not get invitation for same todo more than once.
            This can be the case as we are not using FIFO SQS queue and messages are delivered at least once.
         */
        mailSender.send(message);

        log.info("Successfully sent todo collaboration request.");

        if (autoConfigureCollaborations) {
            log.info("Auto-confirmed collaboration request for todo {}", payload.getTodoId());
            todoCollaborationService.confirmCollaboration(
                    payload.getCollaboratorEmail(), payload.getTodoId(),
                    payload.getCollaboratorId(), payload.getToken());
        }
    }

}
