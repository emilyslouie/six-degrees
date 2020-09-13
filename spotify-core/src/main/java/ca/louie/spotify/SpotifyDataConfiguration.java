package ca.louie.spotify;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.retry.annotation.EnableRetry;

import ca.louie.spotify.data.ArtistRepository;
import ca.louie.spotify.data.TrackRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableNeo4jRepositories(basePackages = "ca.louie.spotify.data")
@EntityScan(basePackages = "ca.louie.spotify.data")
@EnableRetry
@EnableCaching
@Slf4j
public class SpotifyDataConfiguration {
	@Autowired
	public ArtistRepository artistRepository;

	@Autowired
	public TrackRepository trackRepository;

	@PostConstruct
	public void fillCaches() {
//		log.info("Begin findAll artists");
//		Iterable<Artist> allArtists = artistRepository.findAll();
//		log.info("End findAll artists");
//
//		log.info("Begin mapping artists");
//		for (Artist artist : allArtists) {
//			artistRepository.fillCacheWithArtist(artist);
//		}
//		log.info("End mapping artists");
//		log.info("Artist count: " + artistRepository.count());
//
//		log.info("Begin findAll tracks");
//		Iterable<Track> allTracks = trackRepository.findAll();
//		log.info("End findAll tracks");
//
//		log.info("Begin mapping tracks");
//		for (Track track : allTracks) {
//			trackRepository.fillCacheWithTrack(track);
//		}
//		log.info("End mapping tracks");
//		log.info("Track count: " + trackRepository.count());
	}
}
