package de.m_marvin.archiveutility;

import java.io.IOException;
import java.io.InputStream;

public class Testing {

	public static void main(String... test) throws IOException {
		
		System.out.println(ArchiveUtility.getArchivePath());;
		System.out.println(ArchiveUtility.isInArchive() ? "In Archive" : "In Folder");

		for (String item : ArchiveUtility.list("test/")) {
			
			System.out.println("-> " + item);
			
		}
		System.out.println("---");
		for (String item : ArchiveUtility.listFiles("test/")) {
			
			System.out.println("-> " + item);
			
		}
		System.out.println("---");
		for (String item : ArchiveUtility.listFolders("test/")) {
			
			System.out.println("-> " + item);
			
		}
		
		InputStream is = ArchiveUtility.openFile("test/subfolder/test2.txt");
		System.out.println(is);
		is.close();
	}
	
}
