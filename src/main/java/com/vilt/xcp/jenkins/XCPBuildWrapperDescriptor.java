package com.vilt.xcp.jenkins;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.vilt.xcp.jenkins.model.XcpEnvironmentInstance;
import com.vilt.xcp.jenkins.utils.FormValidations;

/**
 * Descriptor for {@link XCPBuildWrapper}. Used as a singleton.
 * The class is marked as public so that it can be accessed from views.
 *
 * <p>
 * See <tt>src/main/resources/com/vilt/xcp/jenkins/XCPBuildWrapper/*.jelly</tt>
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
    public FormValidation doCheckBuildPath(@QueryParameter String buildPath) throws IOException, ServletException {
    	return FormValidations.validateDirectory(buildPath, true);
    }
	
}
