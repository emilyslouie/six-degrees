package my.spotify.data;

import org.springframework.data.repository.CrudRepository;

public interface TrackRepository extends CrudRepository<Track, Long> {

	Track findBySpotifyId(String spotifyId);
}
