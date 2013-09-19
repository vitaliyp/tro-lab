package com.github.vitaliyp.trolab.lab1.director;

import com.github.vitaliyp.trolab.lab1.commons.ComputerWorker;
import com.github.vitaliyp.trolab.lab1.commons.WorkerDataContainer;

import java.util.Iterator;

public class Lab1ComputerWorker extends ComputerWorker {
	@Override
	public void compute(WorkerDataContainer dataContainer) {
		//Extract data from container
		Iterator<Object> it = dataContainer.entries.iterator();
		int N =(Integer)it.next();
		int h = (Integer)it.next();
		int[] A = (int[])it.next();
		int[] B = (int[])it.next();
		int[] C = (int[])it.next();
		int[] D = (int[])it.next();
		int[][] MA = (int[][])it.next();

		int[][] MR = new int[h][N];

		int[][] MO = new int[h][N];
		//Multiply vectors at first
		for(int i = 0; i<h; i++){
			for(int j = 0; j<N; j++){
				MO[i][j] = A[i]*B[j]+C[i]*D[j];
			}
		}

		//Multiply matrices
		for(int i = 0; i<h; i++){
			for(int j = 0; j<N; j++){
				MR[i][j] = 0;
				for(int k = 0; k<N; k++){
					MR[i][j] += MO[i][k]*MA[k][j];
				}
			}
		}

		//Build data container
		dataContainer.entries.clear();
		dataContainer.entries.add(MR);
	}
}
