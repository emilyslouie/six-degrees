package my.spotify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import my.spotify.data.Artist;

@SpringBootTest
class MySpotifyTest {

	@Autowired
	MySpotify myspotify;

	@Autowired
	InteractiveSearch slurp;
	
	@Test
	void testGetArtistId() {
		String artistId = myspotify.getArtistId("Khalid");
		System.out.println(artistId);
		assertEquals("6LuN9FCkKOj5PcnpouEgny", artistId);
	}

	@Test
	void testGetArtist() {
		List<Artist> artist = slurp.fetchArtists("Khalid");
		System.out.println(artist.get(0).getId());
		assertEquals("6LuN9FCkKOj5PcnpouEgny", artist.get(0).getId());
	}
}
