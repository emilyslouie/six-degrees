package my.spotify;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import lombok.extern.slf4j.Slf4j;
import my.spotify.data.Artist;
import my.spotify.data.ArtistRepository;
import my.spotify.data.Track;
import my.spotify.data.TrackRepository;

@SpringBootApplication
@EnableNeo4jRepositories
@Slf4j
public class MySpotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySpotifyApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(ArtistRepository artistRepository, TrackRepository trackRepository) {
		return args -> {

			// how to look for shortest path between two artist spotifyIds
			// MATCH (bacon:Artist {spotifyId:"1111"})-[*1..2]-(otherArtists)
			// RETURN DISTINCT otherArtists

			artistRepository.deleteAll();
			trackRepository.deleteAll();

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
				{"2124", "Java"},
				{"2125", "hola"}
			// @formatter:on
			};

			for (int i = 0; i < artist.length; i++) {
				Artist artist3 = new Artist(artist[i][0], artist[i][1], "someUrl");
				artistRepository.save(artist3);

			}
			for (int i = 0; i < track.length; i++) {
				Track tracks = new Track(track[i][0], track[i][1]);
				trackRepository.save(tracks);

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
				{"1135", "2124"},
				{"1111", "2125"},
				{"1126", "2125"}

				// @formatter:on
			};

			for (int i = 0; i < match.length; i++) {
				Artist findArtist = artistRepository.findBySpotifyId(match[i][0]);
				Track findTrack = trackRepository.findBySpotifyId(match[i][1]);
				findArtist.playsOn(findTrack);
				artistRepository.save(findArtist);
			}

			for (int i = 0; i < artist.length; i++) {
				Artist findArtist = artistRepository.findBySpotifyId(artist[i][0]);
				log.info(findArtist.toString());
			}

			Artist findArtist = artistRepository.findBySpotifyId("1111");

			Artist findBySpotifyId = artistRepository.findBySpotifyId("1111");
			log.info(findBySpotifyId.toString());

			Track findTrackBySpotifyId1 = trackRepository.findBySpotifyId("2111");
			log.info(findTrackBySpotifyId1.toString());

			List<Artist> shortestPath = artistRepository.findShortestPath("1111", "1125");
			log.info(shortestPath.toString());

			// emily added :D
			for (int i = 0; i < shortestPath.size(); i++) {
				Artist loopedArtist = shortestPath.get(i);
				log.info(loopedArtist.getName());
				if (!(i == shortestPath.size() - 1)) {
					if (loopedArtist.getTracks().size() > 1) {
						Artist prevArtist = shortestPath.get(i - 1);
						Iterator<Track> itr = loopedArtist.getTracks().iterator();
						Iterator<Track> itr2 = prevArtist.getTracks().iterator();

						Set<Track> hashSet = new HashSet<Track>(loopedArtist.getTracks());
						hashSet.removeAll(prevArtist.getTracks());
						Track track1 = (Track) hashSet.toArray()[0];

						if (prevArtist.getTracks().size() == 1) {
							Track prevArtistTrack = itr2.next();
							while (itr.hasNext()) {
								Track currArtistTrack = itr.next();
								if (!currArtistTrack.getName().equals(prevArtistTrack.getName())) {
									log.info(currArtistTrack.getName());
								}
							}
						} else {
							while (itr.hasNext()) {
								Track currArtistTrack = itr.next();
								Track prevArtistTrack = itr2.next();
								if (!currArtistTrack.getName().equals(prevArtistTrack.getName())) {
									log.info(currArtistTrack.getName());
								}
							}
						}

					} else {
						Track trackk = (Track) loopedArtist.getTracks().toArray()[0];
						log.info(trackk.getName());
					}
				}

			}

//			List<Map<String, Object>> shortestPath2 = artistRepository.findShortestPathBetweenAnything("2111", "1125");
//			log.info(shortestPath2.toString());
//
//			List<Artist> shortestPath3 = artistRepository.findShortestPathBtwnTrackNArtist("2111", "1125");
//			log.info(shortestPath3.toString());
//
//			ArtistBuilder builder = Artist.builder().spotifyId("spotifyId").name("John").externalUrl("someUrl");
//			Artist john = builder.build();
//
//			john.addImage(
//					"https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex",
//					2, 3);
//			john.addImage("https://developer.spotify.com/documentation/web-api/reference/albums/get-album/", 200, 300);
//			john.addImage(
//					"https://stackoverflow.com/questions/355089/difference-between-stringbuilder-and-stringbuffer",
//					2000, 3000);
//
//			builder = Artist.builder().spotifyId("spotifyIdP").name("Paul").externalUrl("someUrlP");
//			Artist paul = builder.build();
//
//			john.setGenres(Arrays.asList("rock", "pop", "blues"));
//
//			artistRepository.deleteAll();
//			trackRepository.deleteAll();
//
//			artistRepository.save(john);
//			artistRepository.save(paul);
//
//			john = artistRepository.findBySpotifyId("spotifyId");
//			paul = artistRepository.findBySpotifyId("spotifyIdP");
//			john.collaboratesWith(paul);
//			artistRepository.save(john);
//
//			Track track1 = Track.builder().spotifyId("myspot1").discNumber(1).trackNumber(2).externalUrl("http://ext")
//					.previewUrl("http://prev").build();
//			Track track2 = Track.builder().spotifyId("myspot2").discNumber(10).trackNumber(20).externalUrl("http://ext")
//					.previewUrl("http://prev").build();
//
//			trackRepository.save(track1);
//			trackRepository.save(track2);
//			track1 = trackRepository.findBySpotifyId(track1.getSpotifyId());
//			track2 = trackRepository.findBySpotifyId(track2.getSpotifyId());
//			john.playsOn(track1);
//			john.playsOn(track2);
//			artistRepository.save(john);
//
//			john = artistRepository.findBySpotifyId("spotifyId");
//			paul = artistRepository.findBySpotifyId("spotifyIdP");
//			log.info(john.toString());
//			log.info(paul.toString());
		};
	}
}
