package org.aprestos.labs.spring.microservices.solver.k;

import org.aprestos.labs.spring.microservices.model.dto.Problem;

public interface Solver {

	String solve(Problem problem);

}