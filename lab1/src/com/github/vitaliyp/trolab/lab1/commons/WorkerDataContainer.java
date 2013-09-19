package com.github.vitaliyp.trolab.lab1.commons;

import java.io.Serializable;
import java.util.ArrayList;

public class WorkerDataContainer implements Serializable{
	public int id;
	public ContainerStatus status = ContainerStatus.DATA;
	public ArrayList<Object> entries = new ArrayList<Object>();
}
