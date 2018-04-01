package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.PlayList;

class VerifyPlayListTest {

	@Test
	void testVerifyPlayList() {
		PlayList pl = new PlayList();
		pl.setName("test");
		List<String> fileNames = new ArrayList<String>();
		fileNames.add(
				"/media/veracrypt1/mp3s/#HipHop/Kool Savas/Die John Bello Story - 2005/29 - Kool Savas - Das Urteil.mp3");
		fileNames.add(
				"/media/veracrypt1/mp3s/#HipHop/Kool Savas/Die John Bello Story - 2005/29 - Kool Savas -%20Das Urteil.mp3");
		pl.setFilePaths(fileNames);
		// assertth
		// Application.verifyPlaylist(playList);
		// assertEquals(pl.getFilePaths().get(0),
		// "/media/veracrypt1/mp3s/#HipHop/Kool Savas/Die John Bello Story - 2005/29 -
		// Kool Savas - Das Urteil.mp3");
	}

}
