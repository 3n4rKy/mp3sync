

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import model.PlayList;
import shell.Command;
import shell.ExecuteShellComand;

public class Application {
	final static Logger logger = Logger.getLogger(Application.class.getName());

	public static void main(String[] args) {

		logger.info("Start");
		String playlistPath = "";
		if (args.length != 1) {
			throw new RuntimeException("wrong usage, no play list entered");
		} else {
			playlistPath = args[0];
		}
		Properties replacements = new Properties();
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream("replacements.properties"));
			replacements.load(stream);
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PlayList playList = new PlayList();
		createPlaylist(playList, playlistPath, replacements);
		verifyPlaylist(playList);
		checkFolderOnTarget(playList);
		File localTmpFolder = new File("/tmp/" + playList.getName());
		String remoteFolder = "/mnt/media_rw/3831-6531/music/";
		prepareCopyProcess(playList, localTmpFolder);
		copyFiles(localTmpFolder, remoteFolder);
		removeTempFolder(localTmpFolder);

	}

	private static void removeTempFolder(File localFolder) {
		if (localFolder.exists()) {
			File[] filesToDelete = localFolder.listFiles();
			for (int i = 0; i < filesToDelete.length; i++) {
				try {
					Files.delete(filesToDelete[i].toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {

			Files.deleteIfExists(localFolder.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void copyFiles(File localFolder, String remoteFolder) {
		Command copyFolderCmd = new Command();
		ExecuteShellComand cmd = new ExecuteShellComand();
		logger.info("start syncing ...");
		copyFolderCmd = cmd.executeCommand("rsync -rve ssh " + localFolder + " funke:" + remoteFolder + " --size-only");
		logger.info("synced files with: " + copyFolderCmd.getCmd());
		logger.info(copyFolderCmd.getOutput());

	}

	private static void prepareCopyProcess(PlayList playList, File folder) {
		try {
			Files.createDirectory(folder.toPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger.info("copying tmp files");

		for (String entry : playList.getFilePaths()) {
			File file = new File(entry);
			try {
				Files.copy(file.toPath(), folder.toPath().resolve(file.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void checkFolderOnTarget(PlayList playList) {
		Command checkFolderCmd = new Command();
		ExecuteShellComand cmd = new ExecuteShellComand();
		checkFolderCmd = cmd.executeCommand("ssh -t funke ls /mnt/media_rw/3831-6531/music/" + playList.getName());
		logger.fine("folder check: " + checkFolderCmd.getExitValue());
		if (checkFolderCmd.getExitValue() == 1) {
			logger.info("folder doesn't exist: " + checkFolderCmd.getCmd());
			logger.info("creating: " + checkFolderCmd.getCmd());
			Command createFolderCmd = new Command();
			createFolderCmd = cmd
					.executeCommand("ssh -t funke mkdir /mnt/media_rw/3831-6531/music/" + playList.getName());
			if (createFolderCmd.getExitValue() == 0) {
				logger.info("folder created: " + createFolderCmd.getCmd());
			} else {
				throw new RuntimeException("could not create folder: " + createFolderCmd.getExitValue());
			}
		} else if (checkFolderCmd.getExitValue() == 255) {
			throw new RuntimeException("ssh connection could not be established");
		} else {
			logger.info("Folder exists: " + checkFolderCmd.getCmd());
		}
	}

	private static void createPlaylist(PlayList playList, String playlistPath, Properties replacements) {
		File playListFile = new File(playlistPath);
		playList.setName(playListFile.getName().replaceAll(".m3u", ""));
		List<String> fileNames = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(playListFile))) {
			for (String line; (line = br.readLine()) != null;) {
				if (!line.startsWith("#")) {
					for (Entry<Object, Object> replacement : replacements.entrySet()) {
						line = line.replaceAll("file://", "");
						line = line.replaceAll(replacement.getKey().toString(), replacement.getValue().toString());
					}
					fileNames.add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playList.setFilePaths(fileNames);

	}

	private static void verifyPlaylist(PlayList playList) {
		boolean dirty = false;
		for (String filePath : playList.getFilePaths()) {
			if (filePath.contains("%")) {
				System.out.println(filePath);
				dirty = true;
			}
		}
		if (dirty) {
			throw new RuntimeException("adapt replacements");
		}
	}

}
