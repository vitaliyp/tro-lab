package com.github.vitaliyp.trolab.lab1.director;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Diretor {
	public static void main(String[] args) {

		//Read list of hosts from text file
		ArrayList<ComputingNode> hosts = new ArrayList<ComputingNode>();
		BufferedReader hostsIn;
		try {
			hostsIn = new BufferedReader(new FileReader("nodes.txt"));
			String line;
			Pattern pattern =
					Pattern.compile("^(?!#)(((\\d{1,3}\\.){3}\\d{1,3})|([\\w.]*)):(\\d{1,5})");
			while((line = hostsIn.readLine())!=null){
				if(line.charAt(0)!='#'){
					Matcher matcher = pattern.matcher(line);
					if(matcher.find()){
						hosts.add(new ComputingNode(matcher.group(1), Integer.parseInt(matcher.group(5))));
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Can't read nodes.txt");
		}

		//Input data
		Task task = new Lab1Task();
		task.inputData();

		int nThreads = hosts.size();

		//Create semaphore to synchronize threads
		Semaphore semaphore = new Semaphore(0, true);

		//Create pool of threads for com.github.vitaliyp.trolab.lab1.director workers
		ExecutorService executorService = Executors.newFixedThreadPool(hosts.size());
		//Execute workers
		for(ComputingNode node: hosts){
			executorService.execute(new DirectorWorker(task, node, semaphore));
		}

		//Wait for all threads to aquire number of processors
		try {
			semaphore.acquire(nThreads);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Calculate total number of processors
		int nProc = 0;
		for(ComputingNode node: hosts){
			nProc += node.getProcessors();
		}

		//Partition the task
		task.partition(nProc);

		//Release all workers to continue job
		synchronized (task){
			task.notifyAll();
		}

		//Wait for all workers to terminate
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		task.outputData();
	}

}
