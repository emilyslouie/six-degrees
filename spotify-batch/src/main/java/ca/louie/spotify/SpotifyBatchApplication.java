package ca.louie.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import ca.louie.spotify.data.ArtistRepository;
import ca.louie.spotify.data.TrackRepository;
import ca.louie.spotify.service.DataScraping;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SpotifyBatchApplication {
	@Autowired
	private DataScraping scraper;

	public static void main(String[] args) {
		SpringApplication.run(SpotifyBatchApplication.class, args);
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE + 1)
	CommandLineRunner demo(ArtistRepository artists, TrackRepository tracks) {
		return args -> {
			log.info("****CommandLineRunner****");
		};
	}

	public String playlists() {
		// http://localhost:8080/playlists
		// @formatter:off

		String list = "Today’s Top Hits," + 
				"Rock Classics," + 
				"All Out 50s," + 
				"All Out 60s," + 
				"All Out 70s," + 
				"All Out 80s," + 
				"All Out 90s," + 
				"All Out 00s," +
				"Emo Forever," + 
				"Chilled R&B," + 
				"R&B Favourites," + 
				"Dance Hits," + 
				"Chill Tracks," + 
				"Dance Classics," + 
				"Pop Favourites," + 
				"Hit Rewind," + 
				"Legendary," + 
				"Rock Favourites," + 
				"Lofi Beats," + 
				"Hip-Hop Favourites," + 
				"Throwback Party," + 
				"Indie All Stars," + 
				"Essential Indie," + 
				"Indie Favourites," + 
				"Classic Acoustic," + 
				"Folk & Friends," + 
				"Songs to Sing in the Car," + 
				"Classic Road Trip Songs," + 
				"Classic Country Drive," + 
				"Indie Rock Road Trip," + 
				"Classical Essentials," + 
				"Chilled Classical," + 
				"Jazz Classics," + 
				"State of Jazz," + 
				"Jazz Classics Blue Note Edition," + 
				"Chilled Jazz," + 
				"Smooth Jazz," + 
				"Top Gaming Tracks," + 
				"Video Game Soundtracks," + 
				"Love Pop," + 
				"All The Feels," + 
				"Timeless Love Songs," + 
				"Fresh Gospel," + 
				"Top Christian," + 
				"Arab X," + 
				"Best of Arab Pop," + 
				"Essential K-pop," + 
				"K-pop X-overs," + 
				"K-Pop Rising," + 
				"K-pop Daebak," + 
				"Trenchill K-R&B," + 
				"K-hip-hop +82," + 
				"Fierce Femmes," + 
				"Badass Women," + 
				"Bollywood Blast," + 
				"African Heat," + 
				"Afropop," + 
				"Old School metal," + 
				"Metal Essentials," + 
				"Pop Punk Powerhouses," + 
				"Punk Essentials," + 
				"Classic Punk," + 
				"Reggae Classics," + 
				"Funk & Soul Classics," + 
				"Timeless Soul Ballads," + 
				"Soul Classics 1970-1975," + 
				"Disco Forever," + 
				"Let’s Get Funky," + 
				"Funk Rock," + 
				"Blues Classics," + 
				"Blues Origins," + 
				"Modern Blues Rock," + 
				"Soundtracked," + 
				"Canadian Weekend"; 
				// @formatter:on

		StringBuffer buffer = new StringBuffer();
		String[] playlists = list.split(",");
		for (int i = 0; i < playlists.length; i++) {
			buffer.append(playlists[i] + " ");
			buffer.append(scraper.fetchTracks(playlists[i]));
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
