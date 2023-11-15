package de.m_marvin.archiveutility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArchiveUtility {
	
	private static boolean inArchive = false;
	private static File archivePath = null;
	
	static {
		try {
			archivePath = new File(ArchiveUtility.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			inArchive = archivePath.isFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isInArchive() {
		return inArchive;
	}
	
	public static File getArchivePath() {
		return archivePath;
	}
	
	private static String cleanPath(String path) {
		if (path == null || path.isEmpty()) return "";
		if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
		if (path.startsWith("/")) path = path.substring(1);
		return path;
	}
	
	public static String getParent(String path) {
		String p = cleanPath(path);
		int i = p.lastIndexOf("/");
		return i > 0 ? p.substring(0, i) : "";
	}
	
	public static String getChild(String path, String file) {
		return cleanPath(path) + "/" + file;
	}
	
	public static String getName(String path) {
		String p = cleanPath(path);
		int i = p.lastIndexOf("/");
		return i > 0 ? p.substring(i + 1, p.length()) : "";
	}
	
	public static final Map<String, Boolean> path2isFileMap = new HashMap<>();
	
	public static boolean isFile(String path) {
		String p = cleanPath(path);
		if (!path2isFileMap.containsKey(p)) {
			
			if (!isInArchive()) {
				path2isFileMap.put(p, new File(archivePath, path).isFile());
			} else {

				try {
					ZipFile archive = new ZipFile(archivePath);
					ZipEntry entry = archive.getEntry(p);
					path2isFileMap.put(p, !entry.isDirectory());
					archive.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				
			}
			
		}
		
		return path2isFileMap.get(p);
	}
	
	public static boolean isFolder(String path) {
		return !isFile(path);
	}
	
	public static boolean arePathsEqual(String pathA, String pathB) {
		if (pathA == null || pathB == null) return false;
		if (pathA == pathB) return true;
		return cleanPath(pathA).equalsIgnoreCase(cleanPath(pathB));
	}
	
	private static final Map<String, String[]> path2childMap = new HashMap<>();
	
	public static String[] list(String path) {
		
		String p = cleanPath(path);
		if (!path2childMap.containsKey(p)) {
			
			if (!isInArchive()) {
				File folderPath = new File(getArchivePath(), path);
				if (!folderPath.isDirectory())
					path2childMap.put(p, new String[0]);
				else
					path2childMap.put(p, folderPath.list());
			} else {
				
				try {
					ZipFile archive = new ZipFile(archivePath);
					
					ZipEntry entry = archive.getEntry(path);
					if (!entry.isDirectory()) {
						archive.close();
						path2childMap.put(p, new String[0]);
					};
					
					path2childMap.put(p, 
							archive.stream()
							.map(ZipEntry::getName)
							.filter(fp -> arePathsEqual(getParent(fp), path))
							.map(ArchiveUtility::getName)
							.toArray(i -> new String[i]));
					
					archive.close();
				} catch (IOException e) {
					e.printStackTrace();
					return new String[0];
				}
				
			}
			
		}
		
		return path2childMap.get(p);
	}
	
	public static String[] listFiles(String path) {
		return Stream.of(list(path))
				.map(fp -> getChild(path, fp))
				.filter(fp -> isFile(fp))
				.map(fp -> getName(fp))
				.toArray(i -> new String[i]);
	}
	
	public static String[] listFolders(String path) {
		return Stream.of(list(path))
				.map(fp -> getChild(path, fp))
				.filter(fp -> isFolder(fp))
				.map(fp -> getName(fp))
				.toArray(i -> new String[i]);
	}
	
	public static InputStream openFile(String path) {
		if (!isFile(path)) return null;
		return ArchiveUtility.class.getResourceAsStream("/" + cleanPath(path));
	}
	
}
