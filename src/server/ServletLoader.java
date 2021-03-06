package server;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class ServletLoader implements Runnable{
	
	public static final Path directory =  Paths.get("Servlets/").toAbsolutePath();
	public ServletList list;
	private DirectoryWatcher watch;
	
	
	public ServletLoader(Server server) throws IOException{
		this.list = server.servletList;
		
	
	}
	
	
	private void ListenForDirectoryChanges() throws IOException{
		this.watch = new DirectoryWatcher(directory, false, new DirectoryWatcher.EventHandler() {
			
			public void onModify(Path p) {
				// TODO reload
				System.out.println("Seeing a modify: " + p.toString());
			}
			
			public void onDelete(Path p) {
				// TODO unload
				System.out.println("Seeing a delete: " + p.toString());
			}
			
			public void onCreate(Path p) {
				// load
				System.out.println("Seeing a create: " + p.toString());
				try {
					// program likes to read files while they're being written
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file = p.toFile();
				try {
					checkAndLoadSingleServlet(file);
				} catch (java.lang.ClassFormatError e) {
					System.err.println("This error is likely caused by loading a file that already exists or reading a Servlet while its being written. Ignoring.");
					e.printStackTrace();
				}
			}
		});
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
	
	public void checkAndLoadSingleServlet(String filepath) {
		File file = new File(filepath);
		checkAndLoadSingleServlet(file);
	}
	
	public void checkAndLoadSingleServlet(File file){
		
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
	
	public void deleteServlet(String filename){
		
		this.list.removeServlet(filename);
		System.out.println(this.list.size());
	}
	
	


	public void run() {
		initialServletLoad();
		
	}
	
	
}
