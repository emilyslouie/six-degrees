package my.spotify.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jline.internal.Log;


@DataNeo4jTest
class ArtistRepositoryTest {
	@Autowired
	private ArtistRepository artists;

	@Test
	void testFindBySpotifyId() {
		
		Artist artist = new Artist("spotifyId", "John", "someUrl");
		
		artist.addImage(
				"https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex",
				2, 3);
		artist.addImage("https://developer.spotify.com/documentation/web-api/reference/albums/get-album/", 200, 300);
		artist.addImage("https://stackoverflow.com/questions/355089/difference-between-stringbuilder-and-stringbuffer",
				2000, 3000);

		artists.save(artist);

		Artist findBySpotifyId = artists.findBySpotifyId(artist.getSpotifyId());
		Log.info(findBySpotifyId.toString());
		
		assertThat(findBySpotifyId.getSpotifyId()).isEqualTo(artist.getSpotifyId());
	}

	@Test
	void testFindByName() {
		Artist artist = new Artist("spotifydsfsfds4sdfsge", "John Lennon",
				"https://open.spotify.com/artist/0OdUWJ0sBjDrqHygGUXeCF");
		artists.save(artist);

		List<Artist> findByName = artists.findByName(artist.getName());

		assertThat(findByName).extracting(Artist::getName).containsOnly(artist.getName());
	}

	@Test
	void testFindByPopularity() {
		Artist artist = new Artist("spotifydsfsfds4sdfsge", "Paul", "someUrl");
		Artist artist2 = new Artist("spotifydsfsfds4sdfsge", "John Lennon", "someUrl2");
		artists.save(artist);
		artists.save(artist2);

		List<Artist> findByPopularity = artists.findByPopularity(-1);

		assertThat(findByPopularity.get(0)).extracting(Artist::getName).isEqualTo("John Lennon");
	}

//	@Test
//	void testJsonw() {
//		Image image1 = new Image(
//				"https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex",
//				2, 3);
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			System.out.println(objectMapper.writeValueAsString(image1));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		assertThat(true).isEqualTo(true);
//	}

//	@Test
//	void testJsonr() {
//		String json = "{\"url\":\"https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex\",\"height\":2,\"width\":3}";
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			System.out.println(objectMapper.readValue(json, Image.class));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		assertThat(true).isEqualTo(true);
//	}
//
//	@Test
//	void testJsonwc() {
//		Image image1 = new Image(
//				"https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex",
//				2, 3);
//		Image image2 = new Image("https://developer.spotify.com/documentation/web-api/reference/albums/get-album/", 200,
//				300);
//		List<Image> list = new ArrayList<Image>();
//		list.add(image1);
//		list.add(image2);
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			System.out.println(objectMapper.writeValueAsString(list));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		assertThat(true).isEqualTo(true);
//	}
//
//	@Test
//	void testJsonrc() {
//		TypeReference<List<Image>> mapType = new TypeReference<List<Image>>() {
//		};
//
//		String json = "[{\"url\":\"https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex\",\"height\":2,\"width\":3}]";
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			List<Image> jsonToImageList = objectMapper.readValue(json, mapType);
//			jsonToImageList.forEach(System.out::println);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		assertThat(true).isEqualTo(true);
//	}

}
