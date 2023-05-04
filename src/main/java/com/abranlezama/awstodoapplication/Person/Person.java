package com.abranlezama.awstodoapplication.Person;

import com.abranlezama.awstodoapplication.collaboration.TodoCollaborationRequest;
import com.abranlezama.awstodoapplication.todo.Todo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @NotEmpty
    @Column(unique = true)
    private String email;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner")
    private List<Todo> ownedTodos = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "collaborators")
    private List<Todo> collaborativeTodos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collaborator")
    private List<TodoCollaborationRequest> collaborationRequests = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) && Objects.equals(name, person.name) &&
                Objects.equals(email, person.email) && Objects.equals(ownedTodos, person.ownedTodos) &&
                Objects.equals(collaborativeTodos, person.collaborativeTodos) &&
                Objects.equals(collaborationRequests, person.collaborationRequests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, ownedTodos, collaborativeTodos, collaborationRequests);
    }
}
