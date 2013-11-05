package server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import plugin.IPlugin;

/**
 * 
 * @author Robbie Cooper
 */
public class PluginLoader implements Runnable {
	public static final Path directory =  Paths.get("Plugins/").toAbsolutePath();
	private PluginList pluginList;
	private DirectoryWatcher watch;
	
	public PluginLoader(Server server) {
		this.pluginList = server.pluginList;
	}
	
	private boolean loadPlugin(String fileName) {
		ClassFileLoader cl = new ClassFileLoader();
		
		try {
			if (! fileName.endsWith(".class")) {
				return false;
			}

			Class<?> c = cl.loadClassFromFile(new File(fileName));
			Class[] interfaces = c.getInterfaces();
			
			for (Class i : interfaces) {
				if (i.getName().equals("IPlugin")) {
					IPlugin plugin = (IPlugin) c.newInstance();
					return loadPlugin(plugin);
				}
			}
			return false;
		} catch (Exception ex) {
			System.err.println("File " + fileName + " does not contain a valid plugin class.");
			return false;
		}
	}

	/**
	 * @param plugin
	 */
	private boolean loadPlugin(IPlugin plugin) {
		
		// TODO fun
		return pluginList.addPlugin(plugin);
	}
	
	private void listenForDirectoryChanges() throws IOException{
		this.watch = new DirectoryWatcher(directory, false, new DirectoryWatcher.EventHandler() {
			
			public void onModify(Path p) {
				// TODO reload
				System.out.println("Seeing a modify: " + p.toString());
			}
			
			public void onDelete(Path p) {
				// TODO unload
				System.out.println("Seeing a delete: " + p.toString());
			}
			
			public void onCreate(Path p) {
				// load
				System.out.println("Seeing a create: " + p.toString());
				try {
					// program likes to read files while they're being written
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file = p.toFile();
				try {
					loadPlugin(file.getAbsolutePath());
				} catch (java.lang.ClassFormatError e) {
					System.err.println("This error is likely caused by loading a file that already exists or reading a Plugin while its being written. Ignoring.");
					e.printStackTrace();
				}
			}
		});
		this.watch.processEvents();
		
		
	}
	
	public void run() {
		initialPluginLoad();
	}
	
	private void initialPluginLoad(){
		File dir = new File(directory.toUri());
		File[] files = dir.listFiles();
		for (File file : files) {
			loadPlugin(file.getAbsolutePath());
		}
		try {
			this.listenForDirectoryChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
