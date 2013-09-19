package com.github.vitaliyp.trolab.lab1.director;

import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.util.ArrayList;

public class Test {
	public static void main(String[] args) {
		Task task = new Lab1Task();
		task.inputData();
		task.partition(2);

		Lab1ComputerWorker worker = new Lab1ComputerWorker();
		ArrayList<WorkerDataContainer> arr = task.giveData(2);
		worker.compute(arr.get(0));
		worker.compute(arr.get(1));
		task.takeResult(arr.get(0));
		task.takeResult(arr.get(1));

		task.outputData();
	}
}
