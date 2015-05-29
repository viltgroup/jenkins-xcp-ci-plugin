package com.viltgroup.xcp.jenkins.utils;

import hudson.util.FormValidation;

import java.io.File;

public class FormValidations {

    public static FormValidation validateRequired(String value) {
        if(value == null || value.isEmpty()) {
            return FormValidation.warning("Value cannot be empty.");
        }
        return FormValidation.ok();
    }

    public static FormValidation validateDirectory(String path) {
    	return validateDirectory(path, false);
    }
    
    public static FormValidation validateDirectory(String path, boolean ignoreRelativePathExistance) {
        if(path == null || path.isEmpty()) {
            return FormValidation.error("Path cannot be empty.");
        }
        File dir = new File(path);
        if(!dir.exists()) {
        	if (ignoreRelativePathExistance && !dir.isAbsolute()) {
        		return FormValidation.ok();
        	}
	        return FormValidation.error("Path does not exist.");
        }
        if(!dir.isDirectory()) {
            return FormValidation.error("Path is not a directory.");
        }
        return FormValidation.ok();    	
    }

    public static FormValidation validateFile(String path) {
        if(path == null || path.isEmpty()) {
            return FormValidation.warning("Path cannot be empty.");
        }
        File dir = new File(path);
        if(!dir.exists() || !dir.isFile()) {
            return FormValidation.warning("Path is invalid.");
        }
        return FormValidation.ok();    	
    }
    
}
