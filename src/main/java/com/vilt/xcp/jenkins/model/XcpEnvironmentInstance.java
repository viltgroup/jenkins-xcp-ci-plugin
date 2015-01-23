package com.vilt.xcp.jenkins.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

import java.util.UUID;

import org.kohsuke.stapler.DataBoundConstructor;

public class XcpEnvironmentInstance extends AbstractDescribableImpl<XcpEnvironmentInstance> {

	public final String xcpEnvId;
	public final String name;
	public final String xmsToolsPath;
	public final String xcpDesignerPath;
	
	@DataBoundConstructor public XcpEnvironmentInstance(String xcpEnvId, String name, String xmsToolsPath, String xcpDesignerPath) {
		if (xcpEnvId == null || xcpEnvId.isEmpty()) {
			this.xcpEnvId = UUID.randomUUID().toString();
		} else {
			this.xcpEnvId = xcpEnvId;			
		}
		this.name = name;
		this.xmsToolsPath = xmsToolsPath;
		this.xcpDesignerPath = xcpDesignerPath;
	}

	@Extension
    public static class DescriptorImpl extends Descriptor<XcpEnvironmentInstance> {
        public String getDisplayName() { return ""; }
    }
}
