package com.abranlezama.awstodoapplication.dashboard;

import com.abranlezama.awstodoapplication.todo.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {

    private Long id;
    private String title;
    private Integer amountOfCollaborators;
    private Integer amountOfCollaborationRequests;
    private LocalDate dueDate;
    private boolean isCollaboration;

   public TodoDto(Todo todo, boolean isCollaboration) {
       this.id = todo.getId();
       this.title = todo.getTitle();
       this.amountOfCollaborationRequests = todo.getCollaborationRequests().size();
       this.amountOfCollaborators = todo.getCollaborators().size();
       this.dueDate = todo.getDueDate();
       this.isCollaboration = isCollaboration;

   }
}
