package server;


import java.nio.file.*;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class DirectoryWatcher {
	
    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private ServletLoader loader;
    private boolean trace = false;
    private Path dir;
    private EventHandler handler;
    
    public interface EventHandler {
    	public void onCreate(Path p);
    	public void onModify(Path p);
    	public void onDelete(Path p);
    }
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    
    public DirectoryWatcher(Path dir, boolean recursive, EventHandler handler) throws IOException{
    	 this.watcher = FileSystems.getDefault().newWatchService();
         this.keys = new HashMap<WatchKey,Path>();
         this.handler = handler;
         this.dir = dir;

         
         if (recursive) {
             System.out.format("Scanning %s ...\n", dir);
             registerAll(dir);
             System.out.println("Done.");
         } else {
             register(dir);
         }
         
    	
    }
    
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    void processEvents() {
        for (;;) {
        	
        	WatchKey key;
        	try{
        		key = this.watcher.take();
        		
        	} catch (InterruptedException x) {
                return;
            }
        	

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
            
            for (WatchEvent<?> event: key.pollEvents()) {
            	WatchEvent.Kind kind = event.kind();
            	
            	if (kind == OVERFLOW){
            		continue;
            	}else{
            		
            		WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();
                    Path absolutePath = dir.resolve(filename);
            	


	            	if (kind == ENTRY_CREATE){
	            		handler.onCreate(absolutePath);
	            	}
	            	else if (kind == ENTRY_MODIFY){
	            		handler.onModify(absolutePath);
	            	}
	            	else if(kind == ENTRY_DELETE){
	            		handler.onDelete(absolutePath);
	            	}

            	}
            	
            }
            
            boolean valid = key.reset();
            if (!valid) {
                    break;
            }

    
        }
    }
}
