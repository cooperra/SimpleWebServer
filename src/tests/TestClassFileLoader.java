package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import server.ClassFileLoader;
import server.ServletInterface;

/**
 * 
 * @author Robbie Cooper
 */
public class TestClassFileLoader {

	/**
	 * Test method for {@link server.ClassFileLoader#loadClassFromFile(java.io.File)}.
	 */
	@Test
	public void testLoadClassFromFileFile() {
		ClassFileLoader cl = new ClassFileLoader();
		File f = new File("bin/server/POSTServletTest.class");
		Class c = null;
		try {
			c = cl.loadClassFromFile(f);
		} catch (IOException e1) {
			File cd = new File(".");
			System.out.println(cd.getAbsolutePath());
			e1.printStackTrace();
			fail();
		}
		ServletInterface instance = null;
		try {
			instance = (ServletInterface) c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(instance.getServletID(), "POSTServletTest");
	}

}
