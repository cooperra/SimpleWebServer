package server;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

public class DELETEServletTest implements ServletInterface{

	private String url = "/test/";
	
	public DELETEServletTest(){

	}
	
	public HttpResponse makeResponse(HttpRequest request,
			HttpResponse response, String rootDirectory, File file) {
		// Handling DELETE request here

		
		if (file.exists()) {
			if (!file.delete()) {
				response = HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
			}
		} else {
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		
		// if nothing has gone wrong so far... 
		if (response == null) {
			// Lets create 201 Created response
			response = HttpResponseFactory.create204NoContent(Protocol.CLOSE);
		}
		return response;
	}

}
