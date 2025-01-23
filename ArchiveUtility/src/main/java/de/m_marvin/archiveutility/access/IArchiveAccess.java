package de.m_marvin.archiveutility.access;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public interface IArchiveAccess extends Closeable {
	
	public InputStream open(String path) throws IOException;
	public boolean isFile(String path);
	public boolean exists(String path);
	
	/**
	 * Lists all files in all sub-directories in this archive
	 * @return An String array containing the paths to the individual files, relative to the root of this archive
	 */
	public String[] listFull();
	
	/**
	 * Lists all files and folders in the specified directory within this archive, does not search sub-directories.
	 * @param path The path to the directory relative to the archive root
	 * @return An String array containing the paths to the individual files and folders, relative to the root of this archive
	 */
	public String[] list(String path);

	/**
	 * Lists all files in the specified directory within this archive, does not search sub-directories.
	 * @param path The path to the directory relative to the archive root
	 * @return An String array containing the paths to the individual files, relative to the root of this archive
	 */
	public String[] listFiles(String path);

	/**
	 * Lists all folders in the specified directory within this archive, does not search sub-directories.
	 * @param path The path to the directory relative to the archive root
	 * @return An String array containing the paths to the individual folders, relative to the root of this archive
	 */
	public String[] listFolders(String path);
	
	/**
	 * Returns the URL pointing to the same location as the archive root relative path.
	 * @param path The path to be converted to an URL
	 * @return The URL pointing to the same location as the path or null if the path is not valid or no URL could be computed
	 */
	public URL getURL(String path) throws MalformedURLException;
	
	/**
	 * Returns the URLs pointing to the same location as the archive root relative path in each available archive.
	 * @param path The path to be converted to an URL
	 * @return An array of URLs pointing to the location in the available archives, may be an empty array
	 */
	public default URL[] getURLs(String path) throws MalformedURLException {
		URL url = getURL(path);
		if (url == null) return new URL[0];
		return new URL[] { url };
	}
	
	/**
	 * Returns the URL pointing to the archive root of the first archive in which the given path exists (in case of multiple archives are accessible)
	 * @param path The path to be searched for in the archives, can be null
	 * @return The URL pointing to the archive root
	 */
	public URL getRootURL(String path) throws MalformedURLException;

	/**
	 * Returns the URLs pointing to the archives in which the given path exists (in case of multiple archives are accessible)
	 * @param path The path to be searched for in the archives, can be null
	 * @return An array of URLs pointing to the location of the archives, may be an empty array
	 */
	public default URL[] getRootURLs(String path) throws MalformedURLException {
		URL url = getRootURL(path);
		if (url == null) return new URL[0];
		return new URL[] { url };
	}
	
}
