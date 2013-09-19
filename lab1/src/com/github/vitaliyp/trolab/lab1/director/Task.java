package com.github.vitaliyp.trolab.lab1.director;

import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.util.ArrayList;

public abstract class Task {
	private String workerClassName;
	private String workerClassFileName;
	protected int nProc;

	public abstract void inputData();
	public abstract ArrayList<WorkerDataContainer> giveData(int n);
	public abstract void takeResult(WorkerDataContainer container);
	public abstract void outputData();

	protected Task(String workerClassName, String workerClassFileName) {
		this.workerClassName = workerClassName;
		this.workerClassFileName = workerClassFileName;
	}

	public String getWorkerClassName(){
		return workerClassName;
	}

	public String getWorkerClassFileName(){
		return workerClassFileName;
	}

	public void partition(int nProc) {
		this.nProc = nProc;
	}
}
