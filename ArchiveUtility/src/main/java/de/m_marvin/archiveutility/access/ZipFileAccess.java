package de.m_marvin.archiveutility.access;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.m_marvin.archiveutility.IArchiveAccess;

public class ZipFileAccess implements IArchiveAccess {

	private ZipFile zip;
	
	public ZipFileAccess(ZipFile zipFile) {
		this.zip = zipFile;
	}
	
	private static String formatZipEntry(String path) {
		String zp = path.replace('\\', '/');
		if (zp.startsWith("/")) zp = zp.substring(1);
		if (zp.endsWith("/")) zp = zp.substring(0, zp.length() - 1);
		return zp;
	}
	
	private static String formatFileSystem(String path) {
		return path.replace('/', '\\');
	}
	
	@Override
	public InputStream open(String path) throws IOException {
		String p = formatZipEntry(path);
		ZipEntry entry = this.zip.getEntry(p);
		if (entry == null) throw new IOException("zip entry not found: " + p);
		return this.zip.getInputStream(entry);
	}
	
	@Override
	public boolean isFile(String path) {
		String p = formatZipEntry(path);
		ZipEntry entry = this.zip.getEntry(p);
		return entry != null && !entry.isDirectory();
	}
	
	@Override
	public String[] listFull() {
		return this.zip.stream()
				.map(ZipEntry::getName)
				.map(ZipFileAccess::formatFileSystem)
				.toArray(i -> new String[i]);
	}
	
	private static boolean isChild(String parent, String child) {
		return child.startsWith(parent) && parent.length() < child.length() && child.charAt(parent.length()) == '/';
	}

	private static boolean isChildFolder(String parent, String child) {
		return child.startsWith(parent) && parent.length() < child.length() && child.charAt(parent.length()) == '/' && child.indexOf('/', parent.length() + 1) > 0;
	}

	private static boolean isChildFile(String parent, String child) {
		return child.startsWith(parent) && parent.length() < child.length() && child.charAt(parent.length()) == '/' && child.indexOf('/', parent.length() + 1) == -1;
	}
	
	private static String getDirectChild(String parent, String child) {
		String r = child.substring(parent.length());
		int i = r.indexOf('/', 1);
		return parent + r.substring(0, i > 0 ? i + 1 : r.length());
	}
	
	@Override
	public String[] list(String path) {
		String p = formatZipEntry(path);
		return this.zip.stream()
				.map(ZipEntry::getName)
				.filter(e -> isChild(p, e))
				.map(e -> getDirectChild(p, e))
				.distinct()
				.map(ZipFileAccess::formatFileSystem)
				.toArray(i -> new String[i]);
	}

	@Override
	public String[] listFiles(String path) {
		String p = formatZipEntry(path);
		return this.zip.stream()
				.map(ZipEntry::getName)
				.filter(e -> isChildFile(p, e))
				.map(e -> getDirectChild(p, e))
				.distinct()
				.map(ZipFileAccess::formatFileSystem)
				.toArray(i -> new String[i]);
	}

	@Override
	public String[] listFolders(String path) {
		String p = formatZipEntry(path);
		return this.zip.stream()
				.map(ZipEntry::getName)
				.filter(e -> isChildFolder(p, e))
				.map(e -> getDirectChild(p, e))
				.distinct()
				.map(ZipFileAccess::formatFileSystem)
				.toArray(i -> new String[i]);
	}
	
}
