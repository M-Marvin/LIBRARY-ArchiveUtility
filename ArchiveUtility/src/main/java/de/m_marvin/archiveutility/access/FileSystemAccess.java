package de.m_marvin.archiveutility.access;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import de.m_marvin.archiveutility.IArchiveAccess;

public class FileSystemAccess implements IArchiveAccess {

	private File root;
	
	public FileSystemAccess(File rootFolder) {
		this.root = rootFolder;
	}
	
	@Override
	public InputStream open(String path) throws IOException {
		File file = new File(this.root, path);
		if (!file.isFile()) throw new IOException("fs entry not found: " + path);
		return new FileInputStream(file);
	}

	@Override
	public boolean isFile(String path) {
		File file = new File(this.root, path);
		return file.isFile();
	}
	
	@Override
	public String[] listFull() {
		
		List<String> files = new ArrayList<>();
		Queue<File> tovisit = new ArrayDeque<>();
		tovisit.add(this.root);
		while (tovisit.size() > 0) {
			
			File rf = tovisit.poll();
			List<File> folders = Stream.of(rf.list())
				.map(n -> new File(rf, n))
				.filter(File::isDirectory)
				.toList();
			tovisit.addAll(folders);
			
			Stream.of(rf.listFiles())
				.filter(File::isFile)
				.map(f -> this.root.toPath().relativize(f.toPath()))
				.map(p -> p.toString())
				.forEach(s -> files.add(s));
			
		}
		
		return files.toArray(String[]::new);
	}

	@Override
	public String[] list(String path) {
		File folder = new File(this.root, path);
		if (!folder.isDirectory()) return new String[0];
		return Stream.of(folder.list())
				.map(n -> new File(path, n))
				.map(File::getPath)
				.toArray(String[]::new);
	}

	@Override
	public String[] listFiles(String path) {
		File folder = new File(this.root, path);
		if (!folder.isDirectory()) return new String[0];
		return Stream.of(folder.list())
				.filter(n -> new File(folder, n).isFile())
				.map(n -> new File(path, n))
				.map(File::getPath)
				.toArray(String[]::new);
	}

	@Override
	public String[] listFolders(String path) {
		File folder = new File(this.root, path);
		if (!folder.isDirectory()) return new String[0];
		return Stream.of(folder.list())
				.filter(n -> new File(folder, n).isDirectory())
				.map(n -> new File(path, n))
				.map(File::getPath)
				.toArray(String[]::new);
	}

}
