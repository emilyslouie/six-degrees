package my.spotify.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataNeo4jTest
public class EmilyTest {
	@Autowired
	private ArtistRepository artistRepo;

	@Autowired
	private TrackRepository trackRepo;

	@Test
	void testFindBySpotifyId() {

		String[][] artist = new String[][] {
		// @formatter:off 	
			{"1111", "John"},
			{"1112", "Abe"},
			{"1113", "Paul"},
			{"1114", "Ringo"},
			{"1115", "George"},
			{"1116", "Khalid"},
			{"1117", "Khalid"},
			{"1118", "Ed"},
			{"1119", "Billie"},
			{"1120", "Justin"},
			{"1121", "Taylor"},
			{"1122", "Christina"},
			{"1123", "Anna"},
			{"1124", "Nathan"},
			{"1125", "Emily"},
			{"1126", "Gifford"},
			{"1127", "Ana"},
			{"1128", "Jay"},
			{"1129", "Ray"},
			{"1130", "May"},
			{"1131", "Laura"},
			{"1132", "Kevin"},
			{"1133", "Jerry"},
			{"1134", "Mark"},
			{"1135", "Danny"},				
		// @formatter:on
		};

		String[][] track = new String[][] {
		// @formatter:off 	
			{"2111", "Let it be"},
			{"2112", "Hellfire"},
			{"2113", "Saturday Nights"},
			{"2114", "Imposter"},
			{"2115", "Bad Guy"},
			{"2116", "Meh"},
			{"2117", "Love Story"},
			{"2118", "Spotify"},
			{"2119", "Plsay"},
			{"2120", "Mobile"},
			{"2121", "Piplup"},
			{"2122", "Phone"},
			{"2123", "Argh"},
			{"2124", "Java"}			
		// @formatter:on
		};

		for (int i = 0; i < artist.length; i++) {
			Artist artist3 = new Artist(artist[i][0], artist[i][1], "someUrl");
			artistRepo.save(artist3);

		}
		for (int i = 0; i < track.length; i++) {
			Track tracks = new Track(track[i][0], track[i][1]);
			trackRepo.save(tracks);

		}

		String[][] match = new String[][] {
		// @formatter:off 	
			{"1111", "2111"},
			{"1111", "2112"},
			{"1112", "2112"},
			{"1113", "2111"},
			{"1114", "2111"},
			{"1115", "2111"},
			{"1116", "2113"},
			{"1117", "2114"},
			{"1118", "2113"},

			{"1119", "2115"},
			{"1119", "2116"},
			{"1120", "2115"},
			{"1121", "2117"},
			{"1122", "2117"},
			{"1123", "2118"},
			{"1124", "2119"},
			{"1124", "2120"},
			{"1125", "2119"},
			{"1126", "2111"},
			{"1126", "2119"},
			{"1126", "2118"},
			{"1127", "2114"},
			{"1127", "2118"},
			{"1128", "2121"},
			{"1129", "2121"},
			{"1129", "2113"},

			{"1130", "2122"},
			{"1131", "2118"},
			{"1132", "2122"},
			{"1132", "2116"},
			{"1133", "2123"},
			{"1134", "2124"},			
			{"1134", "2120"},
			{"1135", "2124"}			

			// @formatter:on
		};

		for (int i = 0; i < match.length; i++) {
			Artist findArtist = artistRepo.findBySpotifyId(match[i][0]);
			Track findTrack = trackRepo.findBySpotifyId(match[i][1]);
			findArtist.playsOn(findTrack);
			artistRepo.save(findArtist);
		}

		for (int i = 0; i < artist.length; i++) {
			Artist findArtist = artistRepo.findBySpotifyId(artist[i][0]);
			log.info(findArtist.toString());
		}

//		Artist findArtist = artistRepo.findBySpotifyId("1111");
//		assertThat(((Track) (findArtist.getTracks().toArray()[0])).getSpotifyId()).isEqualTo("2111");
//
//		Artist findBySpotifyId = artistRepo.findBySpotifyId("1111");
//		log.info(findBySpotifyId.toString());
//
//		assertThat(findBySpotifyId.getSpotifyId()).isEqualTo("1111");
//
//		Track findTrackBySpotifyId1 = trackRepo.findBySpotifyId("2111");
//		log.info(findTrackBySpotifyId1.toString());
//
//		assertThat(findTrackBySpotifyId1.getSpotifyId()).isEqualTo("2111");

		List<Artist> shortestPath = artistRepo.findShortestPath("1111", "1125");
		log.info(shortestPath.toString());

		assertThat(shortestPath.get(2).getName()).isEqualTo("Emily");

	}

}
