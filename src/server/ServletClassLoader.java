package server;

import java.io.*;

public class ServletClassLoader extends ClassLoader{

	
	 File directory;

	    public ServletClassLoader (File dir) {
	                directory = dir;
	        }

	    public Class loadClass (String name) throws ClassNotFoundException {
	      return loadClass(name, true);
	    }

	    public Class loadClass (String classname, boolean resolve) throws ClassNotFoundException {
	   System.out.println(classname);
	      try {

	        Class c = findLoadedClass(classname);

	        if (c == null) {
	          try { c = findSystemClass(classname); }
	          catch (Exception ex) {}
	        }

	        if (c == null) {
	          String filename = classname.replace('.',File.separatorChar)+".class";
	          System.out.println(filename);
	          File f = new File(directory, filename);

	          int length = (int) f.length();
	          byte[] classbytes = new byte[length];
	          DataInputStream in = new DataInputStream(new FileInputStream(f));
	          in.readFully(classbytes);
	          in.close();
	          System.out.println(classname);
	          c = defineClass(classname, classbytes, 0, length);
	        }

	        if (resolve) resolveClass(c);

	        return c;
	      }
	      catch (Exception ex) { throw new ClassNotFoundException(ex.toString()); }
	    }
	}
