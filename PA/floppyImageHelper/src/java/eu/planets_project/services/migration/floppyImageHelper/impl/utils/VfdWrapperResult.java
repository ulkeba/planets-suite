package eu.planets_project.services.migration.floppyImageHelper.impl.utils;

import java.io.File;

import eu.planets_project.services.utils.ZipResult;

public class VfdWrapperResult {
	
	private int status = -1;
	private String message = null;
	private File resultFile = null;
	private ZipResult zipResult = null;
	public boolean resultIsZip = false;
	public static final int SUCCESS = 0;
	public static final int ERROR = -1;
	
	public ZipResult getZipResult() {
		return zipResult;
	}
	
	public void setZipResult(ZipResult zipResult) {
		resultIsZip = true;
		this.zipResult = zipResult;
	}
	
	public int getState() {
		return status;
	}
	
	public void setState(int status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public File getResultFile() {
		return resultFile;
	}
	
	public void setResultFile(File resultFile) {
		this.resultIsZip = false;
		this.resultFile = resultFile;
	}

}