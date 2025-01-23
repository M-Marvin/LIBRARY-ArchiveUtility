package de.m_marvin.archiveutility.classes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;
import java.util.stream.Stream;

import de.m_marvin.archiveutility.access.IArchiveAccess;

public class ArchiveClassLoader extends SecureClassLoader {
	
	private final ClassLoader parentLoader;
	private final IArchiveAccess archiveAccess;
	
	public ArchiveClassLoader(IArchiveAccess archiveAccess) {
		this(null, archiveAccess);
	}
	
	public ArchiveClassLoader(ClassLoader parentLoader, IArchiveAccess archiveAccess) {
		this.parentLoader = parentLoader;
		this.archiveAccess = archiveAccess;	
	}
	
	public IArchiveAccess getArchiveAccess() {
		return archiveAccess;
	}
	
	public ClassLoader getParentLoader() {
		return parentLoader;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Optional<String> classPath = Stream.of(archiveAccess.listFull())
				.filter(s -> s.endsWith(".class"))
				.filter(s -> s.substring(0, s.lastIndexOf(".")).replace('\\', '.').equals(name))
				.findAny();
		
		if (classPath.isEmpty()) {
			if (this.parentLoader == null) throw new ClassNotFoundException(name);
			return this.parentLoader.loadClass(name);
		} else {
			try {
				InputStream classStream = this.archiveAccess.open(classPath.get());
				byte[] classBytes = classStream.readAllBytes();
				classStream.close();
				URL codeSourceURL = this.archiveAccess.getRootURL(classPath.get());
				CodeSource codeSource = new CodeSource(codeSourceURL, (CodeSigner[]) null);
				return defineClass(name, classBytes, 0, classBytes.length, codeSource);
			} catch (MalformedURLException e) {
				throw new ClassNotFoundException("failed to query archvie code source URL for: " + name, e);
			} catch (IOException e) {
				throw new ClassNotFoundException(name, e);
			}
		}
	}
	
	@Override
	protected URL findResource(String name) {
		if (this.archiveAccess.exists(name)) {
			try {
				return this.archiveAccess.getURL(name);
			} catch (MalformedURLException e) {
				return null;
			}
		} else {
			if (this.parentLoader != null) this.parentLoader.getResource(name);
			return null;
		}
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		if (this.archiveAccess.exists(name)) {
			try {
				return Collections.enumeration(Arrays.asList(this.archiveAccess.getURLs(name)));
			} catch (MalformedURLException e) {
				throw new IOException("URL exception while querying archives", e);
			}
		} else {
			if (this.parentLoader != null) this.parentLoader.getResource(name);
			return null;
		}
	}
	
}
