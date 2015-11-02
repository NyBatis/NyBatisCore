package org.nybatis.core.db.sql.repository;

import java.io.File;

public class LoadedFile {

	private String environmentId = "Unknown because of reloading another database configuration.";
	private String filePath      = environmentId;

	public LoadedFile() {}

	public LoadedFile( String environmentId, File filePath ) {
		this.environmentId = environmentId;
		this.filePath      = filePath.toString();
	}

	public String getEnvironmentId() {
		return environmentId;
	}

	public String getFilePath() {
		return filePath;
	}

}
