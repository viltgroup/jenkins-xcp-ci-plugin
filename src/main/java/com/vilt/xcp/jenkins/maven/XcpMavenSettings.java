package com.vilt.xcp.jenkins.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * Utility class that handles the generation of maven settings.xml for a given xCP Designer project. 
 *
 * <p>
 * See <tt>src/main/resources/com/vilt/xcp/jenkins/maven/XcpMavenSettings/template.jelly</tt>
 * for the actual template
 */
public class XcpMavenSettings {

	public static File generateFile(String xcpDesignerPath, String path) {
        File generatedFile = new File(path);
	    XMLOutput output = null;
	    try {
	    	// create directories if needed
	    	generatedFile.getParentFile().mkdirs();
	    	// create file if necessary
	    	generatedFile.createNewFile();
	    	// prepare output writer
	        output = XMLOutput.createXMLOutput(new FileWriter(generatedFile));

	        // load jelly template
	        String templateLocation = String.format("/%s/template.jelly",XcpMavenSettings.class.getName().replace('.', '/'));
	        URL templateURL = XcpMavenSettings.class.getResource(templateLocation);
	        
	        // generate settings.xml from template
	        Jelly jelly = new Jelly(); 
	        jelly.setUrl( templateURL );
	        Script script = jelly.compileScript();
	        // add xCP Designer path to the jelly context
	        JellyContext context = new JellyContext();
	        context.setVariable("xcpDesignerPath", xcpDesignerPath);
	        script.run( context, output );
	        output.flush();
	    } catch (IOException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (JellyTagException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (JellyException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} finally {
	    	if (output != null) try { output.close(); } catch (IOException e) { }
	    }
	    return generatedFile;
	}
}