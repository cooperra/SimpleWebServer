/*
 * HttpRequest.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a request object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public abstract class HttpRequest {
	private String uri;
	private String version;
	private Map<String, String> header;
	private String body;
	private final long LENGTHLIMIT = 1000000000;
	
	private HttpRequest() {
		this.header = new HashMap<String, String>();
		this.body = "";
	}
	
	protected HttpRequest(BufferedReader reader, String uri, String version) throws ProtocolException {
		this();
		this.uri = uri;
		this.version = version;
		this.readHeader(reader);
		if (this.canHaveContent()) {
			this.readBody(reader);
		}
	}
	
	public abstract boolean canHaveContent();

	/**
	 * @param reader A BufferedReader that is currently seeking at the beginning of the header
	 * 				 key value pairs
	 * @throws ProtocolException
	 */
	private void readHeader(BufferedReader reader) throws ProtocolException {
		try {
			String line;
			
			// Rest of the request is a header that maps keys to values
			// e.g. Host: www.rose-hulman.edu
			// We will convert both the strings to lower case to be able to search later
			line = reader.readLine().trim();
			
			while(!line.equals("")) {
				// THIS IS A PATCH 
				// Instead of a string tokenizer, we are using string split
				// Lets break the line into two part with first space as a separator 
				
				// First lets trim the line to remove escape characters
				line = line.trim();
				
				// Now, get index of the first occurrence of space
				int index = line.indexOf(' ');
				
				if(index > 0 && index < line.length()-1) {
					// Now lets break the string in two parts
					String key = line.substring(0, index); // Get first part, e.g. "Host:"
					String value = line.substring(index+1); // Get the rest, e.g. "www.rose-hulman.edu"
					
					// Lets strip off the white spaces from key if any and change it to lower case
					key = key.trim().toLowerCase();
					
					// Lets also remove ":" from the key
					key = key.substring(0, key.length() - 1);
					
					// Lets strip white spaces if any from value as well
					value = value.trim();
					
					// Now lets put the key=>value mapping to the header map
					this.header.put(key, value);
				}
				
				// Processed one more line, now lets read another header line and loop
				line = reader.readLine().trim();
			}
		} catch (IOException e) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
	}

	/**
	 * @param reader A BufferedReader located at the body of the message (if it has one)
	 * @throws ProtocolException
	 */
	private void readBody(BufferedReader reader) throws ProtocolException {
		int contentLen;
		try {
			String contentLenStr = this.header.get("content-length");
			if (contentLenStr == null) {
				// use length required response code
				throw new ProtocolException(Protocol.LENGTH_REQUIRED_CODE, Protocol.LENGTH_REQUIRED_TEXT);
			}
			contentLen = Integer.parseInt(contentLenStr);
			
			if(contentLen > LENGTHLIMIT){
				throw new ProtocolException(Protocol.REQUEST_ENTITY_TOO_LARGE_CODE, Protocol.REQUEST_ENTITY_TOO_LARGE_TEXT);
			}
		} catch (NumberFormatException e) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
		

		
		StringBuilder bodybuilder = new StringBuilder();
		for (int i = 0; i < contentLen; i++) {
			char c;
			try {
				c = (char) reader.read();
			} catch (IOException e) {
				//TODO check that this is the right error code for too short content
				throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
			}
			bodybuilder.append(c);
		}
		this.body = bodybuilder.toString();
	}

	/**
	 * The request method.
	 * 
	 * @return the method
	 */
	public abstract String getMethod();

	/**
	 * The URI of the request object.
	 * 
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * The version of the http request.
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * The key to value mapping in the request header fields.
	 * 
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Lets return the unmodifable view of the header map
		return Collections.unmodifiableMap(header);
	}
	
	/**
	 * The body of the http request.
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------------------------------\n");
		buffer.append(this.getMethod());
		buffer.append(Protocol.SPACE);
		buffer.append(this.uri);
		buffer.append(Protocol.SPACE);
		buffer.append(this.version);
		buffer.append(Protocol.LF);
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}
		buffer.append("----------------------------------\n");
		return buffer.toString();
	}
}
