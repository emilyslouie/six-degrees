package my.spotify;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import my.spotify.data.Artist;
import my.spotify.data.Track;
import my.spotify.service.DataScraping;
import my.spotify.service.InteractiveSearch;
import my.spotify.service.MySpotify;
import my.spotify.service.quote.Quote;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	private InteractiveSearch slurp;
	private DataScraping scraper;
	private MySpotify mySpotify;

	public GreetingController(InteractiveSearch slurp, DataScraping scraper, MySpotify mySpotify) {
		super();
		this.slurp = slurp;
		this.scraper = scraper;
		this.mySpotify = mySpotify;
	}

	@GetMapping("/emily")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "Piplup") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@PostMapping(path = "/nathan", consumes = "application/json", produces = "text/plain")
	public String lego(@RequestBody Greeting greeting) {
		return greeting.getContent() + "weeee";
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@GetMapping(path = "/piplup")
	public String lego(RestTemplate restTemplate) {
		Quote quote = restTemplate.getForObject("https://gturnquist-quoters.cfapps.io/api/random", Quote.class);
		return quote.getValue().getQuote();
	}

	@GetMapping(path = "/spotify")
	public List<Artist> spotify(@RequestParam(value = "name") String name) {
		List<Artist> artists = slurp.fetchArtists(name);
		return artists;
//		return myspotify.search("6LuN9FCkKOj5PcnpouEgny", "6qqNVTkY8uBg9cP3Jd7DAH");
////		getArtistId("Billie", myspotify);
////		getArtistId("Ed", myspotify);
//		getFullSortedAlbums(getArtistsAlbums("6eUKZXaKkcviH0Ku9w2n3V", myspotify), myspotify);
//		return "0";
	}

	@GetMapping(path = "/playlist-tracks")
	public List<Track> tracks(@RequestParam(value = "name") String name) {
		// http://localhost:8080/playlist-tracks?name=All%20Out%2050s
		return scraper.fetchTracks(name);
	}

	@GetMapping(path = "/artist-albums")
	public List<Artist> artistAlbums(@RequestParam(value = "id") String id) {
		// /artist-albums?id=5V0MlUE1Bft0mbLlND7FJz Ella Fitzgerald
		return scraper.fetchOtherArtists(id);
//		List<AlbumSimplified> artistsAlbums = mySpotify.getArtistsAlbums(id);
//		List<Album> fullSortedAlbums = mySpotify.getFullSortedAlbums(artistsAlbums);
//		mySpotify.getArtists(fullSortedAlbums);
//		return null;
	}

	@GetMapping(path = "/playlists")
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
			buffer.append(scraper.checkPlaylist(playlists[i]));
			buffer.append("\n");
		}
		return buffer.toString();
	}

	@GetMapping(path = "/scrape")
	public List<Artist> scrape() {
		// http://localhost:8080/scrape
		return scraper.fillArtists();
	}

}
