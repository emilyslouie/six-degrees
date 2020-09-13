package ca.louie.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import ca.louie.spotify.data.Artist;
import ca.louie.spotify.data.ArtistRepository;
import ca.louie.spotify.data.Track;
import ca.louie.spotify.data.TrackRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataScraping {
	private MySpotify myspotify;
	private ArtistRepository artistRepository;
	private TrackRepository trackRepository;
	private DataUtility dataUtility;

	public DataScraping(MySpotify myspotify, ArtistRepository artistRepository, TrackRepository trackRepository,
			DataUtility dataUtility) {
		super();
		this.myspotify = myspotify;
		this.artistRepository = artistRepository;
		this.trackRepository = trackRepository;
		this.dataUtility = dataUtility;
	}

	@Synchronized
	private Track saveTrack(Track track) {
		Track dbTrack = trackRepository.findBySpotifyId(track.getSpotifyId());
		if (dbTrack == null) {
			dbTrack = trackRepository.save(track);
			log.debug(track.toString());
		}
		return dbTrack;
	}

	private Track saveTrack(com.wrapper.spotify.model_objects.specification.Track spotifyTrack) {
		Track track = dataUtility.convertToDbTrack(spotifyTrack);
		Track dbTrack = saveTrack(track);
		return dbTrack;
	}

	private Track saveTrack(com.wrapper.spotify.model_objects.specification.TrackSimplified spotifyTrack) {
		Track track = dataUtility.convertToDbTrack(spotifyTrack);
		Track dbTrack = saveTrack(track);
		return dbTrack;
	}

	@Synchronized
	private Artist saveArtist(com.wrapper.spotify.model_objects.specification.ArtistSimplified artist) {
		Artist dbArtist = artistRepository.findBySpotifyId(artist.getId());
		if (dbArtist == null) {
			com.wrapper.spotify.model_objects.specification.Artist fullArtist = myspotify.getFullArtist(artist.getId());
			dbArtist = dataUtility.convertToDbArtist(fullArtist);
			dbArtist = artistRepository.save(dbArtist);
			dbArtist = artistRepository.findBySpotifyId(artist.getId());
			log.debug(dbArtist.toString());
		}
		return dbArtist;
	}

	private Artist saveArtist(com.wrapper.spotify.model_objects.specification.Artist artist) {
		Artist dbArtist = artistRepository.findBySpotifyId(artist.getId());
		if (dbArtist == null) {
			dbArtist = dataUtility.convertToDbArtist(artist);
			dbArtist = artistRepository.save(dbArtist);
			log.debug(dbArtist.toString());
		} else if (dbArtist.isSimple()) {
			dbArtist.setPopularity(artist.getPopularity());
			List<ca.louie.spotify.data.Image> list = dataUtility.convertImages(artist.getImages());
			dbArtist.setImages(list);
			dbArtist = artistRepository.save(dbArtist);
			log.debug(dbArtist.toString());
		}
		return dbArtist;
	}

	private String savePlaysOn(Artist artist, Track track) {
		try {
			artist = artistRepository.findBySpotifyId(artist.getSpotifyId());
			Track newTrack = trackRepository.findBySpotifyId(track.getSpotifyId());
			if (newTrack == null) {
				log.error("Found a null track. Artist: " + artist.getName() + ":" + artist.getSpotifyId() + "  Track: "
						+ track.getName() + ":" + track.getSpotifyId());
			} else {
				artist.playsOn(newTrack);
				artistRepository.save(artist);
			}
		} catch (Exception ex) {
			log.error("savePlaysOn " + artist + " " + track, ex);
		}
		return artist + " plays on " + track;
	}

	public Artist fetchArtistBySpotifyId(String spotifyArtistId) {
		com.wrapper.spotify.model_objects.specification.Artist fullArtist = myspotify.getFullArtist(spotifyArtistId);
		Artist saveArtist = saveArtist(fullArtist);
		return saveArtist;
	
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public List<Artist> fetchOtherArtists(String artistId) {
		log.info("Fetch other artists for: " + artistId);

		List<Artist> result = new ArrayList<Artist>();

		Artist ourArtist = artistRepository.findBySpotifyId(artistId);

		List<AlbumSimplified> artistsAlbums = myspotify.getArtistsAlbums(artistId);
		List<Album> fullSortedAlbums = myspotify.getFullSortedAlbums(artistsAlbums);
		List<TrackSimplified> albumTracks = myspotify.getAlbumTracks(fullSortedAlbums);
		log.info("Counts for: " + artistId + " " + ourArtist.getName() + " albums=" + artistsAlbums.size() + ", tracks="
				+ albumTracks.size());
		for (TrackSimplified track : albumTracks) {
			Track dbTrack = saveTrack(track);

			ArtistSimplified[] artists = track.getArtists();
			for (ArtistSimplified artist : artists) {
				Artist dbArtist = saveArtist(artist);
				result.add(dbArtist);

				String playsOn = savePlaysOn(dbArtist, dbTrack);
				log.debug(playsOn);

			}
		}
		ourArtist = artistRepository.findBySpotifyId(artistId);
		ourArtist.setProcessStatus(true);
		artistRepository.save(ourArtist);

		return result;
	}

	public List<Artist> fillArtists() {
		List<Artist> result = new ArrayList<Artist>();
		List<Artist> notProcessed = artistRepository.findByProcessStatus(false);
		while (notProcessed.size() > 0) {
			List<Artist> artists = notProcessed;
			int i = 0;
			while (artists.size() > 0) {
				fetchOtherArtists(notProcessed.get(i).getSpotifyId());
				result.add(notProcessed.get(i));
				log.info("Artist: " + notProcessed.get(i).getName() + " " + notProcessed.get(i).getSpotifyId());
				i++;
			}
			notProcessed = artistRepository.findByProcessStatus(false);
		}
		return result;

	}

	public List<Track> fetchTracks(String playlistName) {
		List<Track> result = new ArrayList<Track>();
	
		List<com.wrapper.spotify.model_objects.specification.Track> spotifyTracks = myspotify
				.getTracksFromPlaylist(playlistName);
	
		for (com.wrapper.spotify.model_objects.specification.Track spotifyTrack : spotifyTracks) {
			Track dbTrack = saveTrack(spotifyTrack);
			result.add(dbTrack);
	
			ArtistSimplified[] artists = spotifyTrack.getArtists();
			for (ArtistSimplified artist : artists) {
				Artist dbArtist = saveArtist(artist);
				String playsOn = savePlaysOn(dbArtist, dbTrack);
				log.debug(playsOn);
			}
		}
	
		return result;
	}

	public String checkPlaylist(String playlistName) {
		Paging<PlaylistSimplified> playlist = myspotify.getPlaylist(playlistName);
		String result = playlist.getItems()[0].getName() + " " + playlist.getItems()[0].getTracks().getTotal();
		log.info(result);
		return result;
	}
}
