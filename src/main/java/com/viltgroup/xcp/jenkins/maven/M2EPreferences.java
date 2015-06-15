/*
 * Copyright 2015 VILT Group, www.vilt-group.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.viltgroup.xcp.jenkins.maven;

import hudson.FilePath;

import java.io.IOException;

import com.viltgroup.xcp.jenkins.utils.OSPathHacks;

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