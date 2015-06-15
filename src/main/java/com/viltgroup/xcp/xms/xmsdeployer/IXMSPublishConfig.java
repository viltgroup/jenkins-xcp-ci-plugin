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
