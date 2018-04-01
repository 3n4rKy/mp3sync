package model;

import java.util.List;

public class PlayList {
	String name;
	List<String> filePaths;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(List<String> fileNames) {
		this.filePaths = fileNames;
	}

}
