package org.aprestos.labs.spring.microservices.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Task implements Serializable  {

    @NotNull
    private String id;
    private List<TaskState> statuses = new ArrayList<>();
    @NotNull
    private Problem problem;
    private Solution solution;

    public Task(){
        this.id = UUID.randomUUID().toString();
    }
    public Task(final Problem problem){
        this();
        this.problem = problem;

    }

}
