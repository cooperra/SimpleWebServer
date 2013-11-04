package server;
import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;


public class GETServletTest{

	private String url = "/test/";
	
	public GETServletTest(){

	}

	public HttpResponse makeResponse(HttpRequest request, String rootDirectory, File file) {
		HttpResponse response;
		//				Map<String, String> header = request.getHeader();
		//				String date = header.get("if-modified-since");
		//				String hostName = header.get("host");
		//				
						// Handling GET request here
						// Get relative URI path from request
						String uri = request.getUri();

						// Check if the file exists
						if(file.exists()) {
							if(file.isDirectory()) {
								// Look for default index.html file in a directory
								String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
								file = new File(location);
								if(file.exists()) {
									// Lets create 200 OK response
									response = HttpResponseFactory.createGET200OK(file, Protocol.CLOSE);
								}
								else {
									// File does not exist so lets create 404 file not found code
									response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
								}
							}
							else { // Its a file
								// Lets create 200 OK response
								response = HttpResponseFactory.createGET200OK(file, Protocol.CLOSE);
							}
						}
						else {
							// File does not exist so lets create 404 file not found code
							response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
						}
		return response;
	}
}
