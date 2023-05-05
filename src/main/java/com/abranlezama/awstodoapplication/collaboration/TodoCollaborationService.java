package com.abranlezama.awstodoapplication.collaboration;

import com.abranlezama.awstodoapplication.Person.Person;
import com.abranlezama.awstodoapplication.Person.PersonRepository;
import com.abranlezama.awstodoapplication.todo.Todo;
import com.abranlezama.awstodoapplication.todo.TodoRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
public class TodoCollaborationService {
    private final TodoRepository todoRepository;
    private final PersonRepository personRepository;
    private final TodoCollaborationRequestRepository todoCollaborationRequestRepository;
    private final ObjectMapper objectMapper;

    private final SqsTemplate sqsTemplate;
    private final String todoSharingQueueName;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String INVALID_TODO_ID = "Invalid todo ID: ";
    private static final String INVALID_PERSON_ID = "Invalid person ID: ";
    private static final String INVALID_PERSON_EMAIL = "Invalid person Email: ";

    public TodoCollaborationService(
            @Value("${custom.sharing-queue}") String todoSharingQueueName,
            TodoRepository todoRepository,
            PersonRepository personRepository,
            TodoCollaborationRequestRepository todoCollaborationRequestRepository,
            SqsTemplate sqsTemplate,
            SimpMessagingTemplate simpMessagingTemplate,
            ObjectMapper objectMapper) {
        this.todoRepository = todoRepository;
        this.personRepository = personRepository;
        this.todoCollaborationRequestRepository = todoCollaborationRequestRepository;
        this.sqsTemplate = sqsTemplate;
        this.todoSharingQueueName = todoSharingQueueName;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    public String shareWithCollaborators(String todoOwnerEmail, Long todoId, Long collaboratorId) {
        Todo todo = todoRepository.findByIdAndOwnerEmail(todoId, todoOwnerEmail)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + todoId));

        Person collaborator = personRepository.findById(collaboratorId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_PERSON_ID + collaboratorId));

        if (todoCollaborationRequestRepository.findByTodoAndCollaborator(todo, collaborator) != null) {
            log.info("Collaboration request for todo {} with collaborator {} already exists", todoId, collaboratorId);
            return collaborator.getName();
        }

        log.info("About to share todo with id {} with collaborator {}", todoId, collaboratorId);

        TodoCollaborationRequest collaboration = new TodoCollaborationRequest();
        String token = UUID.randomUUID().toString();
        collaboration.setToken(token);
        collaboration.setCollaborator(collaborator);
        collaboration.setTodo(todo);
        todo.getCollaborationRequests().add(collaboration);

        todoCollaborationRequestRepository.save(collaboration);

        TodoCollaborationNotification notification = new TodoCollaborationNotification(collaboration);

        String json = "";

        try {
            json = objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        sqsTemplate.send(todoSharingQueueName, json);

        return collaborator.getName();
    }

    public boolean confirmCollaboration(String authenticatedUserEmail, Long todoId, Long collaboratorId, String token) {
        Person collaborator = personRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_PERSON_EMAIL + authenticatedUserEmail));

        if (!collaborator.getId().equals(collaboratorId)) return false;

        TodoCollaborationRequest collaborationRequest = todoCollaborationRequestRepository
                .findByTodoIdAndCollaboratorId(todoId, collaboratorId);

        log.info("Collaboration request: {}", collaborationRequest);

        if (collaborationRequest == null || !collaborationRequest.getToken().equals(token)) return false;

        log.info("Original collaboration token: {}", collaborationRequest.getToken());;
        log.info("Request token: {}", token);

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_TODO_ID + todoId));

        todo.addCollaborator(collaborator);

        todoCollaborationRequestRepository.delete(collaborationRequest);

        String name = collaborationRequest.getCollaborator().getName();
        String subject = "Collaboration confirmed.";
        String message = "User " + name + " has accepted your collaboration request for todo #"
                + collaborationRequest.getTodo().getId() + ".";
        String ownerEmail = collaborationRequest.getTodo().getOwner().getEmail();

        simpMessagingTemplate.convertAndSend("/topic/todoUpdates/" + ownerEmail, subject + " " + message);

        return true;
    }

}
