package com.github.vitaliyp.trolab.lab1.computer;

import com.github.vitaliyp.trolab.lab1.commons.ComputerCommand;
import com.github.vitaliyp.trolab.lab1.commons.ComputerWorker;
import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Computer {
	public static void main(String[] args) {
		//Parse arguments
		int port = 1460;
		if(args.length!=0){
			port = Integer.parseInt(args[0]);
		}
		//Create server socket
		ServerSocket ssock = null;
		try {
			ssock = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException("Can't create server socket.", e);
		}

		boolean work = true;
		while(work){
			//Accepting connection and creating streams
			Socket csock = null;

			try {
				csock = ssock.accept();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			ObjectOutputStream objectOutputStream = null;
			ObjectInputStream objectInputStream = null;
			try {
				objectOutputStream = new ObjectOutputStream(csock.getOutputStream());
				objectInputStream =
						new ObjectInputStream(csock.getInputStream());

				//Read and process command
				try {
					ComputerCommand command = (ComputerCommand)objectInputStream.readObject();
					switch(command){
						case SHUTDOWN:
							work = false;
							System.out.println("Shutting the server down.");
							continue;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}

				//Send number of available processors
				objectOutputStream.writeInt(Runtime.getRuntime().availableProcessors());
				objectOutputStream.flush();

				//Read class name
				String workerClassName = objectInputStream.readUTF();
				//Receive worker class from com.github.vitaliyp.trolab.lab1.director
				ClassLoader inputStreamClassLoader = new InputStreamClassLoader(objectInputStream);
				Class<ComputerWorker> workerClass = null;
				try {
					workerClass = (Class<ComputerWorker>) inputStreamClassLoader.loadClass(workerClassName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}

				//Read number of data containers from com.github.vitaliyp.trolab.lab1.director
				//And read containers too
				int k = objectInputStream.readInt();
				final ArrayList<WorkerDataContainer> containers =
						new ArrayList<WorkerDataContainer>(k);
				for(int i = 0; i<k; i++){
					Object o = null;
					try {
						o = objectInputStream.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						continue;
					}
					if(o instanceof WorkerDataContainer){
						WorkerDataContainer container = (WorkerDataContainer)o;
						containers.add(container);
					}
				}

				//Create threads pool
				ExecutorService executorService = Executors.newFixedThreadPool(
						Runtime.getRuntime().availableProcessors());

				//
				for(WorkerDataContainer container: containers){
					executorService.execute(
							new ComputerRunner(objectOutputStream, container, workerClass));
				}

				executorService.shutdown();

					try {
						executorService.awaitTermination(1, TimeUnit.MINUTES);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}catch (IOException e){
				e.printStackTrace();
			} finally {
				try {
					csock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			ssock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
