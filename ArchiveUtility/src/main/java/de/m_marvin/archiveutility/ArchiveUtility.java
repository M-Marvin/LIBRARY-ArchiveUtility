package de.m_marvin.archiveutility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArchiveUtility {
	
	private boolean inArchive = false;
	private File archivePath = null;
	private boolean runsInIDE = false;
	private ZipFile archive = null;
	
	public ArchiveUtility(File archivePath) {
		this.archivePath = archivePath;
		this.inArchive = archivePath.isFile();
		this.runsInIDE = this.archivePath.getPath().endsWith("bin/main");
	}
	
	public boolean runsInIDE() {
		return runsInIDE;
	} 
	
	public ArchiveUtility(Class<?> referenceClass) {
		try {
			this.archivePath = new File(referenceClass.getProtectionDomain().getCodeSource().getLocation().toURI());
			this.inArchive = archivePath.isFile();
			this.runsInIDE = this.archivePath.getPath().endsWith("bin\\main") || this.archivePath.getPath().endsWith("bin\\test");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInArchive() {
		return inArchive;
	}
	
	public File getArchivePath() {
		return archivePath;
	}
	
	private String cleanPath(String path) {
		path = path.replace('\\', '/');
		if (path == null || path.isEmpty()) return "";
		if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
		if (path.startsWith("/")) path = path.substring(1);
		return path;
	}
	
	public String getParent(String path) {
		String p = cleanPath(path);
		int i = p.lastIndexOf("/");
		return i > 0 ? p.substring(0, i) : "";
	}
	
	public String getChild(String path, String file) {
		return cleanPath(path) + "/" + file;
	}
	
	public String getName(String path) {
		String p = cleanPath(path);
		int i = p.lastIndexOf("/");
		return i > 0 ? p.substring(i + 1, p.length()) : "";
	}
	
	public final Map<String, Boolean> path2isFileMap = new HashMap<>();
	
	public boolean isFile(String path) {
		String p = cleanPath(path);
		if (!path2isFileMap.containsKey(p)) {
			
			if (!isInArchive()) {
				if (runsInIDE()) {
					File binDir = this.archivePath.getParentFile();
					for (String entry : binDir.list()) {
						File folder = new File(binDir, entry);
						File file = new File(folder, path);
						if (folder.isDirectory() && file.exists()) {
							path2isFileMap.put(p, file.isFile());
							break;
						}
					}
				} else {
					path2isFileMap.put(p, new File(archivePath, path).isFile());
				}
			} else {
				
				try {
					if (this.archive == null) this.archive = new ZipFile(archivePath);
					ZipEntry entry = this.archive.getEntry(p);
					path2isFileMap.put(p, entry != null && !entry.isDirectory());
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				
			}
			
		}
		
		return path2isFileMap.containsKey(p) ? path2isFileMap.get(p) : false;
	}
	
	public boolean isFolder(String path) {
		return !isFile(path);
	}
	
	public boolean arePathsEqual(String pathA, String pathB) {
		if (pathA == null || pathB == null) return false;
		if (pathA == pathB) return true;
		return cleanPath(pathA).equalsIgnoreCase(cleanPath(pathB));
	}
	
	private final Map<String, String[]> path2childMap = new HashMap<>();
	
	public String[] list(String path) {
		
		String p = cleanPath(path);
		if (!path2childMap.containsKey(p)) {
			
			if (!isInArchive()) {
				
				if (runsInIDE()) {
					File binDir = this.archivePath.getParentFile();
					for (String entry : binDir.list()) {
						File folder = new File(binDir, entry);
						File folderPath = new File(folder, path);
						if (folder.isDirectory() && folderPath.exists()) {
							if (!folderPath.isDirectory())
								path2childMap.put(p, new String[0]);
							else
								path2childMap.put(p, folderPath.list());
							break;
						}
					}
				} else {
					File folderPath = new File(getArchivePath(), path);
					if (!folderPath.isDirectory())
						path2childMap.put(p, new String[0]);
					else
						path2childMap.put(p, folderPath.list());
				}
				
			} else {
				
				try {
					if (this.archive == null) this.archive = new ZipFile(archivePath);
					ZipEntry entry = this.archive.getEntry(p);
					if (entry == null || !entry.isDirectory()) {
						path2childMap.put(p, new String[0]);
					};
					
					path2childMap.put(p, 
							this.archive.stream()
							.map(ZipEntry::getName)
							.filter(fp -> arePathsEqual(getParent(fp), path))
							.map(this::getName)
							.toArray(i -> new String[i]));
					
				} catch (IOException e) {
					e.printStackTrace();
					return new String[0];
				}
				
			}
			
		}
		
		return path2childMap.containsKey(p) ? path2childMap.get(p) : new String[0];
	}
	
	public String[] listFiles(String path) {
		return Stream.of(list(path))
				.map(fp -> getChild(path, fp))
				.filter(fp -> isFile(fp))
				.map(fp -> getName(fp))
				.toArray(i -> new String[i]);
	}
	
	public String[] listFolders(String path) {
		return Stream.of(list(path))
				.map(fp -> getChild(path, fp))
				.filter(fp -> isFolder(fp))
				.map(fp -> getName(fp))
				.toArray(i -> new String[i]);
	}
	
	public InputStream openFile(String path) {
		if (!isFile(path)) return null;
		
		String p = cleanPath(path);

		if (!isInArchive()) {
			
			if (runsInIDE()) {
				File binDir = this.archivePath.getParentFile();
				for (String entry : binDir.list()) {
					File folder = new File(binDir, entry);
					File filePath = new File(folder, path);
					if (folder.isDirectory() && filePath.exists()) {
						if (!filePath.isFile()) return null;
						
						try {
							return new FileInputStream(filePath);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return null;
						}
					}
				}
				return null;
			} else {
				File filePath = new File(getArchivePath(), path);
				if (!filePath.isFile()) return null;
				
				try {
					return new FileInputStream(filePath);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
		} else {
			
			try {
				if (this.archive == null) this.archive = new ZipFile(archivePath);
				
				ZipEntry entry = this.archive.getEntry(p);
				if (entry == null || entry.isDirectory()) {
					return null;
				}
				
				return this.archive.getInputStream(entry);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
}
