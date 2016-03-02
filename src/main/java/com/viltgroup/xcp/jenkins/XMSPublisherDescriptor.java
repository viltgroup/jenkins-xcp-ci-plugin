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

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

/**
 * Descriptor for {@link XMSPublisher}. Used as a singleton.
 * The class is marked as public so that it can be accessed from views.
 *
 * <p>
 * See <tt>src/main/resources/com/viltgroup/xcp/jenkins/XMSPublisher/*.jelly</tt>
 * for the actual HTML fragment for the configuration screen.
 */
public final class XMSPublisherDescriptor extends BuildStepDescriptor<Publisher> {

	public XMSPublisherDescriptor() {
		super(XMSPublisher.class);
    }

    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
        // Indicates that this builder can be used with all kind of project types 
        return true;
    }

    /**
     * This human readable name is used in the configuration screen.
     */
    public String getDisplayName() {
        return "Publish xCP Application";
    }

    public List<XcpEnvironmentInstance> getXcpEnvironmentInstances() {
    	return XCPEnvironmentsConfig.get().xcpEnvironmentInstances;
    }

	@Override
	public Publisher newInstance(StaplerRequest req, JSONObject formData) {
		XMSPublisher ret = req.bindJSON(XMSPublisher.class, formData);
		return ret;
	}

	/**
     * Performs on-the-fly validation of the form field 'xcpAppPackagePath'.
     * 
     * @param xcpAppPackagePath
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXcpAppPackagePath(@QueryParameter String xcpAppPackagePath) {
        return FormValidations.validateFile(xcpAppPackagePath);
    }

	/**
     * Performs on-the-fly validation of the form field 'xcpAppConfigPath'.
     * 
     * @param xcpAppConfigPath
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXcpAppConfigPath(@QueryParameter String xcpAppConfigPath) {
        return FormValidations.validateFile(xcpAppConfigPath);
    }
    
    /**
     * Performs on-the-fly validation of the form field 'environment'.
     * 
     * @param environment
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckEnvironment(@QueryParameter String environment) {
        return FormValidations.validateRequired(environment);
    }
    
    /**
     * Performs on-the-fly validation of the form field 'batchSize'.
     * 
     * @param batchSize
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckBatchSize(@QueryParameter String batchSize) {
    	if (batchSize != null && !batchSize.isEmpty()) {
	    	try {
	    		int batchSizeNum = Integer.parseInt(batchSize);
	    		if (batchSizeNum <= 0) {
	        		return FormValidation.warning("Batch Size value is invalid.");    			
	    		}
	    	} catch(NumberFormatException ex) {
	    		return FormValidation.warning("Batch Size value is invalid.");
	    	}
    	}
        return FormValidation.ok();
    }
    
    /**
     * Performs on-the-fly validation of the form field 'xmsUsername'.
     * 
     * @param xmsUsername
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXmsUsername(@QueryParameter String xmsUsername) {
        return FormValidations.validateRequired(xmsUsername);
    }

    /**
     * Performs on-the-fly validation of the form field 'xmsPassword'.
     * 
     * @param xmsPassword
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doChecXxmsPassword(@QueryParameter String xmsPassword) {
        return FormValidations.validateRequired(xmsPassword);
    }

    /**
     * Performs on-the-fly validation of the form field 'xmsServerHost'.
     * 
     * @param xmsServerHost
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXmsServerHost(@QueryParameter String xmsServerHost) {
        return FormValidations.validateRequired(xmsServerHost);
    }

    /**
     * Performs on-the-fly validation of the form field 'xmsServerPort'.
     * 
     * @param xmsServerPort
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXmsServerPort(@QueryParameter String xmsServerPort) {
    	try {
    		int portNumber = Integer.parseInt(xmsServerPort);
    		if (portNumber <= 0 || portNumber > 0xffff) {
        		return FormValidation.warning("Port number is invalid.");    			
    		}
    	} catch(NumberFormatException ex) {
    		return FormValidation.warning("Port number is invalid.");
    	}
        return FormValidation.ok();
    }

	/**
     * Performs on-the-fly validation of the form field 'workPath'.
     * 
     * @param workPath
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckWorkPath(@QueryParameter String workPath) {
        return FormValidations.validateDirectory(workPath, true);
    }
}