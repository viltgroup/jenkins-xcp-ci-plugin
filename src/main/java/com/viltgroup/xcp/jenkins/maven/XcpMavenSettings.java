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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import com.viltgroup.xcp.jenkins.utils.OSPathHacks;

import hudson.FilePath;

/**
 * Utility class that handles the generation of maven settings.xml for a given xCP Designer project. 
 *
 * <p>
 * See <tt>src/main/resources/com/viltgroup/xcp/jenkins/maven/XcpMavenSettings/template.jelly</tt>
 * for the actual template
 */
public class XcpMavenSettings {

	public static void generateFile(String workspacePath, String xcpDesignerPath, String localRepositoryPath, FilePath generatedFilePath) {
	    XMLOutput output = null;
	    try {
	    	// create directories if needed
	    	generatedFilePath.getParent().mkdirs();

	    	// prepare output writer
	        output = XMLOutput.createXMLOutput(generatedFilePath.write());

	        // load jelly template
	        String templateLocation = String.format("/%s/template.jelly",XcpMavenSettings.class.getName().replace('.', '/'));
	        URL templateURL = XcpMavenSettings.class.getResource(templateLocation);
	        
	        // generate settings.xml from template
	        Jelly jelly = new Jelly(); 
	        jelly.setUrl( templateURL );
	        Script script = jelly.compileScript();
	        // add xCP Designer path to the jelly context
	        JellyContext context = new JellyContext();
	        context.setVariable("workspacePath", OSPathHacks.processFilePath(workspacePath));
	        context.setVariable("xcpDesignerPath", xcpDesignerPath);

	        String xcpDesignerPathParsed = OSPathHacks.processFilePath(xcpDesignerPath);
        	// #1 - Starting from xCP Designer 2.2, internal maven repository is inside ./maven/designer instead of just ./maven
        	String mavenRepoPath22 = String.format("%s/maven/designer", xcpDesignerPathParsed);
	        if (new File(mavenRepoPath22).exists()) {
		        context.setVariable("xcpDesignerMavenPath", mavenRepoPath22);
	        } else {
	        	// fallback to xCP Designer 2.1 location
		        String mavenRepoPath = String.format("%s/maven", xcpDesignerPathParsed);
		        if (new File(mavenRepoPath).exists()) {
			        context.setVariable("xcpDesignerMavenPath", mavenRepoPath);
		        } else {
			    	throw new RuntimeException(String.format("Could not find xCP Designer internal maven repository. Path %s (for 2.1) and %s (for 2.2) do not exist.", mavenRepoPath, mavenRepoPath22));
		        }
	        }
	        context.setVariable("localRepositoryPath", OSPathHacks.processFilePath(localRepositoryPath));
	        script.run( context, output );
	        output.flush();
	    } catch (IOException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (JellyTagException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (JellyException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (InterruptedException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} finally {
	    	if (output != null) try { output.close(); } catch (IOException e) { }
	    }
	}
}