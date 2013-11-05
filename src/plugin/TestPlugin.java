package plugin;

import java.util.ArrayList;
import java.util.List;

public class TestPlugin implements IPlugin {
	
	private List<ServletMapping> list;
	
	
	public TestPlugin(){
	 ServletMapping m1 = new IPlugin.ServletMapping("GET", "/GETServletTest", "GETServletTest");
	 this.list = new ArrayList<ServletMapping>();
	 this.list.add(m1);
		
		
	}
	
	@Override
	public String getPluginID() {
		// TODO Auto-generated method stub
		return "TestPlugin";
	}

	@Override
	public List<String> getServletIDs() {
		// TODO Auto-generated method stub
		List<String> ret = null;
		for (ServletMapping mp : this.list) {
			ret.add(mp.getServletID());
			
			
		}
		return ret;
	}

	@Override
	public List<ServletMapping> getServletMappings() {
	return this.list;
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return "/TestPlugin";
	}

}
