package de.m_marvin.archiveutility;

import java.io.IOException;
import java.io.InputStream;

public class Testing {

	public static void main(String... test) throws IOException {
		
		ArchiveUtility utility = new ArchiveUtility(Testing.class);
		
		System.out.println(utility.getArchivePath());;
		System.out.println(utility.isInArchive() ? "In Archive" : "In Folder");

		for (String item : utility.list("test/")) {
			
			System.out.println("-> " + item);
			
		}
		System.out.println("---");
		for (String item : utility.listFiles("test/")) {
			
			System.out.println("-> " + item);
			
		}
		System.out.println("---");
		for (String item : utility.listFolders("test/")) {
			
			System.out.println("-> " + item);
			
		}
		
		InputStream is = utility.openFile("test/subfolder/test2.txt");
		System.out.println(is);
		is.close();
	}
	
}
