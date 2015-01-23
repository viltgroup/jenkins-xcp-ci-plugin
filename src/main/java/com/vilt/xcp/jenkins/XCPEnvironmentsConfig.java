package com.vilt.xcp.jenkins;

import hudson.Extension;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.vilt.xcp.jenkins.model.XcpEnvironmentInstance;
import com.vilt.xcp.jenkins.utils.FormValidations;

/**
 * Global configurations for the Documentum xCP Plugin
 *
 * <p>
 * See <tt>src/main/resources/com/vilt/xcp/plugins/XCPEnvironmentsConfig/*.jelly</tt>
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
    public FormValidation doCheckName(@QueryParameter String name) throws IOException, ServletException {
    	return FormValidations.validateRequired(name);
    }
    
    /**
     * Performs on-the-fly validation of the form field 'xcpDesignerPath'.
     * 
     * @param path
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXcpDesignerPath(@QueryParameter String xcpDesignerPath) throws IOException, ServletException {
    	return FormValidations.validateDirectory(xcpDesignerPath);
    }
    
    /**
     * Performs on-the-fly validation of the form field 'xmsToolsPath'.
     * 
     * @param path
     *            This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the
     *         browser.
     */
    public FormValidation doCheckXmsToolsPath(@QueryParameter String xmsToolsPath) throws IOException, ServletException {
    	return FormValidations.validateDirectory(xmsToolsPath);
    }
}
