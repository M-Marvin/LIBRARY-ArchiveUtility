package de.m_marvin.archiveutility.access;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileAccess implements IArchiveAccess, Closeable {

	private final ZipFile zip;
	private final URL location;
	
	public ZipFileAccess(ZipFile zipFile, URL location) {
		this.zip = zipFile;
		this.location = location;
	}

	public ZipFileAccess(URL location) throws IOException {
		this.zip = new ZipFile(location.getPath());
		this.location = location;
	}

	public ZipFileAccess(File file) throws IOException {
		this.zip = new ZipFile(file);
		this.location = file.toURI().toURL();
	}

	public ZipFileAccess(ZipFile zipFile) {
		this.zip = zipFile;
		this.location = null;
	}

	@Override
	public void close() throws IOException {
		this.zip.close();
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
	public boolean exists(String path) {
		String p = formatZipEntry(path);
		ZipEntry entry = this.zip.getEntry(p);
		return entry != null;
	}
	
	@Override
	public String[] listFull() {
		return this.zip.stream()
				.filter(z -> !z.isDirectory())
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
	
	@Override
	public URL getURL(String path) throws MalformedURLException {
		if (this.location == null) return null;
		return new URL("jar", "", this.location.toString() + "!/" + path);
	}
	
	@Override
	public URL getRootURL(String path) throws MalformedURLException {
		if (this.location == null) return null;
		return this.location;
	}
	
}
