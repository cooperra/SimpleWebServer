/*
 * HttpResponseFactory.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */
 
package protocol;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This is a factory to produce various kind of HTTP responses.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponseFactory {
	/**
	 * Convenience method for adding general header to the supplied response object.
	 * 
	 * @param response The {@link HttpResponse} object whose header needs to be filled in.
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 */
	private static void fillGeneralHeader(HttpResponse response, String connection) {
		// Lets add Connection header
		response.put(Protocol.CONNECTION, connection);

		// Lets add current date
		Date date = Calendar.getInstance().getTime();
		response.put(Protocol.DATE, date.toString());
		
		// Lets add server info
		response.put(Protocol.Server, Protocol.getServerInfo());

		// Lets add extra header with provider info
		response.put(Protocol.PROVIDER, Protocol.AUTHOR);
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending the supplied file with supplied connection
	 * parameter.
	 * 
	 * @param file The {@link File} to be sent.
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 200 status.
	 */
	public static HttpResponse createGET200OK(File file, String connection) {
		return createGETHEAD200OK(file, connection, false);
	}
	public static HttpResponse createHEAD200OK(File file, String connection) {
		return createGETHEAD200OK(file, connection, true);
	}
	private static HttpResponse createGETHEAD200OK(File file, String connection, boolean isHead) {
		HttpResponse response;
		if (isHead) {
			response = new Response200OK(Protocol.VERSION, new HashMap<String, String>(), null);
		} else {
			response = new Response200OK(Protocol.VERSION, new HashMap<String, String>(), file);
		}
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		// Lets add last modified date for the file
		long timeSinceEpoch = file.lastModified();
		Date modifiedTime = new Date(timeSinceEpoch);
		response.put(Protocol.LAST_MODIFIED, modifiedTime.toString());
		
		// Lets get content length in bytes
		long length = file.length();
		response.put(Protocol.CONTENT_LENGTH, length + "");
		
		// Lets get MIME type for the file
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mime = fileNameMap.getContentTypeFor(file.getName());
		// The fileNameMap cannot find mime type for all of the documents, e.g. doc, odt, etc.
		// So we will not add this field if we cannot figure out what a mime type is for the file.
		// Let browser do this job by itself.
		if(mime != null) { 
			response.put(Protocol.CONTENT_TYPE, mime);
		}
		
		return response;
	}
	
	public static HttpResponse create201Created(String location, String connection) {
		HashMap<String, String> header = new HashMap<String, String>();
		if (location != null) {
			header.put("Location", location);
		}
		
		HttpResponse response = new Response201Created(Protocol.VERSION, header, null);
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;
	}
	
	public static HttpResponse create204NoContent(String connection) {
		HttpResponse response = new Response204NoContent(Protocol.VERSION, new HashMap<String, String>(), null);
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending bad request response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 400 status.
	 */
	public static HttpResponse create400BadRequest(String connection) {
		HttpResponse response = new Response400BadRequest(Protocol.VERSION, new HashMap<String, String>(), null);
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending not found response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 404 status.
	 */
	public static HttpResponse create404NotFound(String connection) {
		HttpResponse response = new Response404NotFound(Protocol.VERSION, new HashMap<String, String>(), null);
		
		// Lets fill up the header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;	
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending not found response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 404 status.
	 */
	public static HttpResponse create411LengthRequired(String connection) {
		HttpResponse response = new Response411LengthRequired(Protocol.VERSION, new HashMap<String, String>(), null);
		
		// Lets fill up the header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;	
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending version not supported response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 505 status.
	 */
	public static HttpResponse create505NotSupported(String connection) {
		// TODO fill in this method
		return null;
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending file not modified response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 304 status.
	 */
	public static HttpResponse create304NotModified(String connection) {
		// TODO fill in this method
		return null;
	}

	/**
	 * Creates a {@link HttpResponse} object for sending version not implemented response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 501 status.
	 */
	public static HttpResponse create501NotImplemented(String connection) {
		HttpResponse response = new Response501NotImplemented(Protocol.VERSION, new HashMap<String, String>(), null);
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;
	}

	/**
	 * Creates a {@link HttpResponse} object for sending version not implemented response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 501 status.
	 */
	public static HttpResponse create500InternalServerError(String connection) {
		HttpResponse response = new Response500InternalServerError(Protocol.VERSION, new HashMap<String, String>(), null);
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);
		
		return response;
	}
}
