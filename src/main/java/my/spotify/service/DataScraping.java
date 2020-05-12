package my.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import lombok.extern.slf4j.Slf4j;
import my.spotify.data.Artist;
import my.spotify.data.ArtistRepository;
import my.spotify.data.Track;
import my.spotify.data.TrackRepository;

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

	private Track saveTrack(com.wrapper.spotify.model_objects.specification.Track spotifyTrack) {
		Track dbTrack = trackRepository.findBySpotifyId(spotifyTrack.getId());
		if (dbTrack == null) {
			Track track = dataUtility.convertToDbTrack(spotifyTrack);
			dbTrack = trackRepository.save(track);
			log.debug(track.toString());
		}
		return dbTrack;
	}

	private Track saveTrack(com.wrapper.spotify.model_objects.specification.TrackSimplified spotifyTrack) {
		Track dbTrack = trackRepository.findBySpotifyId(spotifyTrack.getId());
		if (dbTrack == null) {
			Track track = dataUtility.convertToDbTrack(spotifyTrack);
			dbTrack = trackRepository.save(track);
			log.debug(track.toString());
		}
		return dbTrack;
	}

	private Artist saveArtist(com.wrapper.spotify.model_objects.specification.ArtistSimplified artist) {
		Artist dbArtist = artistRepository.findBySpotifyId(artist.getId());
		if (dbArtist == null) {
			com.wrapper.spotify.model_objects.specification.Artist fullArtist = myspotify.getFullArtist(artist.getId());
			dbArtist = dataUtility.convertToDbArtist(fullArtist);
			dbArtist = artistRepository.save(dbArtist);
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
			List<my.spotify.data.Image> list = dataUtility.convertImages(artist.getImages());
			dbArtist.setImages(list);
			dbArtist = artistRepository.save(dbArtist);
			log.debug(dbArtist.toString());
		}
		return dbArtist;
	}

	public List<Track> fetchTracks(String playlistName) {
		List<Track> result = new ArrayList<Track>();

		List<com.wrapper.spotify.model_objects.specification.Track> spotifyTracks = myspotify
				.getTracksFromPlaylist(playlistName);

		for (com.wrapper.spotify.model_objects.specification.Track spotifyTrack : spotifyTracks) {
			Track dbTrack = saveTrack(spotifyTrack);
			result.add(dbTrack);

			String trackId = dbTrack.getSpotifyId();
			ArtistSimplified[] artists = spotifyTrack.getArtists();
			for (ArtistSimplified artist : artists) {
				Artist dbArtist = saveArtist(artist);
//				saveArtistTrack(dbArtist.getSpotifyId(), trackId);
			}
			for (int i = 0; i < artists.length; i++) {
				for (int j = i + 1; j < artists.length; j++) {
					String artist1Id = artists[i].getId();
					String artist2Id = artists[j].getId();
//					saveCollaboration(artist1Id, artist2Id, trackId);
				}
			}
		}

		return result;
	}

	public List<Artist> fetchOtherArtists(String artistId) {
		List<Artist> result = new ArrayList<Artist>();

		// emily added
		com.wrapper.spotify.model_objects.specification.Artist spotifyArtist = myspotify.getFullArtist(artistId);
		saveArtist(spotifyArtist);

		List<AlbumSimplified> artistsAlbums = myspotify.getArtistsAlbums(artistId);
		log.info("Album count for: " + artistId + " " + spotifyArtist.getName() + " = " + artistsAlbums.size());
		List<Album> fullSortedAlbums = myspotify.getFullSortedAlbums(artistsAlbums);
		List<TrackSimplified> albumTracks = myspotify.getAlbumTracks(fullSortedAlbums);
		log.info("Track count for: " + artistId + spotifyArtist.getName() + " = " + albumTracks.size());
		for (TrackSimplified track : albumTracks) {
			Track dbTrack = saveTrack(track);

			String trackId = dbTrack.getSpotifyId();
			ArtistSimplified[] artists = track.getArtists();
			for (ArtistSimplified artist : artists) {
				Artist dbArtist = saveArtist(artist);
				result.add(dbArtist);
//				saveArtistTrack(dbArtist.getSpotifyId(), trackId);
			}
			for (int i = 0; i < artists.length; i++) {
				for (int j = i + 1; j < artists.length; j++) {
					String artist1Id = artists[i].getId();
					String artist2Id = artists[j].getId();
//					saveCollaboration(artist1Id, artist2Id, trackId);
				}
			}
		}

		return result;
	}

	// emily added
	public List<Artist> fillArtists() {
		List<Artist> result = new ArrayList<Artist>();
		List<Artist> notProcessed = artistRepository.findByPopularity(-1);
		while (notProcessed.size() > 0) {
			fetchOtherArtists(notProcessed.get(0).getSpotifyId());
			result.add(notProcessed.get(0));
			log.info("Artist: " + notProcessed.get(0).getName() + " " + notProcessed.get(0).getSpotifyId());
			notProcessed = artistRepository.findByPopularity(-1);
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
