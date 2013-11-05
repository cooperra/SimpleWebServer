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

public class PUTServletTest implements ServletInterface{

	private String url = "/test/";
	
	public PUTServletTest(){

	}
	
	public HttpResponse makeResponse(HttpRequest request, String rootDirectory, File file) {
		// Handling PUT request here
		
		boolean preexists = false;
		if (file.exists()) {
			preexists = true;
		}
		
		HttpResponse response = null;
		if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter( new FileWriter(file));
				writer.write( request.getBody());
			} catch (IOException e) {
				response  = HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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

	/* (non-Javadoc)
	 * @see server.ServletInterface#getServletID()
	 */
	public String getServletID() {
		return "PUTServletTest";
	}
	
}
