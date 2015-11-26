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
package com.viltgroup.xcp.jenkins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;

import com.viltgroup.xcp.jenkins.maven.M2EPreferences;
import com.viltgroup.xcp.jenkins.maven.XcpMavenSettings;
import com.viltgroup.xcp.jenkins.model.XcpEnvironmentInstance;

/**
 * Custom build wrapper that injects maven settings.xml, required for building a xCP project, into built-in jenkins Maven action.
 *
 */
public class XCPBuildWrapper extends BuildWrapper {

	public final String xcpEnvId;
	public final String buildPath;
	public final boolean usePrivateRepository;
	
	public @DataBoundConstructor XCPBuildWrapper(String xcpEnvId, String buildPath, boolean usePrivateRepository) {
		this.xcpEnvId = xcpEnvId;
		this.buildPath = buildPath;
		this.usePrivateRepository = usePrivateRepository;
	}
	
	@Override
	public Environment setUp(@SuppressWarnings("rawtypes") final AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
		
		// losd xcp environment
		XcpEnvironmentInstance xcpEnv = XCPEnvironmentsConfig.get().getById(xcpEnvId);
		if (xcpEnv == null) {
			throw new RuntimeException("xCP Runtime environment not found!");
		}

		// prepare build path
		FilePath workspacePath = build.getWorkspace();
		FilePath buildFilePath = workspacePath.child(buildPath);
		FilePath xcpWorkspacePath = buildFilePath.child("workspace");
		final FilePath userHomePath = buildFilePath.child("home");

		// clear previous xCP Build
		clearPreviousXCPBuild(xcpWorkspacePath, listener.getLogger());

		setupMavenSettings(userHomePath, xcpWorkspacePath, xcpEnv.xcpDesignerPath, usePrivateRepository);

		return new Environment() {
			@Override
			public void buildEnvVars(Map<String, String> env) {
				// set user home to build path
				env.put("MAVEN_OPTS", String.format("-Duser.home=%s",userHomePath));
			}
		};
	}
	
	private void clearPreviousXCPBuild(FilePath xcpWorkspacePath, PrintStream logger) {
		try {
			if (xcpWorkspacePath.exists()) {
				logger.print("Deleting previous build from ");
				logger.println(xcpWorkspacePath.getRemote());
				xcpWorkspacePath.deleteContents();
				xcpWorkspacePath.delete();
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error deleting previous xCP build.", ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Error deleting previous xCP build.", ex);
		}
	}
	
	public void prepareXCPDesignerDirectory(Path xcpDesignerPath, Path runPath) throws IOException 
	{
		class XCPDesignerCopyVisitor extends SimpleFileVisitor<Path> {
		    private Path xcpDesignerPath;
		    private Path toPath;

		    private List<Path> dirsToLink;
		    private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
		    
		    public XCPDesignerCopyVisitor(Path xcpDesignerPath, Path toPath) {
		    	this.xcpDesignerPath = xcpDesignerPath;
		    	this.toPath = toPath;
		    	
		    	this.dirsToLink = new ArrayList<Path>();
		    	this.dirsToLink.add((new File(xcpDesignerPath.toFile(), "features")).toPath());
		    	this.dirsToLink.add((new File(xcpDesignerPath.toFile(), "jre")).toPath());
		    	this.dirsToLink.add((new File(xcpDesignerPath.toFile(), "maven")).toPath());
		    	this.dirsToLink.add((new File(xcpDesignerPath.toFile(), "OptionalLibs")).toPath());
		    	this.dirsToLink.add((new File(xcpDesignerPath.toFile(), "plugins")).toPath());
		    	this.dirsToLink.add((new File(xcpDesignerPath.toFile(), "readme")).toPath());
		    }

		    @Override
		    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		        Path targetPath = toPath.resolve(xcpDesignerPath.relativize(dir));
		        if (this.dirsToLink.contains(dir) && !targetPath.toFile().exists()) {
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
		        Files.copy(file, toPath.resolve(xcpDesignerPath.relativize(file)), copyOption);
		        return FileVisitResult.CONTINUE;
		    }
		}
		Files.walkFileTree(xcpDesignerPath, new XCPDesignerCopyVisitor(xcpDesignerPath, runPath));
	}
	
	private void setupMavenSettings(FilePath homePath, FilePath xcpWorkspacePath, String xcpDesignerPath, boolean usePrivateRepository) {
		try {
			// generate %BUILD_PATH%/home/.m2/settings.xml file 
			FilePath mavenSettingsFilePath = homePath.child(".m2").child("settings.xml").absolutize();
			mavenSettingsFilePath.getParent().mkdirs();
			String localRepositoryPath = "";
			if (usePrivateRepository) {
				localRepositoryPath = mavenSettingsFilePath.getParent().child("repository").absolutize().getRemote();
			}
			XcpMavenSettings.generateFile(xcpWorkspacePath.getRemote(), xcpDesignerPath, localRepositoryPath, mavenSettingsFilePath);

			// generate %BUILD_PATH%/workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.m2e.core.prefs file
			FilePath m2ePreferencesPath = xcpWorkspacePath.child(".metadata").child(".plugins").child("org.eclipse.core.runtime").child(".settings").child("org.eclipse.m2e.core.prefs");
			m2ePreferencesPath.getParent().mkdirs();
			M2EPreferences.generateFile(mavenSettingsFilePath.getRemote(), m2ePreferencesPath);
		} catch (IOException ex) {
			throw new RuntimeException("Error deleting previous xCP build.", ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Error deleting previous xCP build.", ex);
		}
	}

	@Override
    public Descriptor<BuildWrapper> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final XCPBuildWrapperDescriptor DESCRIPTOR = new XCPBuildWrapperDescriptor();

}
