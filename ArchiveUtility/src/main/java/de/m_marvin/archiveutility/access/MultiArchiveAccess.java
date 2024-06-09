package de.m_marvin.archiveutility.access;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import de.m_marvin.archiveutility.IArchiveAccess;

public class MultiArchiveAccess implements IArchiveAccess {

	private IArchiveAccess[] access;
	
	public MultiArchiveAccess(IArchiveAccess... accessArr) {
		this.access = accessArr;
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
	
}
