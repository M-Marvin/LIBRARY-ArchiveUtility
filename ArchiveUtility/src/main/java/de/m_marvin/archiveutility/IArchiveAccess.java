package de.m_marvin.archiveutility;

import java.io.IOException;
import java.io.InputStream;

public interface IArchiveAccess {
	
	public InputStream open(String path) throws IOException;
	public boolean isFile(String path);
	
	public String[] listFull();
	public String[] list(String path);
	public String[] listFiles(String path);
	public String[] listFolders(String path);
	
}
