package com.vilt.xcp.jenkins.maven;

import hudson.FilePath;

import java.io.IOException;

import com.vilt.xcp.jenkins.utils.OSPathHacks;

/**
 * Utility class that handles the generation of m2e preferences file for a given xCP Designer project. 
 *
 */
public class M2EPreferences {

	public static void generateFile(String mavenSettingsPath, FilePath outputPath) {
	    try {
	    	// create directories if needed
	    	outputPath.getParent().mkdirs();

	    	StringBuilder sb = new StringBuilder("eclipse.m2.userSettingsFile=");
	    	sb.append(OSPathHacks.escapePathForEclipseConf(mavenSettingsPath));
	    	sb.append("\neclipse.preferences.version=1");
	    	outputPath.write(sb.toString(), null);
	    } catch (IOException e) {
	    	throw new RuntimeException("Error generating xCP Designer m2e preferences file.", e);
		} catch (InterruptedException e) {
	    	throw new RuntimeException("Error generating xCP Designer m2e preferences file.", e);
		}
	}
}