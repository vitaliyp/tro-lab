package com.github.vitaliyp.trolab.lab1.computer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class InputStreamClassLoader extends ClassLoader{
	private InputStream inputStream;

	public InputStreamClassLoader(ObjectInputStream inputStream){
		super();
		this.inputStream = inputStream;
	}

	@Override
	public Class findClass(String name) throws ClassNotFoundException{
		byte[] b = null;
		try {
			b = loadClassData();
		} catch (IOException e) {
			throw new ClassNotFoundException("Can't load class data from stream properly.", e);
		}
		Class c = defineClass(name, b, 0, b.length);
		if(!name.equals(c.getName())){
			throw new ClassNotFoundException("Can't load class data from stream properly.");
		}
		return c;
	}


	private byte[] loadClassData() throws IOException{
		byte b[];
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		int n = dataInputStream.readInt();
		b = new byte[n];
		dataInputStream.readFully(b);
		return b;
	}
}
