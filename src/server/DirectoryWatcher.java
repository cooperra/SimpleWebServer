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
    private final boolean recursive;
    private ServletLoader loader;
    private boolean trace = false;
    private Path dir;
    
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    
    public DirectoryWatcher(Path dir, boolean recursive, ServletLoader sv) throws IOException{
    	 this.watcher = FileSystems.getDefault().newWatchService();
         this.keys = new HashMap<WatchKey,Path>();
         this.recursive = recursive;
         this.loader = sv;
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
            	
            	
            	if (kind == ENTRY_CREATE){
            		System.out.println(this.dir.toString());
            		System.out.println(filename.getFileName().toString());
            		String path = this.dir.toString() + "\\" + filename.getFileName().toString();
            		System.out.println(path);
            		
            		this.loader.checkAndLoadSingleServlet(path);
          
            	
            		
            		
        
            	}
            	else if (kind == ENTRY_MODIFY){
            		
            		System.out.println("Seeing a modify");
            	}
            	else if(kind == ENTRY_DELETE){
            		
            		this.loader.deleteServlet(filename.getFileName().toString());
            		
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
