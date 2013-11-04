package server;

import java.util.ArrayList;
import java.util.List;

public class ServletList {
	
	private List<ServletInterface> servlets = new ArrayList<ServletInterface>();
	
	public ServletList() {
		
	}
	
	public boolean addServlet(ServletInterface s) {
		// TODO check whether adding the servlet is allowed
		return servlets.add(s);
	}

	public boolean removeServlet(String servletID) {
		// TODO implement
		return false;
	}
	
	public ServletInterface getServlet(String servletID) {
		// TODO implement
		return null;
	}
}
