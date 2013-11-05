package server;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class ServletLoader {
	
	public static final Path dir =  Paths.get("../SimpleWebServer/Servlets/");
	private DirectoryWatcher watch;
	
	
	public ServletLoader() throws IOException{
		
		this.watch = new DirectoryWatcher(dir, false);
	
	}
	
	
	private void ListenForDirectoryChanges(){
		
		
	}
	
	
}
