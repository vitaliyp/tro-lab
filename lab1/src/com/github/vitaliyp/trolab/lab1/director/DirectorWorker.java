package com.github.vitaliyp.trolab.lab1.director;

import com.github.vitaliyp.tro.lab1.ComputerCommand;
import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DirectorWorker implements Runnable {
	private Semaphore semaphore;
	private final Task task;
	private ComputingNode node;

	public DirectorWorker(Task task, ComputingNode node, Semaphore semaphore) {
		this.semaphore = semaphore;
		this.task = task;
		this.node = node;
	}

	@Override
	public void run() {
		//Open connection
		Socket socket = null;
		try {
			socket = new Socket(node.getAdress(), node.getPort());
			ObjectInputStream objectInputStream =
					new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOutputStream =
					new ObjectOutputStream(socket.getOutputStream());

			//Send compute command
			objectOutputStream.writeObject(ComputerCommand.COMPUTE);
			objectOutputStream.flush();

			//Get numbers of processors on node
			int nProc = objectInputStream.readInt();
			node.setProcessors(nProc);
			semaphore.release();
			try {
				synchronized (task){
					task.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//Send a worker class
			//Send name of class and number of bytes over a network
			objectOutputStream.writeUTF(task.getWorkerClassName());
			objectOutputStream.flush();
			File f = new File(task.getWorkerClassFileName());
			int n = (int)f.length();
			objectOutputStream.writeInt(n);
			objectOutputStream.flush();
			//Send class bytecode across the network
			BufferedInputStream bufferedFileInputStream =
					new BufferedInputStream(new FileInputStream(f));
			int b;
			while((b=bufferedFileInputStream.read())!=-1){
				objectOutputStream.writeByte(b);
			}
			objectOutputStream.flush();

			//Get data from task
			ArrayList<WorkerDataContainer> dataContainers =
					task.giveData(nProc);

			//Send number of data containers
			objectOutputStream.writeInt(dataContainers.size());
			objectOutputStream.flush();

			//Send data containers one by one
			for(WorkerDataContainer container: dataContainers){
				objectOutputStream.writeObject(container);
				objectOutputStream.flush();
			}

			//Retrieve results
			for (int i = 0; i<nProc; i++){
				try {
					task.takeResult((WorkerDataContainer)objectInputStream.readObject());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("Cant connect to host", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
