package server;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

public class PUTServletTest {

	private String url = "/test/";
	
	public PUTServletTest(){

	}
	
	public HttpResponse makeResponse(HttpRequest request, HttpResponse response, File file)
			throws IOException {
		// Handling PUT request here
		
		boolean preexists = false;
		if (file.exists()) {
			preexists = true;
		}
		
		if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter( new FileWriter(file));
				writer.write( request.getBody());
			} catch (IOException e) {
				response = HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} else {
			response = HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
		}
		
		// if nothing has gone wrong so far... 
		if (response == null) {
			if (preexists) {
				// Lets create 204 No Content response
				response = HttpResponseFactory.create204NoContent(Protocol.CLOSE);
			} else {
				// Lets create 201 Created response
				response = HttpResponseFactory.create201Created(null, Protocol.CLOSE);
			}
		}
		return response;
	}
	
}
