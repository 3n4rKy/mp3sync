package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import main.Application;
import main.PropertiesLoader;
import model.PlayList;

class CreatePlayListTest {

	@Test
	void testCreatePlayList() {
		String str = "#EXTM3U\n" + "#EXTINF:661,Kool Savas - Das Urteil\n"
				+ "file:///media/veracrypt1/mp3s/%23HipHop/Kool%20Savas/Die%20John%20Bello%20Story%20-%202005/29%20-%20Kool%20Savas%20-%20Das%20Urteil.mp3\n"
				+ "#EXTINF:233,Underground Source - Kaffee & Kuchen\n"
				+ "file:///media/veracrypt1/mp3s/%23HipHop/Underground%20Source/Kaffee%20&%20Kuchen/Underground%20Source%20-%2002%20-%20Kaffee%20&%20Kuchen%20-%20Kaffee%20&%20Kuchen.mp3";
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		InputStream is = new ByteArrayInputStream(bytes);
		PlayList playList = new PlayList();
		playList.setName("test");
		Properties replacements = PropertiesLoader.loadProperties();
		Application.createPlaylist(playList, is, replacements);
		assertEquals(playList.getFilePaths().get(0),
				"/media/veracrypt1/mp3s/#HipHop/Kool Savas/Die John Bello Story - 2005/29 - Kool Savas - Das Urteil.mp3");
	}

}
