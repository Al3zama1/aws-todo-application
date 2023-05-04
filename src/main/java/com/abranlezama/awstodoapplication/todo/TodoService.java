package com.abranlezama.awstodoapplication.todo;

import com.abranlezama.awstodoapplication.Person.Person;
import com.abranlezama.awstodoapplication.Person.PersonRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final PersonRepository personRepository;
    private final MeterRegistry meterRegistry;

    public Todo saveNewTodo(Todo todo, String ownerEmail, String ownerName) {
        Person person = personRepository.findByEmail(ownerEmail).orElse(null);

        if (person == null) {
            Person newUser = Person.builder()
                    .name(ownerName)
                    .email(ownerEmail)
                    .build();

            person = personRepository.save(newUser);
        }

        todo.setOwner(person);
        todo.setStatus(Status.OPEN);

        meterRegistry.gauge("application.todo.created", 1);

        return todoRepository.save(todo);
    }

    public void updateTodo(Todo updateTodo, long id, String email) {
        Todo existingTodo = getOwnedOrSharedTodo(id, email);

        existingTodo.setTitle(updateTodo.getTitle());
        existingTodo.setDescription(updateTodo.getDescription());
        existingTodo.setPriority(updateTodo.getPriority());
        existingTodo.setDueDate(updateTodo.getDueDate());

        todoRepository.save(existingTodo);
    }

    public Todo getOwnedOrSharedTodo(long id, String email) {
        Todo todo = this.todoRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (userINotOwner(email, todo) && userIsNotCollaborator(email, todo)) {
            throw new ForbiddenException();
        }

        return todo;
    }

    private boolean userIsNotCollaborator(String email, Todo todo) {
        return todo.getCollaborators().stream()
                .noneMatch(collaborator -> collaborator.getEmail().equals(email));
    }

    private boolean userINotOwner(String email, Todo todo) {
        return !todo.getOwner().getEmail().equals(email);
    }

    private Todo getOwnedTodo(long id, String ownerEmail) {
        Todo todo = this.todoRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (userINotOwner(ownerEmail, todo)) {
            throw new ForbiddenException();
        }

        return todo;
    }
}
