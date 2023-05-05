package com.abranlezama.awstodoapplication.todo;

import com.abranlezama.awstodoapplication.Person.Person;
import com.abranlezama.awstodoapplication.collaboration.TodoCollaborationRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder

public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size
    private String title;

    @Size(max = 100)
    private String description;

    private Priority priority;

    @NotNull
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person owner;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "todo_id")
    private List<Reminder> reminders;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "todo_id")
    private List<Note> notes;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "todo_id")
    private List<TodoCollaborationRequest> collaborationRequests;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "todo_collaboration",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_id")
    )
    private List<Person> collaborators;

    public void addCollaborator(Person person) {
        collaborators.add(person);
        person.getCollaborativeTodos().add(this);
    }

    public void removeCollaborator(Person person) {
        collaborators.remove(person);
        person.getCollaborativeTodos().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return Objects.equals(id, todo.id) && Objects.equals(title, todo.title) &&
                Objects.equals(description, todo.description) && priority == todo.priority &&
                Objects.equals(dueDate, todo.dueDate) && Objects.equals(status, todo.status) &&
                Objects.equals(owner, todo.owner) && Objects.equals(reminders, todo.reminders) &&
                Objects.equals(notes, todo.notes) && Objects.equals(collaborationRequests, todo.collaborationRequests) &&
                Objects.equals(collaborators, todo.collaborators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, priority, dueDate, status, owner, reminders, notes,
                collaborationRequests, collaborators);
    }
}
