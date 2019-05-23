package org.aprestos.labs.spring.microservices.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TaskState implements Serializable {

    private Long id;
    @NotNull
    private Long timestamp;
    @NotNull
    private TaskStatus status;

    public TaskState(){}

    public TaskState(Long id, Long timestamp, TaskStatus status){
        this.id = id;
        this.timestamp = timestamp;
        this.status = status;
    }

    public TaskState(Long timestamp, TaskStatus status){
        this.timestamp = timestamp;
        this.status = status;
    }

}
