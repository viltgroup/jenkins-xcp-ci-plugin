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

import java.util.List;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.viltgroup.xcp.jenkins.model.XcpEnvironmentInstance;
import com.viltgroup.xcp.jenkins.utils.FormValidations;

import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

/**
 * Descriptor for {@link XCPBuildWrapper}. Used as a singleton.
 * The class is marked as public so that it can be accessed from views.
 *
 * <p>
 * See <tt>src/main/resources/com/viltgroup/xcp/jenkins/XCPBuildWrapper/*.jelly</tt>
 * for the actual HTML fragment for the configuration screen.
 */
public final class XCPBuildWrapperDescriptor extends BuildWrapperDescriptor  {

	public XCPBuildWrapperDescriptor() {
        super(XCPBuildWrapper.class);
        // Load the persisted properties from file.
        load();
    }
 
    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
    	// support free style and maven projects
        return (item instanceof FreeStyleProject) || (item instanceof AbstractMavenProject); 
    }

    @Override
	public BuildWrapper newInstance(StaplerRequest req, JSONObject formData) throws FormException {
    	return req.bindJSON(XCPBuildWrapper.class, formData);
	}

    public List<XcpEnvironmentInstance> getXcpEnvironmentInstances() {
    	return XCPEnvironmentsConfig.get().xcpEnvironmentInstances;
    }

	@Override
    public String getDisplayName() {
        return "Documentum xCP Build";
    }
	
    /**
     * Performs on-the-fly validation of the form field 'buildPath'.
     * 
     * @param buildPath
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckBuildPath(@QueryParameter String buildPath) {
    	return FormValidations.validateDirectory(buildPath, true);
    }
	
}
