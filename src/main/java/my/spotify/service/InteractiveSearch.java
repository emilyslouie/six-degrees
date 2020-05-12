package my.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import my.spotify.data.Artist;
import my.spotify.data.ArtistRepository;

@Service
@Slf4j
public class InteractiveSearch {
	private MySpotify myspotify;
	private ArtistRepository artistRepository;
	private DataUtility dataUtility;

	public InteractiveSearch(MySpotify myspotify, ArtistRepository artistRepository, DataUtility dataUtility) {
		super();
		this.myspotify = myspotify;
		this.artistRepository = artistRepository;
		this.dataUtility = dataUtility;
	}

	public List<my.spotify.data.Artist> fetchArtists(String name) {
		List<Artist> result = new ArrayList<Artist>();
		List<com.wrapper.spotify.model_objects.specification.Artist> spotifyArtists = myspotify.getArtistsByName(name);

		for (com.wrapper.spotify.model_objects.specification.Artist artist : spotifyArtists) {
			Artist dbArtist = artistRepository.findBySpotifyId(artist.getId());

			if (dbArtist == null) {
				dbArtist = saveArtist(artist);
				result.add(dbArtist);
			} else {
				if (dbArtist.isSimple()) {
					dbArtist.setPopularity(artist.getPopularity());
					dbArtist.setImages(dataUtility.convertImages(artist.getImages()));
					artistRepository.save(dbArtist);
					log.info(dbArtist.toString());
				}
				result.add(dbArtist);
			}
		}

		return result;
	}

	private Artist saveArtist(com.wrapper.spotify.model_objects.specification.Artist artist) {
		Artist dbArtist = dataUtility.convertToDbArtist(artist);
		dbArtist = artistRepository.save(dbArtist);
		log.debug(dbArtist.toString());
		return dbArtist;
	}

}
