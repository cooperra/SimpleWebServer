package server;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class ServletLoader {
	
	public static final Path directory =  Paths.get("Servlets/").toAbsolutePath();
	public ServletList list;
	private DirectoryWatcher watch;
	
	
	public ServletLoader(Server server) throws IOException{
		this.list = server.servletList;
		initialServletLoad();
	
	}
	
	
	private void ListenForDirectoryChanges() throws IOException{
		this.watch = new DirectoryWatcher(directory, false, this);
		this.watch.processEvents();
		
		
	}
	
	private void initialServletLoad(){
	
		File dir = new File(directory.toUri());
		System.out.println(dir.toString());
		File[] files = dir.listFiles();
		ClassFileLoader cl = new ClassFileLoader();
		for (File file : files) {
			System.out.println(file.toString());
			if(! file.getName().endsWith(".class")){
				
				continue;}
			
			try {
				Class<?> c = cl.loadClassFromFile(file);
				Class[] inter = c.getInterfaces();
				
				for (Class class1 : inter) {
					System.out.println(class1.getName());
					if(class1.getName().equals("server.ServletInterface")){
						System.out.println("Got 1!!!");
						
						ServletInterface servlet = (ServletInterface) c.newInstance();
						loadServlet(servlet);
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		try {
			this.ListenForDirectoryChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void checkAndLoadSingleServlet(String filepath){
		
		File file = new File(filepath);
		
	if(! file.exists()){
		System.out.println("WHY?!");
		return;
	}
		if(! file.getName().endsWith(".class")){
			
			return;}
		
		ClassFileLoader cl = new ClassFileLoader();
		try {
			
			Class<?> c = cl.loadClassFromFile(file);
			Class[] inter = c.getInterfaces();
			
			for (Class class1 : inter) {
				System.out.println(class1.getName());
				if(class1.getName().equals("server.ServletInterface")){
					System.out.println("Got 1!!!");
					
					ServletInterface servlet = (ServletInterface) c.newInstance();
					loadServlet(servlet);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(this.list.size());
		
		
	}
	
	private void loadServlet(ServletInterface se){
		this.list.addServlet(se);
		return;
	}
	
	
}
