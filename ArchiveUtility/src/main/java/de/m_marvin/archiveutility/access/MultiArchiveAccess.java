package de.m_marvin.archiveutility.access;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MultiArchiveAccess implements IArchiveAccess {

	private IArchiveAccess[] access;
	
	public MultiArchiveAccess(IArchiveAccess... accessArr) {
		this.access = accessArr;
	}

	@Override
	public void close() throws IOException {
		List<IOException> ex = new ArrayList<IOException>();
		for (IArchiveAccess a : this.access) {
			try {
				a.close();
			} catch (IOException e) {
				ex.add(e);
			}
		}
		
		if (ex.isEmpty()) return;
		
		IOException e = new IOException(ex.get(0));
		ex.remove(0);
		for (IOException e2 : ex) {
			e.addSuppressed(e2);
		}
		throw e;
	}
	
	@Override
	public InputStream open(String path) throws IOException {
		for (IArchiveAccess a : this.access) {
			if (!a.isFile(path)) continue;
			return a.open(path);
		}
		throw new IOException("archive entry not found: " + path);
	}

	@Override
	public boolean isFile(String path) {
		for (IArchiveAccess a : this.access) {
			if (a.isFile(path)) return true;
		}
		return false;
	}
	
	@Override
	public boolean exists(String path) {
		for (IArchiveAccess a : this.access) {
			if (a.exists(path)) return true;
		}
		return false;
	}
	
	@Override
	public String[] listFull() {
		return Stream.of(this.access)
			.flatMap(a -> Stream.of(a.listFull()))
			.toArray(String[]::new);
	}

	@Override
	public String[] list(String path) {
		return Stream.of(this.access)
				.flatMap(a -> Stream.of(a.list(path)))
				.toArray(String[]::new);
	}

	@Override
	public String[] listFiles(String path) {
		return Stream.of(this.access)
				.flatMap(a -> Stream.of(a.listFiles(path)))
				.toArray(String[]::new);
	}

	@Override
	public String[] listFolders(String path) {
		return Stream.of(this.access)
				.flatMap(a -> Stream.of(a.listFolders(path)))
				.toArray(String[]::new);
	}
	
	@Override
	public URL getURL(String path) throws MalformedURLException {
		for (IArchiveAccess a : this.access) {
			if (a.exists(path)) return a.getURL(path);
		}
		return null;
	}
	
	@Override
	public URL[] getURLs(String path) throws MalformedURLException {
		Set<URL> urls = new HashSet<>();
		for (IArchiveAccess a : this.access) {
			if (a.exists(path)) urls.add(a.getURL(path));
		}
		return urls.toArray(URL[]::new);
	}
	
	@Override
	public URL getRootURL(String path) throws MalformedURLException {
		for (IArchiveAccess a : this.access) {
			if (a.exists(path)) return a.getRootURL(path);
		}
		return null;
	}

	@Override
	public URL[] getRootURLs(String path) throws MalformedURLException {
		Set<URL> urls = new HashSet<>();
		for (IArchiveAccess a : this.access) {
			if (path == null || a.exists(path)) urls.add(a.getRootURL(path));
		}
		return urls.toArray(URL[]::new);
	}
	
}
