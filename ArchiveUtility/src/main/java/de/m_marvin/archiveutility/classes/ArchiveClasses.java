package de.m_marvin.archiveutility.classes;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import de.m_marvin.archiveutility.ArchiveAccess;
import de.m_marvin.archiveutility.access.IArchiveAccess;

public class ArchiveClasses {
	
	private ArchiveClasses() {
		throw new UnsupportedOperationException();
	}
	
	private static Map<IArchiveAccess, ArchiveClassLoader> classloaders = new HashMap<>();
	
	public static ArchiveClassLoader getClassLoader(IArchiveAccess archiveAccess) {
		if (!classloaders.containsKey(archiveAccess))
			classloaders.put(archiveAccess, new ArchiveClassLoader(archiveAccess));
		return classloaders.get(archiveAccess);
	}
	
	public static List<Class<?>> getArchiveClassTypes(IArchiveAccess archiveAccess) throws IOException {
		ArchiveClassLoader classLoader = getClassLoader(archiveAccess);
		return Stream.of(archiveAccess.listFull())
				.filter(s -> s.endsWith(".class"))
				.map(s -> s.substring(0, s.lastIndexOf(".")).replace('\\', '.'))
				.filter(s -> !s.substring(s.lastIndexOf("$") + 1).matches("[0-9]+"))
				.map(s -> {
					try {
						return classLoader.loadClass(s);
					} catch (Throwable e) {
						return (Class<?>) null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}

	public static List<Class<?>> getClasspathTypes() throws IOException {
		return Stream.of(ArchiveAccess.getClasspathAccess().listFull())
				.filter(s -> s.endsWith(".class"))
				.map(s -> s.substring(0, s.lastIndexOf(".")).replace('\\', '.'))
				.filter(s -> !s.substring(s.lastIndexOf("$") + 1).matches("[0-9]+"))
				.map(s -> {
					try {
						return ClassLoader.getSystemClassLoader().loadClass(s);
					} catch (Throwable e) {
						return (Class<?>) null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}
	
	public static List<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) throws IOException {
		return getClasspathTypes().stream()
			.filter(c -> c.isAnnotationPresent(annotation))
			.toList();
	}

	public static List<Class<?>> getTypesAnnotatedWith(IArchiveAccess archiveAccess, Class<? extends Annotation> annotation) throws IOException {
		return getArchiveClassTypes(archiveAccess).stream()
			.filter(c -> c.isAnnotationPresent(annotation))
			.toList();
	}
	
}
