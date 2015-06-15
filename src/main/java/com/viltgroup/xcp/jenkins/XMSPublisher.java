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
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.util.Secret;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.viltgroup.xcp.jenkins.model.DataPolicy;
import com.viltgroup.xcp.jenkins.model.DeployEnvType;
import com.viltgroup.xcp.jenkins.model.DeployMethod;
import com.viltgroup.xcp.jenkins.model.XcpEnvironmentInstance;
import com.viltgroup.xcp.xms.xmsdeployer.IXMSPublishConfig;
import com.viltgroup.xcp.xms.xmsdeployer.XMSExecutionWrapper;

/**
 * Sample {@link Builder}.
 *
s * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link XMSPublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 * 
 */
public class XMSPublisher extends Publisher implements IXMSPublishConfig {

	/**
	 * Hold an instance of the Descriptor implementation of this publisher.
	 */
	@Extension
	public static final XMSPublisherDescriptor DESCRIPTOR = new XMSPublisherDescriptor();
	
	// Configs
	private final String xcpEnvId;
	private final String xcpAppPackagePath;
	private final String xcpAppConfigPath;

	private final String environment;
	private final DataPolicy dataPolicy;
	private final DeployMethod deployMethod;
	private final DeployEnvType deployEnvType;

	private final boolean xploreIndexing;
	private final boolean validateOnly;

	private final String xmsUsername;
	private final Secret xmsPassword;
	private final String xmsServerHost;
	private final String xmsServerPort;
	private final String xmsServerSchema;
	private final String xmsServerContextPath;

	private final String workPath;
	private final String javaOpts;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
	@DataBoundConstructor public XMSPublisher(String xcpEnvId, String xcpAppPackagePath, String xcpAppConfigPath,
			String environment, DataPolicy dataPolicy,
			DeployMethod deployMethod, DeployEnvType deployEnvType,
			boolean xploreIndexing, boolean validateOnly, String xmsUsername,
			Secret xmsPassword, String xmsServerHost, String xmsServerPort,
			String xmsServerSchema, String xmsServerContextPath,
			String workPath, String javaOpts) {
		this.xcpEnvId = xcpEnvId;
		this.xcpAppPackagePath = xcpAppPackagePath;
		this.xcpAppConfigPath = xcpAppConfigPath;
		this.environment = environment;
		this.dataPolicy = dataPolicy;
		this.deployMethod = deployMethod;
		this.deployEnvType = deployEnvType;
		this.xploreIndexing = xploreIndexing;
		this.validateOnly = validateOnly;
		this.xmsUsername = xmsUsername;
		this.xmsPassword = xmsPassword;
		this.xmsServerHost = xmsServerHost;
		this.xmsServerPort = xmsServerPort;
		this.xmsServerSchema = xmsServerSchema;
		this.xmsServerContextPath = xmsServerContextPath;
		this.workPath = workPath;
		this.javaOpts = javaOpts;
	}

	public String getXcpAppPackagePath() {
		return xcpAppPackagePath;
	}
	public String getXcpAppConfigPath() {
		return xcpAppConfigPath;
	}

	public String getEnvironment() {
		return environment;
	}

	public DeployMethod getDeployMethod() {
		return deployMethod;
	}
	public DataPolicy getDataPolicy() {
		return dataPolicy;
	}
	public DeployEnvType getDeployEnvType() {
		return deployEnvType;
	}

	public boolean isXploreIndexing() {
		return xploreIndexing;
	}
	public boolean isValidateOnly() {
		return validateOnly;
	}

	public String getXmsUsername() {
		return xmsUsername;
	}
	public String getXmsPassword() {
		return Secret.toString(xmsPassword);
	}
    public String getXmsServerHost() {
		return xmsServerHost;
	}
	public String getXmsServerPort() {
		return xmsServerPort;
	}
	public String getXmsServerSchema() {
		return xmsServerSchema;
	}
	public String getXmsServerContextPath() {
		return xmsServerContextPath;
	}

	public String getXmsToolsPath() {
		String xmsToolsPath = "";
        for(XcpEnvironmentInstance xcpEnvironmentInstance : DESCRIPTOR.getXcpEnvironmentInstances()) {
            if(xcpEnvId.equals(xcpEnvironmentInstance.xcpEnvId)) {
            	xmsToolsPath = xcpEnvironmentInstance.xmsToolsPath;
            }
        }
		return xmsToolsPath;
	}
	public String getXcpEnvId() {
		return xcpEnvId;
	}
	public String getWorkPath() {
		return workPath;
	}
	public String getJavaOpts() {
		return javaOpts;
	}

	@Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
		XMSExecutionWrapper xmsExecution = new XMSExecutionWrapper();
		
		// Construct xmsWorkPath based on current workspace.
		FilePath workspacePath = build.getWorkspace();
		FilePath xmsWorkPath = workspacePath.child(this.getWorkPath());
		
		return xmsExecution.run(xmsWorkPath.getRemote(), this, listener.getLogger());
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public XMSPublisherDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}

