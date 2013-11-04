package server;

import java.util.ArrayList;

public class ServletList {
	
	private ArrayList<ServletInterface> servlets;
	private ArrayList<String> servletNames;
	
	
	public ServletList(){
		
		this.servlets = new ArrayList<ServletInterface>();
		this.servletNames = new ArrayList<String>();
		
	}
	
	public boolean add(ServletInterface se){
		servletNames.add(se.getname());
		return servlets.add(se);
	
		
	}
	
	public boolean remove(ServletInterface se){
		servletNames.remove(se.getname());
		return servlets.remove(se);
	}
	
	public boolean contains(String name){
		return servletNames.contains(name);
		
	}
	
	public ServletInterface getServlet(String name){
		if(this.contains(name)){
			int i = servletNames.indexOf(name);
			return servlets.get(i);
		}else{
			return null;
		}
		
	}
	
	

}
