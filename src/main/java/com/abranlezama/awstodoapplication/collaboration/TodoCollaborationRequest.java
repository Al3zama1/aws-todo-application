package com.abranlezama.awstodoapplication.collaboration;

import com.abranlezama.awstodoapplication.Person.Person;
import com.abranlezama.awstodoapplication.todo.Todo;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class TodoCollaborationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "collaborator_id")
    private Person collaborator;

    @ManyToOne
    private Todo todo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoCollaborationRequest that = (TodoCollaborationRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token) &&
                Objects.equals(collaborator, that.collaborator) && Objects.equals(todo, that.todo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, collaborator, todo);
    }
}
