package server;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class ServletLoader implements Runnable {
	
	public static final Path dir =  Paths.get("../SimpleWebServer/Servlets/");
	private DirectoryWatcher watch;
	ArrayList<ServletInterface> temp;
	private ServletList list;
	private ServletClassLoader cl;
	
	
	
	public ServletLoader(ServletList list) throws IOException{
		
	
		this.list = list;
		MakeWatcher();
	}
		
	
	
	public void run(){
		initialLoadOfServlets();
		
	}
	
	private boolean MakeWatcher() throws IOException{
		
		this.watch = new DirectoryWatcher(dir, false, this);
		return true;
	}
	
	
	private void ListenForDirectoryChanges(){
		
		
		this.watch.processEvents();
		
		
	}
	

	

	private void initialLoadOfServlets() {
	
		ArrayList<ServletInterface> servs = new ArrayList<ServletInterface>();
		File directory = new File(System.getProperty("user.dir")+ File.separator+ "Servlets");
	 cl = new ServletClassLoader(directory);
		  if (directory.exists()) {
              if(directory.isDirectory()){
                      String[] files = directory.list();
                      for (int i=0; i<files.length; i++) {
	                    	 
                              try {
                                      if (! files[i].endsWith(".class"))
                                              continue;
                                      	
                                      Class<?> c = cl.loadClass(files[i].substring(0, files[i].indexOf(".")));
                                      Class[] intf = c.getInterfaces();
                                      System.out.println(intf.toString());
                                      
                                      for (int j=0; j<intf.length; j++) {
                                    	  		System.out.println(intf[j].getName());
                                              if (intf[j].getName().equals("ServletInterface")) {
                                            	  ServletInterface se = (ServletInterface) c.newInstance();
                                            	  servs.add(se);
                                            	  list.add(se);
                                            	  System.out.println("Found a class!");
                                            	  continue;
                                              }
                                      }
                                      } catch (Exception ex) {
                                          System.err.println("File " + files[i] + " does not contain a valid makeResponse class.");
                                         
                                  }                                
                          }
                  }else{
                          System.out.println("File is not a directory");
                          return;
                  }
          }else{
                  System.out.println("Directory does not exist");
                  return;
          }
          
          if(servs.isEmpty()){
                  System.out.println("There are no plugins");
                  
          }
         
          ListenForDirectoryChanges();
         
  }
	
public void LoadServlet(String name) throws ClassNotFoundException{
	
	 try {
	 Class<?> c = cl.loadClass(name.substring(0, name.indexOf(".")));
	 Class[] intf = c.getInterfaces();
     
     for (int j=0; j<intf.length; j++) {
             if (intf[j].getName().equals("ServletInterface")) {
           	  ServletInterface se = (ServletInterface) c.newInstance();
   
           	  list.add(se);
           	  continue;
             }
             

	
}

} catch (Exception ex) {
    System.err.println("File " + name + " does not contain a valid makeResponse class.");
    return;
}            
	

	
}}