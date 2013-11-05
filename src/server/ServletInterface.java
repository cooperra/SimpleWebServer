package server;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;

public interface ServletInterface {

	public HttpResponse makeResponse(HttpRequest request, String rootDirectory, File file);

	/**
	 * @return
	 */
	public String getServletID();

}
