package plugin;

import java.util.List;

/**
 * 
 * @author Robbie Cooper
 */
public interface IPlugin {
	class ServletMapping {
		private String method;
		private String uri;
		private String servletID;

		private ServletMapping(String method, String uri, String servletID) {
			this.method = method;
			this.uri = uri;
			this.servletID = servletID;
		}
		
		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}

		/**
		 * @return the uri
		 */
		public String getUri() {
			return uri;
		}

		/**
		 * @return the servletID
		 */
		public String getServletID() {
			return servletID;
		}
	}
	
	String getPluginID();
	List<String> getServletIDs();
	List<ServletMapping> getServletMappings();
	/**
	 * @return
	 */
	public String getURI();
}
