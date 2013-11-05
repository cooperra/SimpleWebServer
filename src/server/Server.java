/*
 * Server.java
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
 
package server;

import gui.WebServer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This represents a welcoming server for the incoming
 * TCP request from a HTTP client such as a web browser. 
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private final long dosAttackTimeMeasure = 900000000;
	
											  
	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	
	private long connections;
	private long serviceTime;
	
	private WebServer window;
	private ArrayList<InetAddress> ipAddressQueue = new ArrayList<InetAddress>();
	private HashMap<InetAddress,Integer> ipAddressLog = new HashMap<InetAddress,Integer>();
	private ArrayList<Long> timeQueue = new ArrayList<Long>();
	private ArrayList<InetAddress> banList = new ArrayList<InetAddress>();
	protected ServletList servletList;
	protected PluginList pluginList;
	protected ServletLoader servletLoader;

	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server(String rootDirectory, int port, WebServer window) {
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.stop = false;
		this.connections = 0;
		this.serviceTime = 0;
		this.window = window;
		this.ipAddressQueue = new ArrayList<InetAddress>();
		this.ipAddressLog = new HashMap<InetAddress,Integer>();
		this.servletList = new ServletList();
		try {
			
			this.servletLoader = new ServletLoader(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(this.servletLoader).start();
		this.pluginList = new PluginList(this);
	}

	/**
	 * Gets the root directory for this web server.
	 * 
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}


	/**
	 * Gets the port number for this web server.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns connections serviced per second. 
	 * Synchronized to be used in threaded environment.
	 * 
	 * @return
	 */
	public synchronized double getServiceRate() {
		if(this.serviceTime == 0)
			return Long.MIN_VALUE;
		double rate = this.connections/(double)this.serviceTime;
		rate = rate * 1000;
		return rate;
	}
	
	/**
	 * Increments number of connection by the supplied value.
	 * Synchronized to be used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementConnections(long value) {
		this.connections += value;
	}
	
	/**
	 * Increments the service time by the supplied value.
	 * Synchronized to be used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementServiceTime(long value) {
		this.serviceTime += value;
	}

	/**
	 * The entry method for the main server thread that accepts incoming
	 * TCP connection request and creates a {@link ConnectionHandler} for
	 * the request.
	 */
	public void run() {
		try {
			this.welcomeSocket = new ServerSocket(port);
			
			// Now keep welcoming new connections until stop flag is set to true
			while(true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				
				Socket connectionSocket = this.welcomeSocket.accept();
				if(banList.contains(connectionSocket.getInetAddress())){
					break;
				}else{
					
					addItemToIPAddressQueue(connectionSocket.getInetAddress());
					
					
					// Come out of the loop if the stop flag is set
					if(this.stop)
						break;
					
					// Create a handler for this incoming connection and start the handler in a new thread
					ConnectionHandler handler = new ConnectionHandler(this, connectionSocket);
					new Thread(handler).start();
				}
			}
			this.welcomeSocket.close();
		}
		catch(Exception e) {
			window.showSocketException(e);
		}
	}
	
	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if(this.stop)
			return;
		
		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept() method 
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), port);
			
			// We do not have any other job for this socket so just close it
			socket.close();
		}
		catch(Exception e){}
	}
	
	/**
	 * Checks if the server is stopped or not.
	 * @return
	 */
	public boolean isStoped() {
		if(this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}
	
	private void addItemToIPAddressQueue(InetAddress address){
		
		//Gets time stamp of current action
		long currentTime = System.nanoTime();
		
		//Adds the new item to the ip address queue
		ipAddressQueue.add(address);
		
		//Adds the new item to the time mark queue
		timeQueue.add(currentTime);
		
		//If the address is new within the last 200 logs
		//we then add it to the hash map
		//If it is not new, we increment the number of times
		//we've logged it
		if(ipAddressLog.containsKey(address)){
			int i = ipAddressLog.get(address);
			i = i + 1;
			ipAddressLog.put(address, i);

		}else{
			ipAddressLog.put(address, 1);
		}		
		
		//The queue will handle up to 200 items
		if(ipAddressQueue.size()>200){
			
			//At 201 items, it will kick out the item at the zero position
			InetAddress tempAddress = ipAddressQueue.get(0);
			long tempTime = timeQueue.get(0);
			ipAddressQueue.remove(0);
			timeQueue.remove(0);
			
			//It will also decrement the hashmap accordingly
			int j = ipAddressLog.get(tempAddress);
			if(j == 1){
				ipAddressLog.remove(tempAddress);
			}else{
				ipAddressLog.put(tempAddress, j--);
			}
			
			//Checks now if there is a DoS attack.
			
			if(ipAddressLog.get(address) > 50 &&
					currentTime - tempTime < dosAttackTimeMeasure){
				banList.add(address);
			}
		}
	}
}
