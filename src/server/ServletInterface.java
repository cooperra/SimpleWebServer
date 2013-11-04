package server;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;

public interface ServletInterface {

	public HttpResponse makeResponse(HttpRequest request,
			HttpResponse response, String rootDirectory, File file);
	
	
	public String getname(); //returns the name of the servlet

}
