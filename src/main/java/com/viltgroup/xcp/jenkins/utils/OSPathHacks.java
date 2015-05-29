package com.viltgroup.xcp.jenkins.utils;

public class OSPathHacks {

	public static String escapePathForEclipseConf(String path) {
		return path.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\:", "\\\\\\:");
	}
	
	public static String processFilePath(String path) {
		return path.replaceAll("\\\\", "/");
	}
	
}
