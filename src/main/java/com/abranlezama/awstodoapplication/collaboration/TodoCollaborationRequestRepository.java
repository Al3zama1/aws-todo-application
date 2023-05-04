package com.abranlezama.awstodoapplication.collaboration;

import com.abranlezama.awstodoapplication.Person.Person;
import com.abranlezama.awstodoapplication.todo.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoCollaborationRequestRepository extends CrudRepository<TodoCollaborationRequest, Long> {
    TodoCollaborationRequest findByTodoAndCollaborator(Todo todo, Person person);
    TodoCollaborationRequest findByTodoIdAndCollaboratorId(Long todoId, Long collaboratorId);
}
