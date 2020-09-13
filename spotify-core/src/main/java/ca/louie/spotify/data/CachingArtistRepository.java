package ca.louie.spotify.data;

import org.springframework.cache.annotation.CachePut;

public interface CachingArtistRepository {

	@CachePut(cacheNames = "artists", key = "#artist.spotifyId")
	Artist fillCacheWithArtist(Artist artist);

}
