package com.viltgroup.xcp.xms.xmsdeployer;

import com.viltgroup.xcp.jenkins.model.DataPolicy;
import com.viltgroup.xcp.jenkins.model.DeployEnvType;
import com.viltgroup.xcp.jenkins.model.DeployMethod;

public interface IXMSPublishConfig {

	public String getXcpAppPackagePath();
	public String getXcpAppConfigPath();
	
	public String getEnvironment();

	public DeployMethod getDeployMethod();
	public DataPolicy getDataPolicy();
	public DeployEnvType getDeployEnvType();

	public boolean isXploreIndexing();
	public boolean isValidateOnly();

	public String getXmsUsername();
	public String getXmsPassword();
	public String getXmsServerHost();
	public String getXmsServerPort();
	public String getXmsServerSchema();
	public String getXmsServerContextPath();
	
	public String getXmsToolsPath();
	public String getJavaOpts();
}
