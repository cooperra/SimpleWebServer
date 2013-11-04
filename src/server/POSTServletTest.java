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

public class POSTServletTest implements ServletInterface{

	private String url = "/test/";
	
	public POSTServletTest(){

	}
	
	public HttpResponse makeResponse(HttpRequest request,
			HttpResponse response, String rootDirectory, File file) {
		// Handling POST request here

		String uri = request.getUri();
		
		if (file.getParentFile().exists() && file.getParentFile().isDirectory()) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter( new FileWriter(file));
				writer.write( request.getBody());
			} catch (IOException e) {
				response = HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
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
			// Lets create 201 Created response
			response = HttpResponseFactory.create201Created(uri, Protocol.CLOSE);
		}
		return response;
	}
}
