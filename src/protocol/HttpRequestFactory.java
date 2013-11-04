/*
 * HttpRequestFactory.java
 * Nov 3, 2013
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
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
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * 
 * @author Robbie Cooper
 */
public class HttpRequestFactory {
	private static final String[] SUPPORTED_METHODS = {"GET","POST","HEAD","PUT","DELETE"};
	private static final String[] BODY_METHODS = {"POST","PUT"};

	/**
	 * Reads raw data from the supplied input stream and constructs a 
	 * <tt>HttpRequest</tt> object out of the raw data.
	 * 
	 * @param inputStream The input stream to read from.
	 * @return A <tt>HttpRequest</tt> object.
	 * @throws Exception Throws either {@link ProtocolException} for bad request or 
	 * {@link IOException} for socket input stream read errors.
	 */
	public static HttpRequest read(InputStream inputStream) throws Exception {
		InputStreamReader inStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inStreamReader);
		
		//First Request Line: GET /somedir/page.html HTTP/1.1
		String line = reader.readLine(); // A line ends with either a \r, or a \n, or both
		
		if(line == null) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
		
		// We will break this line using space as delimeter into three parts
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// Error checking the first line must have exactly three elements
		if(tokenizer.countTokens() != 3) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
		
		String method = tokenizer.nextToken();		// GET
		String uri = tokenizer.nextToken();		// /somedir/page.html
		String version = tokenizer.nextToken();	// HTTP/1.1
		
		return readRequest(reader, method, uri, version);
	}

	/**
	 * @param reader
	 * @param request
	 * @return
	 * @throws ProtocolException
	 * @throws IOException
	 */
	private static HttpRequest readRequest(BufferedReader reader, String method, String uri, String version) throws ProtocolException {
		
		if (method.equalsIgnoreCase(Protocol.GET)) {
			return new RequestGet(reader, uri, version);
		} else if (method.equalsIgnoreCase(Protocol.HEAD)) {
			return new RequestHead(reader, uri, version);
		} else if (method.equalsIgnoreCase(Protocol.POST)) {
			return new RequestPost(reader, uri, version);
		} else if (method.equalsIgnoreCase(Protocol.PUT)) {
			return new RequestPut(reader, uri, version);
		} else if (method.equalsIgnoreCase(Protocol.DELETE)) {
			return new RequestDelete(reader, uri, version);
		} else {
			throw new ProtocolException(Protocol.NOT_IMPLEMENTED_CODE, Protocol.NOT_IMPLEMENTED_TEXT);
		}
	}
}
