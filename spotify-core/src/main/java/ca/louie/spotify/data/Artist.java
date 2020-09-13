package ca.louie.spotify.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

	@Id
	@GeneratedValue
	@Getter
	@Setter
	private Long id;

	// A basic artist has these fields
	@Getter
	@Setter
	private String spotifyId;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String externalUrl; // a spotify web page for the artist

	// A complete artist has these additional fields
	@Getter
	@Setter
	private List<String> genres;

	private int popularity = -1; // popularity is 0 - 100, -1 indicates a basic artist

	private List<String> imageUrl;
	private List<Integer> imageHeight;
	private List<Integer> imageWidth;

	// false = not processed artist's tracks
	// true = processed all artist's tracks
	@Getter
	@Setter
	private boolean processStatus;

	@Relationship(type = "PLAYSON", direction = Relationship.UNDIRECTED)
	@Getter
	@Setter
	public Set<Track> tracks;

	public Artist(String spotifyId, String name, String externalUrl) {
		super();
		this.spotifyId = spotifyId;
		this.name = name;
		this.externalUrl = externalUrl;
		this.popularity = -1;
		this.processStatus = false;
	}

	public Artist(String spotifyId, String name, String externalUrl, List<String> genres,
			List<com.wrapper.spotify.model_objects.specification.Image> images, int popularity) {
		super();
		this.spotifyId = spotifyId;
		this.name = name;
		this.externalUrl = externalUrl;
		this.genres = genres;
		for (com.wrapper.spotify.model_objects.specification.Image image : images) {
			addImage(image.getUrl(), image.getHeight(), image.getWidth());
		}
		this.popularity = popularity;
		this.processStatus = false;
	}

	public boolean isSimple() {
		return getPopularity() == -1;
	}

	public void addImage(String url, Integer height, Integer width) {
		if (imageUrl == null) {
			imageUrl = new ArrayList<String>();
			imageHeight = new ArrayList<Integer>();
			imageWidth = new ArrayList<Integer>();
		}
		imageUrl.add(url);
		imageHeight.add(height);
		imageWidth.add(width);

	}

	public List<Image> getImages() {
		List<Image> images = new ArrayList<Image>();
		for (int i = 0; i < this.imageUrl.size(); i++) {
			Image image = new Image(this.imageUrl.get(i), this.imageHeight.get(i), this.imageWidth.get(i));
			images.add(image);
		}
		return images;

	}

	public void setImages(List<Image> images) {
		for (Image image : images) {
			imageUrl.add(image.getUrl());
			imageHeight.add(image.getHeight());
			imageWidth.add(image.getWidth());
		}
	}

	public void playsOn(Track track) {
		if (tracks == null) {
			tracks = new HashSet<>();
		}
		tracks.add(track);
	}

	@Override
	public String toString() {
		final int maxLen = 2;
		return String.format(
				"Artist [id=%s, spotifyId=%s, name=%s, externalUrl=%s, genres=%s, popularity=%s, images=%s, collaborators=%s, tracks=%s]",
				id, spotifyId, name, externalUrl, genres != null ? toString(genres, maxLen) : null, popularity,
				imageUrl != null ? toString(imageUrl, maxLen) : null, tracks != null ? toString(tracks, maxLen) : null);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
}
