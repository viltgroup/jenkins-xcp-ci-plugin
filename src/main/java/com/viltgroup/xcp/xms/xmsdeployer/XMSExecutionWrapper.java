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
package com.viltgroup.xcp.xms.xmsdeployer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class XMSExecutionWrapper {

	/**
	 * Calls XMS Tools to execute a deploy
	 * 
	 * @param xmsWorkPath - Path to execution directory (should start empty).
	 * @param publishConfig - XMS deployment configuration
	 * @param consoleOutput - Print stream where to write the console output
	 * @return
	 *	true if deployment succeeds
	 */
	public boolean run(String xmsWorkPath, IXMSPublishConfig publishConfig, PrintStream consoleOutput) {
		String javaHome = System.getProperty("java.home");

		try {
			// prepare xms work directory
			prepareXMSDirectory(Paths.get(publishConfig.getXmsToolsPath()), Paths.get(xmsWorkPath));
			
			// prepare xms-server.properties
			prepareXMSConnectionProperties(xmsWorkPath, publishConfig);

			// prepare script to run
			String runScriptPath = prepareRunScript(xmsWorkPath, publishConfig);
			
			String javaExecPath = String.format("%s%sbin%sjava",javaHome,File.separator,File.separator);
	
			List<String> commandAndArguments = new ArrayList<String>();
			commandAndArguments.add(javaExecPath);
			String javaOptsStr = publishConfig.getJavaOpts();
			if (javaOptsStr != null && !javaOptsStr.isEmpty()) {
				String[] javaOpts = javaOptsStr.split(" ");
				commandAndArguments.addAll(Arrays.asList(javaOpts));
			}
			commandAndArguments.add("-classpath");
			commandAndArguments.add(computeXMSClasspath(xmsWorkPath));
			commandAndArguments.add(String.format("-Dxms.username=%s", publishConfig.getXmsUsername()));
			commandAndArguments.add(String.format("-Dxms.password=%s", publishConfig.getXmsPassword()));
			commandAndArguments.add(String.format("-Dxms.mode=%s", "server"));
			commandAndArguments.add(String.format("-Dxms.input.file=%s", runScriptPath));
			commandAndArguments.add(String.format("-Dxms.tools.home=%s", xmsWorkPath));
			commandAndArguments.add(String.format("-DXMS_DATA_DIR=%s", xmsWorkPath));
			
			// main class
			commandAndArguments.add("com.documentum.xms.cli.XmsConsole");

			// debug command line execution
			{
				StringBuffer commandLineDebugBuffer = new StringBuffer();
				for (String aux : commandAndArguments) {
					if (commandLineDebugBuffer.length() > 0) {
						commandLineDebugBuffer.append(" ");
					}
					if (aux.startsWith("-Dxms.password=")) {
						// don't include password in the log
						commandLineDebugBuffer.append("-Dxms.password=****");
					} else {
						commandLineDebugBuffer.append(aux);	
					}
				}
				consoleOutput.println(String.format("Executing xMS:\n> %s", commandLineDebugBuffer.toString()));
			}
			
			ProcessBuilder processBuilder = new ProcessBuilder(commandAndArguments);
			// run directory needs to be inside bin/ 
			processBuilder.directory(new File(xmsWorkPath, "bin"));
			Process process = processBuilder.start();
			
			boolean deploySucceeded = false;
			Integer result = null;
			String outputLine = null;

			BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			try {
				do {
					while ((outputLine = stdout.readLine ()) != null) {
						consoleOutput.println(outputLine);
						deploySucceeded = deploySucceeded || outputLine.endsWith("Deploying application was successful");
					}
					while ((outputLine = stderr.readLine ()) != null) {
						consoleOutput.format("***%s",outputLine);
					}
					try {
						result = process.exitValue();
				    } catch (IllegalThreadStateException itse) {
				    	// process still running
				    }
				}
				while(result == null);
			} finally {
				stdout.close();
				stderr.close();
			}
			
			return (result != null && result == 0 && deploySucceeded);

		} catch (IOException ioex) {
			consoleOutput.println("*** FAILED ***");
			ioex.printStackTrace(consoleOutput);
			return false;
		}
	}

	public void prepareXMSDirectory(Path xmsToolsPath, Path workPath) throws IOException 
	{
		class XMSToolsCopyVisitor extends SimpleFileVisitor<Path> {
		    private Path xmsToolsPath;
		    private Path toPath;
		    private Path xmsToolsLibPath;
		    private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
		    
		    public XMSToolsCopyVisitor(Path xmsToolsPath, Path toPath) {
		    	this.xmsToolsPath = xmsToolsPath;
		    	this.toPath = toPath;
		    	
		    	this.xmsToolsLibPath = (new File(xmsToolsPath.toFile(), "lib")).toPath();
		    }

		    @Override
		    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		        Path targetPath = toPath.resolve(xmsToolsPath.relativize(dir));
		        if (xmsToolsLibPath.equals(dir) && !targetPath.toFile().exists()) {
		        	// For windows, in order to be able to create a symbolic link you need to disable UAC or:
		        	// Run secpol.msc; Go to Security Settings|Local Policies|User Rights Assignment|Create symbolic links; select your username
		        	Files.createSymbolicLink(targetPath, dir);
		        	return FileVisitResult.SKIP_SUBTREE;
		        }

		        if(!Files.exists(targetPath)){
		            Files.createDirectory(targetPath);
		        }
		        return FileVisitResult.CONTINUE;
		    }

		    @Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		        Files.copy(file, toPath.resolve(xmsToolsPath.relativize(file)), copyOption);
		        return FileVisitResult.CONTINUE;
		    }
		}
		Files.walkFileTree(xmsToolsPath, new XMSToolsCopyVisitor(xmsToolsPath, workPath));
	}
	
	public void prepareXMSConnectionProperties(String xmsDataPath, IXMSPublishConfig publishConfig) throws IOException 
	{
		Properties xmsProperties = new Properties();
		xmsProperties.setProperty("xms-server-host", publishConfig.getXmsServerHost());
		xmsProperties.setProperty("xms-server-port", publishConfig.getXmsServerPort());
		xmsProperties.setProperty("xms-server-schema", publishConfig.getXmsServerSchema());
		xmsProperties.setProperty("xms-server-context-path", publishConfig.getXmsServerContextPath());
		
		File xmsConfigDirectory = new File(xmsDataPath, "config");
		xmsConfigDirectory.mkdirs();
		File xmsConfigFile = new File(xmsConfigDirectory, "xms-server.properties");
		FileWriter xmsPropertiesWriter = null;
		try {
			xmsPropertiesWriter = new FileWriter(xmsConfigFile);
			xmsProperties.store(xmsPropertiesWriter, "Auto-generated properties file");
		} finally {
			if (xmsPropertiesWriter != null) xmsPropertiesWriter.close();
		}
	}
	
	public String computeXMSClasspath(String xmsPath) {
		StringBuilder buffer = new StringBuilder();

		String configPath = String.format("%s%sconfig",xmsPath,File.separator);
		buffer.append(configPath);
		buffer.append(File.pathSeparator);
		buffer.append(String.format("%s%ssystem",configPath,File.separator));
		buffer.append(File.pathSeparator);
		buffer.append(String.format("%s%sproperties",configPath,File.separator));
		
		File libDirectory = new File(xmsPath, "lib");
		buffer.append(File.pathSeparator);
		buffer.append(String.format("%s%s*",libDirectory.getAbsolutePath(),File.separator));
		for(File file : libDirectory.listFiles()) {
			if (file.isDirectory()) {
				buffer.append(File.pathSeparator);
				buffer.append(String.format("%s%s*",file.getAbsolutePath(),File.separator));
			}
		}
		
		return buffer.toString();
	}

	public String prepareRunScript(String xmsWorkPath, IXMSPublishConfig publishConfig) throws IOException {
		String runScriptPath = String.format("%s%srunscript.xms", xmsWorkPath, File.separator);

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(runScriptPath);
			writer.format("deploy-xcp-application --configuration-file \"%s\"", publishConfig.getXcpAppConfigPath());
			writer.format(" --war-file \"%s\"", publishConfig.getXcpAppPackagePath());
			writer.format(" --environment %s", publishConfig.getEnvironment());
			writer.format(" --deployment-method %s", publishConfig.getDeployMethod());
			writer.format(" --deployment-env-type %s", publishConfig.getDeployEnvType());
			writer.format(" --data-policy %s", publishConfig.getDataPolicy());
			writer.format(" --xploreindexing %s", publishConfig.isXploreIndexing());
			writer.format(" --validateonly %s", publishConfig.isValidateOnly());
			if (publishConfig.getBatchSize() != null) {
				writer.format(" --batch-size %s", publishConfig.getBatchSize());				
			}
		} finally {
			if (writer != null) writer.close();
		}
		return runScriptPath;		
	}
}
