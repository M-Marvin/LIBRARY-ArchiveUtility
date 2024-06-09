package de.m_marvin.archiveutility;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ClasspathScaner {

	private static List<Class<?>> classpathTypes = null;
	
	public static void resetClasspathCache() {
		classpathTypes = null;
	}
	
	public static List<Class<?>> getClasspathTypes() throws IOException {
		if (classpathTypes == null) {
			IArchiveAccess classpathAccess = ArchiveAccess.getClasspathAccess();
			classpathTypes = Stream.of(classpathAccess.listFull())
					.filter(s -> s.endsWith(".class"))
					.map(s -> s.substring(0, s.lastIndexOf(".")).replaceAll("[$\\\\]", "."))
					.filter(s -> !s.substring(s.lastIndexOf(".") + 1).matches("[0-9]+"))
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
		return classpathTypes;
	}
	
	public static List<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) throws IOException {
		return getClasspathTypes().stream()
			.filter(c -> c.isAnnotationPresent(annotation))
			.toList();
	}
	
}
