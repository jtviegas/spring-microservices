package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.dto.TaskState;

import java.util.function.Function;

public class TaskState2Dto implements Function<org.aprestos.labs.spring.microservices.model.entities.TaskState, TaskState> {


    @Override
    public TaskState apply(org.aprestos.labs.spring.microservices.model.entities.TaskState state) {
        return new TaskState(state.getIdent(), state.getTimestamp(), state.getStatus());
    }
}
