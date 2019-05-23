package org.aprestos.labs.spring.microservices.model.transform;


import org.aprestos.labs.spring.microservices.model.entities.Problem;
import org.aprestos.labs.spring.microservices.model.entities.Solution;
import org.aprestos.labs.spring.microservices.model.entities.Task;
import org.aprestos.labs.spring.microservices.model.dto.TaskState;

import java.util.function.Function;

public class Task2entity implements Function<org.aprestos.labs.spring.microservices.model.dto.Task, Task> {

    @Override
    public Task apply(org.aprestos.labs.spring.microservices.model.dto.Task task) {

        Task result = new Task();
        Problem2entity problemMapper = new Problem2entity();
        Problem problem = problemMapper.apply(task.getProblem());
        result.setProblem(problem);

        if( null != task.getSolution() ) {
            Solution solution = new Solution2entity().apply(task.getSolution());
            result.setSolution(solution);
        }

        result.setIdent(task.getId());

        if( null != task.getStatuses() && !task.getStatuses().isEmpty() ){
            TaskState2entity mapper = new TaskState2entity();
            for(TaskState state: task.getStatuses())
                result.getStatuses().add(mapper.apply(state));
        }

        return result;
    }
}
