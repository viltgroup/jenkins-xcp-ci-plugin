package com.vilt.xcp.jenkins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;

import com.vilt.xcp.jenkins.maven.XcpMavenSettings;
import com.vilt.xcp.jenkins.model.XcpEnvironmentInstance;

public class XCPBuildWrapper extends BuildWrapper {

	public final String xcpEnvId;
	public final String buildPath;
	
	public @DataBoundConstructor XCPBuildWrapper(String xcpEnvId, String buildPath) {
		this.xcpEnvId = xcpEnvId;
		this.buildPath = buildPath;
	}
	
	@Override
	public Environment setUp(final AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {
		
		return new Environment() {

			@Override
			public void buildEnvVars(Map<String, String> env) {
				// losd xcp environment
				XcpEnvironmentInstance xcpEnv = XCPEnvironmentsConfig.get().getById(xcpEnvId);
				if (xcpEnv == null) {
					throw new RuntimeException("xCP Runtime environment not found!");
				}

				// prepare build path
				FilePath workspacePath = build.getWorkspace();
				FilePath buildFilePath = workspacePath.child(buildPath);

				// generate %BUILD_PATH%/.m2/settings.xml file 
				String settingsPath = String.format("%s%s.m2%ssettings.xml",buildFilePath,File.separator,File.separator);
				File settingsFile = XcpMavenSettings.generateFile(xcpEnv.xcpDesignerPath, settingsPath);
				
				// set user home to build path
				env.put("MAVEN_OPTS", String.format("-Duser.home=%s",buildFilePath));
			}
			
		};
	}
	
	@Override
    public Descriptor<BuildWrapper> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final XCPBuildWrapperDescriptor DESCRIPTOR = new XCPBuildWrapperDescriptor();

}
