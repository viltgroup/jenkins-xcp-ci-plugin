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
package com.viltgroup.xcp.jenkins.utils;

public class OSPathHacks {

	public static String escapePathForEclipseConf(String path) {
		return path.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\:", "\\\\\\:");
	}
	
	public static String processFilePath(String path) {
		return path.replaceAll("\\\\", "/");
	}
	
}
