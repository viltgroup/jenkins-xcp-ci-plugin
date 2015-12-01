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

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.viltgroup.xcp.jenkins.model.XcpEnvironmentInstance;
import com.viltgroup.xcp.jenkins.utils.FormValidations;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

/**
 * Global configurations for the Documentum xCP Plugin
 *
 * <p>
 * See <tt>src/main/resources/com/viltgroup/xcp/plugins/XCPEnvironmentsConfig/*.jelly</tt>
 * for the actual HTML fragment for the configuration screen.
 */
@Extension
public class XCPEnvironmentsConfig extends GlobalConfiguration {

	public final List<XcpEnvironmentInstance> xcpEnvironmentInstances;
	
    public static XCPEnvironmentsConfig get() {
    	return GlobalConfiguration.all().get(XCPEnvironmentsConfig.class);
    }
	
    /**
     * In order to load the persisted global configuration, you have to 
     * call load() in the constructor.
     */
	public XCPEnvironmentsConfig() {
		this.xcpEnvironmentInstances = new ArrayList<XcpEnvironmentInstance>();
        load();
    }
	
	public XcpEnvironmentInstance getById(String xcpEnvId) {
		XcpEnvironmentInstance result = null;
		if (xcpEnvId != null && this.xcpEnvironmentInstances != null) {
			for(XcpEnvironmentInstance instance : this.xcpEnvironmentInstances) {
				if (xcpEnvId.equals(instance.xcpEnvId)) {
					result = instance;
					break;
				}
			}
		}
		return result;
	}
	
    @Override
    public String getDisplayName() {
        return "xCP Environment Parameter";
    }
    
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
    	this.xcpEnvironmentInstances.clear();
    	List<XcpEnvironmentInstance> newXcpEnvironmentInstances = req.bindJSONToList(XcpEnvironmentInstance.class, formData.get("xcpEnvironmentInstances"));
    	if (xcpEnvironmentInstances != null) {
    		this.xcpEnvironmentInstances.addAll(newXcpEnvironmentInstances);
    	}
        save();
        return true;
    }
    
    /**
     * Performs on-the-fly validation of the form field 'name'.
     * 
     * @param name
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckName(@QueryParameter String name) {
    	return FormValidations.validateRequired(name);
    }
    
    /**
     * Performs on-the-fly validation of the form field 'xcpDesignerPath'.
     * 
     * @param xcpDesignerPath
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXcpDesignerPath(@QueryParameter String xcpDesignerPath) {
    	return FormValidations.validateDirectory(xcpDesignerPath);
    }
    
    /**
     * Performs on-the-fly validation of the form field 'xmsToolsPath'.
     * 
     * @param xmsToolsPath
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXmsToolsPath(@QueryParameter String xmsToolsPath) {
    	return FormValidations.validateDirectory(xmsToolsPath);
    }
}
