package com.github.vitaliyp.trolab.lab1.director;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ComputingNode {
	private InetAddress adress;
	private int port;
	private int processors;

	public int getProcessors() {
		return processors;
	}

	public void setProcessors(int processors) {
		this.processors = processors;
	}

	public InetAddress getAdress() {
		return adress;
	}

	public int getPort() {
		return port;
	}

	public ComputingNode(String hostname, int port) throws UnknownHostException {
		this.adress = InetAddress.getByName(hostname);
		this.port = port;
	}

	@Override
	public String toString(){
		return adress+":"+port;
	}
}
