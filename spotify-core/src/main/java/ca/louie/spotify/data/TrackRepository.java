package ca.louie.spotify.data;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

public interface TrackRepository extends CrudRepository<Track, Long>, CachingTrackRepository {

	@Cacheable(cacheNames = "tracks", key = "#spotifyId")
	Track findBySpotifyId(String spotifyId);

}
