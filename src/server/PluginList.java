package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugin.IPlugin;

/**
 * 
 * @author Robbie Cooper
 */
public class PluginList {
	private Map<String, IPlugin> plugins = new HashMap<String, IPlugin>();

	boolean addPlugin(IPlugin p) {
		String pluginID = p.getPluginID();
		if (plugins.containsKey(pluginID)) {
			return false; // deny insertion if plugin already loaded
		}
		plugins.put(pluginID, p);
		return true;
	}
	
	boolean removePlugin(String pluginID) {
		if (plugins.containsKey(pluginID)) {
			return false; // can't remove if plugin isn't here
		}
		plugins.remove(pluginID);
		return true;
	}
	
	public IPlugin getPlugin(String pluginID) {
		return plugins.get(pluginID);
	}
	
	public boolean containsPlugin(String pluginID) {
		return plugins.containsKey(pluginID);
	}
}
