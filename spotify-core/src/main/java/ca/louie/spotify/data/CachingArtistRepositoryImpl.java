package ca.louie.spotify.data;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
public class CachingArtistRepositoryImpl implements CachingArtistRepository {

	@CachePut(cacheNames = "artists", key = "#artist.spotifyId")
	public Artist fillCacheWithArtist(Artist artist) {
		return artist;
	}
}
