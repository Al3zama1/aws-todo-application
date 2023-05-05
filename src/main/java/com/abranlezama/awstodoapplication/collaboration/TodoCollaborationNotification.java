package com.abranlezama.awstodoapplication.collaboration;

import com.abranlezama.awstodoapplication.todo.Priority;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodoCollaborationNotification {
    private String collaboratorEmail;
    private String collaboratorName;
    private Long collaboratorId;

    private String todoTitle;
    private String todoDescription;
    private Priority todoPriority;
    private Long todoId;

    private String token;

    public TodoCollaborationNotification(TodoCollaborationRequest todoCollaborationRequest) {
        this.collaboratorEmail = todoCollaborationRequest.getCollaborator().getEmail();
        this.collaboratorName = todoCollaborationRequest.getCollaborator().getName();
        this.collaboratorId = todoCollaborationRequest.getCollaborator().getId();
        this.todoTitle = todoCollaborationRequest.getTodo().getTitle();
        this.todoDescription = todoCollaborationRequest.getTodo().getDescription();
        this.todoId = todoCollaborationRequest.getTodo().getId();
        this.todoPriority = todoCollaborationRequest.getTodo().getPriority();
        this.token = todoCollaborationRequest.getToken();
    }
}
