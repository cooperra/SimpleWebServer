package server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 
 * @author Robbie Cooper
 */
public class ClassFileLoader extends ClassLoader {

	/**
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public Class<?> loadClassFromFile(File f) throws IOException {
        return loadClassFromFile(f, null);
	}
	
	public Class<?> loadClassFromFile(File f, String classname) throws IOException {
		

	
        int length = (int) f.length();
        byte[] classbytes = new byte[length];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        in.readFully(classbytes);
        in.close();
        Class c = this.defineClass(classname, classbytes, 0, length);
      
        this.resolveClass(c);
        return c;
    
	}
	
}
