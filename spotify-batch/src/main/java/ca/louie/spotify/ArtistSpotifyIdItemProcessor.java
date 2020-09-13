package ca.louie.spotify;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import ca.louie.spotify.service.DataScraping;

@Component
public class ArtistSpotifyIdItemProcessor implements ItemProcessor<String, String> {

	public DataScraping scraper;

	public ArtistSpotifyIdItemProcessor(DataScraping scraper) {
		super();
		this.scraper = scraper;
	}

	@Override
	public String process(String artistSpotifyId) throws Exception {
		scraper.fetchOtherArtists(artistSpotifyId);
		return artistSpotifyId;
	}

}
