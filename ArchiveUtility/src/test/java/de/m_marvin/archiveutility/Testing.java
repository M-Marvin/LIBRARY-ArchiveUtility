package de.m_marvin.archiveutility;

import java.io.IOException;

public class Testing {

	public static void main(String... test) throws IOException {
		
		testClassJarAccess();
		testClasspathBrowsing();
		
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
	
	public static void testClasspathBrowsing() {
		
		try {

			IArchiveAccess access = ArchiveAccess.getClasspathAccess();
			
			System.out.println("\nclasspath files:");
			for (String entry: access.listFull()) {
				System.out.println(entry);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
