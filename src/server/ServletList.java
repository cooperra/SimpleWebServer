package server;

import java.util.HashMap;
import java.util.Map;

import server.ServletInterface;

public class ServletList {
	private Map<String, ServletInterface> servlets = new HashMap<String, ServletInterface>();
	
	public ServletList() {
	}

	boolean addServlet(ServletInterface p) {
		String servletID = p.getServletID();
		if (servlets.containsKey(servletID)) {
			return false; // deny insertion if Servlet already loaded
		}
		servlets.put(servletID, p);
		return true;
	}
	
	boolean removeServlet(String servletID) {
		if (servlets.containsKey(servletID)) {
			return false; // can't remove if Servlet isn't here
		}
		servlets.remove(servletID);
		return true;
	}
	
	public ServletInterface getServlet(String servletID) {
		return servlets.get(servletID);
	}
	
	public boolean containsServlet(String servletID) {
		return servlets.containsKey(servletID);
	}
}
