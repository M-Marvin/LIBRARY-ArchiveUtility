package de.m_marvin.archiveutility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import de.m_marvin.archiveutility.access.FileSystemAccess;
import de.m_marvin.archiveutility.access.MultiArchiveAccess;
import de.m_marvin.archiveutility.access.ZipFileAccess;

public class ArchiveAccess {
	
	public static IArchiveAccess getJarAccessForClass(Class<?> clazz) throws IOException {
		try {
			File archiveFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			return getArchiveAccess(archiveFile);
		} catch (Exception e) {
			throw new IOException("could not get code source location of class: " + clazz.getName(), e);
		}
	}
	
	public static IArchiveAccess getClasspathAccess() throws IOException {
		String[] classpath = ((String) System.getProperty("java.class.path")).split(";");
		List<IArchiveAccess> access = new ArrayList<>();
		for (String cp : classpath) {
			File classFile = new File(cp);
			if (!classFile.exists()) continue;
			access.add(getArchiveAccess(classFile));
		}
		return new MultiArchiveAccess(access.toArray(IArchiveAccess[]::new));
	}
	
	public static IArchiveAccess getArchiveAccess(File archivePath) throws IOException {
		if (!archivePath.exists()) {
			throw new FileNotFoundException("archive not found: " + archivePath);
		} else {
			if (archivePath.isFile()) {
				try {
					ZipFile zip = new ZipFile(archivePath);
					return new ZipFileAccess(zip);
				} catch (ZipException e) {
					throw new IOException("could not open zip archive: " + archivePath, e);
				}
			} else {
				return new FileSystemAccess(archivePath);
			}
		}
	}
	
}
