package my.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import my.spotify.data.Artist;
import my.spotify.data.Image.ImageBuilder;
import my.spotify.data.Track;

@Service
public class DataUtility {
//	public Artist convertToDbArtist(com.wrapper.spotify.model_objects.specification.ArtistSimplified spotifyArtists) {
//
//		
//		
////		ArtistBuilder artistBuilder = my.spotify.data.Artist.builder();
////		// @formatter:off
////		artistBuilder.spotifyId(spotifyArtists.getId())
////					.name(spotifyArtists.getName())
////					.externalUrl(spotifyArtists.getExternalUrls().get("spotify"));
////		Artist dbArtist = artistBuilder.build();
////
////		return dbArtist;
//		return null;
//	}

	public Artist convertToDbArtist(com.wrapper.spotify.model_objects.specification.Artist spotifyArtists) {

		
//		Artist dbArtist = new Artist(spotifyArtists.getId(), spotifyArtists.getName(), spotifyArtists.getExternalUrls(), spotifyArtists.getGenres(), spotifyArtists.getPopularity(), spotifyArtists.getImages(), );
		
//		com.wrapper.spotify.model_objects.specification.Image[] images = spotifyArtists.getImages();
//		ArtistBuilder artistBuilder = my.spotify.data.Artist.builder();
//		// @formatter:off
//		artistBuilder.spotifyId(spotifyArtists.getId())
//					.name(spotifyArtists.getName())
//					.popularity(spotifyArtists.getPopularity())
//					.externalUrl(spotifyArtists.getExternalUrls().get("spotify"));
//		// @formatter:on
//		Artist dbArtist = artistBuilder.build();
//		List<my.spotify.data.Image> list = convertImages(images);
//		dbArtist.setImages(list);

//		return dbArtist;
		return null;
	}

	public List<my.spotify.data.Image> convertImages(com.wrapper.spotify.model_objects.specification.Image[] images) {
		ImageBuilder builder = my.spotify.data.Image.builder();
		List<my.spotify.data.Image> list = new ArrayList<my.spotify.data.Image>();
		for (int i = 0; i < images.length; i++) {
			builder.height(images[i].getHeight());
			builder.width(images[i].getWidth());
			builder.url(images[i].getUrl());
			list.add(builder.build());
		}
		return list;
	}

	public Track convertToDbTrack(com.wrapper.spotify.model_objects.specification.Track spotifyTrack) {
		// @formatter:off
		Track track = Track.builder()
				.spotifyId(spotifyTrack.getId())
				.name(spotifyTrack.getName())
				.discNumber(spotifyTrack.getDiscNumber())
				.trackNumber(spotifyTrack.getTrackNumber())
				.externalUrl(spotifyTrack.getExternalUrls().get("spotify"))
				.previewUrl(spotifyTrack.getPreviewUrl())
				.build();
		// @formatter:on
		return track;
	}

	public Track convertToDbTrack(com.wrapper.spotify.model_objects.specification.TrackSimplified spotifyTrack) {
		// @formatter:off
		Track track = Track.builder()
				.spotifyId(spotifyTrack.getId())
				.name(spotifyTrack.getName())
				.discNumber(spotifyTrack.getDiscNumber())
				.trackNumber(spotifyTrack.getTrackNumber())
				.externalUrl(spotifyTrack.getExternalUrls().get("spotify"))
				.previewUrl(spotifyTrack.getPreviewUrl())
				.build();
		// @formatter:on
		return track;
	}

}
