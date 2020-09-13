package ca.louie.spotify.data;

import org.springframework.cache.annotation.CachePut;

public interface CachingTrackRepository {

	@CachePut(cacheNames = "tracks", key = "#track.spotifyId")
	Track fillCacheWithTrack(Track track);

}
