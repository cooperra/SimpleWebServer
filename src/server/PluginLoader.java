package server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import plugin.IPlugin;

/**
 * 
 * @author Robbie Cooper
 */
public class PluginLoader {
	private PluginList pluginList;
	
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
	
}
