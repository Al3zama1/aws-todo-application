package com.abranlezama.awstodoapplication.todo;

import com.abranlezama.awstodoapplication.Person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByOwner(Person person);

    List<Todo> findAllByOwnerEmail(String email);

    List<Todo> findAllByOwnerEmailOrderByIdAsc(String email);
}
