package de.m_marvin.archiveutility;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;

import de.m_marvin.archiveutility.access.IArchiveAccess;
import de.m_marvin.archiveutility.classes.ArchiveClasses;

public class Testing {

	public static void main(String... test) throws IOException {

		//System.out.println(Testing.class.getResource("Testing.class"));
		
		URL jarURL = new URL("file:/C:/Users/marvi/Desktop/test.jar");
		URL fileURL = new URL("jar", "", jarURL.toString() + "!/" + "de/m_marvin/archiveutility/Testing.class");
		
		URL url = new URL("jar:file:/C:/Users/marvi/Desktop/test.jar!/de/m_marvin/archiveutility/Testing.class");
		
		System.out.println(url);
		System.out.println(fileURL);
		
//		testClassJarAccess();
//		testClasspathBrowsing();
		
	}
	
	public static void testClassJarAccess() {
		
		try {
			
			IArchiveAccess access1 = ArchiveAccess.getJarAccessForClass(Testing.class);
			IArchiveAccess access2 = ArchiveAccess.getJarAccessForClass(ArchiveAccess.class);

			System.out.println("\naccess1:");
			for (String s : access1.listFull()) {
				System.out.println(s);
			}
			
			System.out.println("\naccess2:");
			for (String s : access2.listFull()) {
				System.out.println(s);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@TTT
	public static class Test {
		
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TTT {
		
	}
	
	public static void testClasspathBrowsing() {
		
		try {

			IArchiveAccess access = ArchiveAccess.getClasspathAccess();
			
			System.out.println("\nclasspath files:");
			for (String entry: access.listFull()) {
				System.out.println(entry);
			}

			System.out.println("\nclasspath files:");
			for (Class<?> entry: ArchiveClasses.getTypesAnnotatedWith(TTT.class)) {
				System.out.println(entry);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
