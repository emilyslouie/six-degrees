package ca.louie.spotify.data;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
public class CachingTrackRepositoryImpl implements CachingTrackRepository {

	@CachePut(cacheNames = "tracks", key = "#track.spotifyId")
	public Track fillCacheWithTrack(Track track) {
		return track;
	}

}
