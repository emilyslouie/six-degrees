package my.spotify.data;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NodeEntity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Track {

	@Id
	@GeneratedValue
	private Long id;

	private String spotifyId;
	private String name;
	private int discNumber;
	private int trackNumber;
	private String externalUrl;
	private String previewUrl;

	public Track(String spotifyId, String name) {
		this.spotifyId = spotifyId;
		this.name = name;
	}
}
