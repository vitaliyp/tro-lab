package com.github.vitaliyp.trolab.lab1.director;

import com.github.vitaliyp.tro.lab1.ComputerCommand;
import com.github.vitaliyp.trolab.lab1.commons.ContainerStatus;
import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Computation com.github.vitaliyp.trolab.lab1.director class performs all work
 * connected to distributing data among computers
 * and getting results back.
 */
public class DirectorZero {
	public static void main(String[] args) {
		try {
			Socket s = new Socket("localhost", 1460);
			//Create input and output streams
			ObjectInputStream objectInputStream =
					new ObjectInputStream(s.getInputStream());
			ObjectOutputStream objectOutputStream =
					new ObjectOutputStream(s.getOutputStream());

			//Send Command
			objectOutputStream.writeObject(ComputerCommand.COMPUTE);
			objectOutputStream.flush();

			//Get number of processors
			int nProc = objectInputStream.readInt();

			//Send name of class and number of bytes over a network
			objectOutputStream.writeUTF("com.github.vitaliyp.trolab.lab1.director.Lab1ComputerWorker");
			objectOutputStream.flush();
			File f = new File("out/production/test/com.github.vitaliyp.trolab.lab1.director/Lab1ComputerWorker.class");
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

			//Define the task, break it and send
			int arr[] = new int[10000];
			for(int i = 0; i<10000; i++){
				arr[i] = i;
			}
			ArrayList<WorkerDataContainer> containers = new ArrayList<WorkerDataContainer>(nProc);
			int nh = arr.length/nProc;
			for(int i = 0; i<nProc; i++){
				WorkerDataContainer container = new WorkerDataContainer();
				container.id = i;
				container.status = ContainerStatus.DATA;
				container.entries.add(Arrays.copyOfRange(arr, i * nh, (i + 1) * nh));
				containers.add(container);
			}
//			for(Object a:(int[])containers.get(1).entries.get(0)){
//				System.out.println(a);
//			}

			//Send number of containers
			objectOutputStream.writeInt(containers.size());
			objectOutputStream.flush();


			//Send containers one by one
			for(WorkerDataContainer container: containers){
				objectOutputStream.writeObject(container);
			}
			objectOutputStream.flush();

			//waiting for all containers to arrive
			containers.clear();
			for(int i =0 ; i<nProc; i++){
				try {
					WorkerDataContainer container =
							(WorkerDataContainer)objectInputStream.readObject();
					containers.add(container.id, container);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			//Combine task
			int sum = 0;
			for(WorkerDataContainer container: containers){
				sum += (Double)container.entries.get(0);
			}

			System.out.println("Result: "+sum);

			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
