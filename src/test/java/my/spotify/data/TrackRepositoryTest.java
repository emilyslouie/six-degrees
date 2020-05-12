package my.spotify.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

@DataNeo4jTest
class TrackRepositoryTest {

	@Autowired
	private TrackRepository tracks;

	@Test
	void test1() {
		Track track1 = Track.builder().spotifyId("myspot1").discNumber(1).trackNumber(2).externalUrl("http://ext")
				.previewUrl("http://prev").build();
		Track track2 = Track.builder().spotifyId("myspot2").discNumber(100).trackNumber(200).externalUrl("http://ext1")
				.previewUrl("http://prev1").build();

		tracks.save(track1);
		tracks.save(track2);

		Track track = tracks.findBySpotifyId("myspot2");

		assertThat(track).isEqualTo(track2);
	}
}
