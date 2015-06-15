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
package com.viltgroup.xcp.jenkins.model;

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
