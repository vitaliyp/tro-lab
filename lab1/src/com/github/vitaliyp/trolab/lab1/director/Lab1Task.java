package com.github.vitaliyp.trolab.lab1.director;

import com.github.vitaliyp.trolab.lab1.commons.ContainerStatus;
import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Lab1Task extends Task{
	private static final int N = 10;
	protected int h;
	protected List<WorkerDataContainer> containers;
	private double result = 0;

	public Lab1Task() {
		super("com.github.vitaliyp.trolab.lab1.director.Lab1ComputerWorker", "out/production/lab1/com.github.vitaliyp.trolab.lab1.director/Lab1ComputerWorker.class");
	}

	private int[] A, B, C, D;
	private int[][] MA, MR;

	@Override
	public void partition(int nProc) {
		super.partition(nProc);
		h = N/nProc;
		containers = new LinkedList<WorkerDataContainer>();
		for(int i = 0; i<nProc; i++){
			WorkerDataContainer container = new WorkerDataContainer();
			container.id = i;
			container.status = ContainerStatus.DATA;
			container.entries.add(N);
			container.entries.add(h);
			container.entries.add(Arrays.copyOfRange(A, i*h, (i+1)*h));
			container.entries.add(B);
			container.entries.add(Arrays.copyOfRange(C, i*h, (i+1)*h));
			container.entries.add(D);
			container.entries.add(MA);
			containers.add(container);
		}
	}

	@Override
	public void inputData() {
		A = new int[N];
		B = new int[N];
		C = new int[N];
		D = new int[N];
		MA = new int[N][N];
		MR = new int[N][N];
		Arrays.fill(A, 1);
		Arrays.fill(B, 1);
		Arrays.fill(C, 1);
		Arrays.fill(D, 1);
		for(int i = 0; i<N; i++){
			Arrays.fill(MA[i], 1);
		}
	}

	@Override
	public ArrayList<WorkerDataContainer> giveData(int n) {
		ArrayList<WorkerDataContainer> wc = new ArrayList<WorkerDataContainer>(n);
		for(int i = 0; i<n; i++){
			wc.add(containers.remove(0));
		}
		return wc;
	}

	@Override
	public synchronized void takeResult(WorkerDataContainer container) {
		int[][] mt = (int[][])container.entries.get(0);
		for(int i = 0; i<h; i++){
			for(int j = 0; j<N; j++){
				MR[i+container.id*h][j] = mt[i][j];
			}
		}
	}

	@Override
	public void outputData() {
		for(int i = 0; i<N; i++){
			for(int j = 0; j<N; j++){
				System.out.print(MR[i][j]+" ");
			}
			System.out.println();
		}
	}
}
