package org.aprestos.labs.spring.microservices.solver.k;

import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.apis.springboot2.statemanager.StateManager;
import org.aprestos.labs.apis.springboot2.statemanager.StateManagerException;
import org.aprestos.labs.spring.microservices.model.dto.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SolverTask implements Runnable {

	private final StateManager<String, Task, TaskStatus> stateManager;
	private final Task task;

	public SolverTask(final StateManager<String,Task, TaskStatus> stateManager, final Task task) {
		log.trace("[()|in] task: {}", task);
		this.stateManager = stateManager;
		this.task = task;
		log.trace("[()|out]");
	}

	@Override
	public void run() {
		log.trace("[run|in]");
		try {
			solve(task);
		} catch (Exception e) {
			log.error("[run]", e);
		} finally {
			log.trace("[run|out]");
		}
	}

	public Solution solve(Task task) throws StateManagerException {
		log.trace("[solve|in]({})", task);

		task.getStatuses().add(new TaskState(System.currentTimeMillis(), TaskStatus.submitted));
		stateManager.notify(task);
		log.info("[run] going to solve task: {}", task.getId());

		Item[] items = task.getProblem().getItems();
		int capacity = task.getProblem().getCapacity();
		// we use a matrix to store the max value at each n-th item
		int[][] matrix = new int[items.length + 1][capacity + 1];

		// first line is initialized to 0
		for (int i = 0; i <= capacity; i++)
			matrix[0][i] = 0;

		final long start = System.currentTimeMillis();
		task.getStatuses().add(new TaskState(start, TaskStatus.started));
		stateManager.notify(task);

		// we iterate on items
		for (int i = 1; i <= items.length; i++) {
			// we iterate on each capacity
			for (int j = 0; j <= capacity; j++) {
				if (items[i - 1].getWeight() > j)
					matrix[i][j] = matrix[i-1][j];
				else
					// we maximize value at this rank in the matrix
					matrix[i][j] = Math.max(matrix[i-1][j], matrix[i-1][j - items[i-1].getWeight()]
							+ items[i-1].getValue());
			}
		}

		int res = matrix[items.length][capacity];
		int w = capacity;
		List<Item> itemsSolution = new ArrayList<>();

		for (int i = items.length; i > 0  &&  res > 0; i--) {
			if (res != matrix[i-1][w]) {
				itemsSolution.add(items[i-1]);
				// we remove items value and weight
				res -= items[i-1].getValue();
				w -= items[i-1].getWeight();
			}
		}

		final long end = System.currentTimeMillis();
		final long time = (end - start);
		final Solution solution = new Solution(itemsSolution.toArray(new Item[itemsSolution.size()]), time, matrix[items.length][capacity]);
		log.info("[run] task:{} solution took {}ms and is: {}", task.getId(), time, solution);
		task.setSolution(solution);
		task.getStatuses().add(new TaskState(end, TaskStatus.completed));
		stateManager.notify(task);
		log.trace("[solve|out] => {}", solution);
		return solution;
	}

}
