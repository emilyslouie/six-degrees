package ca.louie.spotify.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.albums.GetSeveralAlbumsRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest.Builder;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchPlaylistsRequest;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MySpotify {

	@Value("${spotify.client}")
	private String clientId;
	@Value("${spotify.secret}")
	private String clientSecret;
	private long creationTime;

	public boolean flag = false;

	private SpotifyApi spotifyApi;

	@Synchronized
	private SpotifyApi getApi() {
		if (spotifyApi != null && (System.currentTimeMillis() - creationTime) < 3600000) {
			return spotifyApi;
		}
		try {
			if (creationTime > 0)
				log.info("" + (System.currentTimeMillis() - creationTime));
			spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();

			ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
			ClientCredentials clientCredentials = clientCredentialsRequest.execute();
			creationTime = System.currentTimeMillis();
			log.info("" + creationTime);
			// Set access token for further "spotifyApi" object usage
			spotifyApi.setAccessToken(clientCredentials.getAccessToken());

			log.info("Expires in: " + clientCredentials.getExpiresIn());
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			log.error("Error: " + e.getMessage());
		}
		return spotifyApi;
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public List<Artist> getArtistsByName(String name) {
		log.info("MySpotify getArtistsByName: " + name);
		SearchArtistsRequest searchArtistsRequest = getApi().searchArtists(name).limit(5).market(CountryCode.CA)
				.build();
		Paging<Artist> paging = null;
		try {
			paging = searchArtistsRequest.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error("Get artist from name", e);
		}
		Artist[] artists = paging.getItems();
		return Arrays.asList(artists);
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public Paging<PlaylistSimplified> getPlaylist(String playlistName) {
		log.info("MySpotify getPlaylist: " + playlistName);
		SearchPlaylistsRequest playlistRequest = getApi().searchPlaylists(playlistName).limit(1).market(CountryCode.CA)
				.build();
		Paging<PlaylistSimplified> playlistResults = null;
		try {
			playlistResults = playlistRequest.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error("Get playlist", e);
		}
		return playlistResults;
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public List<Track> getTracksFromPlaylist(String playlistName) {
		log.info("MySpotify getTracksFromPlaylist: " + playlistName);
		List<Track> tracks = new ArrayList<Track>();

		SearchPlaylistsRequest playlistRequest = getApi().searchPlaylists(playlistName).limit(1).market(CountryCode.CA)
				.build();
		Paging<PlaylistSimplified> playlistResults;
		try {
			playlistResults = playlistRequest.execute();
			PlaylistSimplified[] playlist = playlistResults.getItems();
			String playlistId = playlist[0].getId();

			GetPlaylistsItemsRequest playlistItemsRequest = getApi().getPlaylistsItems(playlistId).limit(100)
					.market(CountryCode.CA).build();
			Paging<PlaylistTrack> playlistItemsResults = playlistItemsRequest.execute();
			PlaylistTrack[] playlistTracks = playlistItemsResults.getItems();
			for (PlaylistTrack playlistTrack : playlistTracks) {
				Track track = (Track) playlistTrack.getTrack();
				tracks.add(track);
			}
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error("Get playlist tracks", e);
		}
		return tracks;
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public List<AlbumSimplified> getArtistsAlbums(String id) {
		log.info("MySpotify getArtistsAlbums: " + id);
		List<AlbumSimplified> list = new ArrayList<AlbumSimplified>();
		int offset = 0;
		int limit = 50;

		Builder builder = getApi().getArtistsAlbums(id).limit(limit).market(CountryCode.CA);
		GetArtistsAlbumsRequest getArtistsAlbumsRequest = builder.build();
		Paging<AlbumSimplified> paging;
		try {
			paging = getArtistsAlbumsRequest.execute();
			int maxCount = paging.getTotal();
			list.addAll(Arrays.asList(paging.getItems()));

			offset += limit;
			while (offset < maxCount) {
				builder.offset(offset);
				getArtistsAlbumsRequest = builder.build();
				paging = getArtistsAlbumsRequest.execute();
				list.addAll(Arrays.asList(paging.getItems()));
				offset += limit;
			}
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error("Get artist albums", e);
		}

		return list;
	}

	public String combineIds(List<AlbumSimplified> albums) {
		String ids = albums.get(0).getId();
		for (int i = 1; i < albums.size(); i++) {
			ids = ids + "," + albums.get(i).getId();
		}
		return ids;
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public List<Album> getFullSortedAlbums(List<AlbumSimplified> albums) {
		log.info("MySpotify getFullSortedAlbums: albums count=" + albums.size());
		List<Album> list = new ArrayList<Album>();
		int offset = 0;
		int limit = 20;
		while (offset < albums.size()) {
			int toIndex = offset + limit;
			if (toIndex > albums.size()) {
				toIndex = albums.size();
			}
			List<AlbumSimplified> listToGet = albums.subList(offset, toIndex);
			String allAlbums = combineIds(listToGet);
			GetSeveralAlbumsRequest severalAlbums = getApi().getSeveralAlbums(allAlbums).build();

			try {
				Album[] fullAlbumObjects = severalAlbums.execute();
				list.addAll(Arrays.asList(fullAlbumObjects));
				offset += limit;
			} catch (ParseException | SpotifyWebApiException | IOException e) {
				log.error("Get full albums", e);
			}
		}
		return list;
	}

	public TrackSimplified[] getTracks(Album fullAlbumObject) {
		return fullAlbumObject.getTracks().getItems();
	}

	public ArtistSimplified[] getArtist(TrackSimplified track) {
		return track.getArtists();
	}

	public List<TrackSimplified> getAlbumTracks(List<Album> albums) {
		List<TrackSimplified> artistSet = new ArrayList<TrackSimplified>();
		for (int i = 0; i < albums.size(); i++) {
			for (TrackSimplified track : getTracks(albums.get(i))) {
				artistSet.add(track);
			}
		}
		return artistSet;
	}

	public Set<ArtistSimplified> getArtists(List<Album> albums) {
		Set<ArtistSimplified> artistSet = new HashSet<>();
		for (int i = 0; i < albums.size(); i++) {
			for (TrackSimplified track : getTracks(albums.get(i))) {
				for (ArtistSimplified artist : getArtist(track)) {
					artistSet.add(artist);
				}
			}
		}
		return artistSet;
	}

	public String getArtistId(String name) {
		SearchArtistsRequest searchArtistsRequest = getApi().searchArtists(name).limit(1).build();
		Paging<Artist> paging = null;
		try {
			paging = searchArtistsRequest.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error("Get artist from name", e);
		}
		Artist[] artists = paging.getItems();
		Artist artist = artists[0];
		return artist.getId();
	}

	public void addAllArtists(Album[] albums, Queue<String> search, HashSet<String> seen,
			HashMap<String, String> connections, String parent, String end) {
		for (int i = 0; i < albums.length; i++) {
			for (TrackSimplified track : getTracks(albums[i])) {
				for (ArtistSimplified artist : getArtist(track)) {
					if (!seen.contains(artist.getId())) {
						search.add(artist.getId());
						System.out.println(artist.getName() + " ");
						seen.add(artist.getId());
						connections.put(artist.getId(), parent);
					}
					if (artist.getId().equals(end)) {
						search = new LinkedList<>();
						flag = true;
						return;
					}
				}
			}
		}
	}

	public void getCollaboratedArtists(Queue<String> search, HashSet<String> seen, HashMap<String, String> connections,
			String end) {
		String current = search.poll();
	}

	public String search(String start, String end) {
		Queue<String> queue = new LinkedList<String>();
		HashSet<String> seen = new HashSet<String>();

		HashMap<String, String> connections = new HashMap<String, String>();

		queue.add(start);

		while (!queue.isEmpty()) {
			getCollaboratedArtists(queue, seen, connections, end);
			if (flag)
				break;
		}

		String key = end;

		String out = key + " ";

		while (key != start) {
			key = connections.get(key);
			out += key + " ";

		}

		return out;
	}

	@Retryable(backoff = @Backoff(delay = 5000))
	public Artist getFullArtist(String spotifyArtistId) {
		log.info("MySpotify getFullArtist: " + spotifyArtistId);
		GetArtistRequest getArtistRequest = getApi().getArtist(spotifyArtistId).build();
		Artist artist = null;
		try {
			artist = getArtistRequest.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error("Get artist", e);
		}
		return artist;
	}

	public int getArtistPop(String artist) {
		return getFullArtist(artist).getPopularity();
	}
}
