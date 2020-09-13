package ca.louie.spotify.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wrapper.spotify.model_objects.specification.Image;

import ca.louie.spotify.data.Artist;
import ca.louie.spotify.data.Image.ImageBuilder;
import ca.louie.spotify.data.Track;

@Service
public class DataUtility {
	public Artist convertToDbArtist(com.wrapper.spotify.model_objects.specification.Artist spotifyArtists) {
		String id = spotifyArtists.getId();
		String name = spotifyArtists.getName();
		String url = spotifyArtists.getExternalUrls().get("spotify");
		List<String> genres = Arrays.asList(spotifyArtists.getGenres());
		List<Image> images = Arrays.asList(spotifyArtists.getImages());
		int popularity = (int) spotifyArtists.getPopularity();

		Artist dbArtist = new Artist(id, name, url, genres, images, popularity);

		return dbArtist;
	}

	public List<ca.louie.spotify.data.Image> convertImages(
			com.wrapper.spotify.model_objects.specification.Image[] images) {
		ImageBuilder builder = ca.louie.spotify.data.Image.builder();
		List<ca.louie.spotify.data.Image> list = new ArrayList<ca.louie.spotify.data.Image>();
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
