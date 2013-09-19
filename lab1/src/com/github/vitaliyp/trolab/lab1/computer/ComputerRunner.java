package com.github.vitaliyp.trolab.lab1.computer;

import com.github.vitaliyp.trolab.lab1.commons.ComputerWorker;
import com.github.vitaliyp.trolab.lab1.commons.ContainerStatus;
import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ComputerRunner implements Runnable {
	final private ObjectOutputStream stream;
	private WorkerDataContainer container;
	private Class<ComputerWorker> workerClass;

	public ComputerRunner(ObjectOutputStream stream, WorkerDataContainer container,
	                      Class<ComputerWorker> workerClass){
		this.stream = stream;
		this.container = container;
		this.workerClass = workerClass;
	}

	private synchronized void sendDataBack(){
		container.status = ContainerStatus.COMPLETED;
		try {
			synchronized (stream){
				stream.writeObject(container);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		//Instantiate worker
		try {
			ComputerWorker worker = workerClass.newInstance();
			worker.compute(container);
			sendDataBack();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
