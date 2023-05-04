package com.abranlezama.awstodoapplication.dashboard;

import com.abranlezama.awstodoapplication.Person.Person;
import com.abranlezama.awstodoapplication.Person.PersonRepository;
import com.abranlezama.awstodoapplication.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final PersonRepository personRepository;
    private final TodoRepository todoRepository;

    public List<CollaboratorDto> getAvailableCollaborators(String email) {
        List<Person> collaborators = personRepository.findByEmailNot(email);

        return collaborators
                .stream()
                .map(person -> new CollaboratorDto(person.getId(), person.getEmail()))
                .collect(Collectors.toList());
    }

    public List<TodoDto> getAllOwnedAndSharedTodos(String email) {
        List<TodoDto> ownedTodo = todoRepository.findAllByOwnerEmailOrderByIdAsc(email)
                .stream()
                .map(todo -> new TodoDto(todo, false))
                .collect(Collectors.toList());

        List<TodoDto> collaborativeTodos = todoRepository.findAllByCollaboratorsEmailOrderByIdAsc(email)
                .stream()
                .map(todo -> new TodoDto(todo, true))
                .toList();

        ownedTodo.addAll(collaborativeTodos);

        return ownedTodo;
    }
}
