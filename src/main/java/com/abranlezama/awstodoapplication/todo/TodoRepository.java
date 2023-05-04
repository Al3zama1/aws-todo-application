package com.abranlezama.awstodoapplication.todo;

import com.abranlezama.awstodoapplication.Person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByOwnerEmailOrderByIdAsc(String email);

    List<Todo> findAllByCollaboratorsEmailOrderByIdAsc(String email);

    Optional<Todo> findByIdAndOwnerEmail(Long todoId, String todoOwnerEmail);
}
