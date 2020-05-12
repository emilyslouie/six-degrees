package my.spotify.data;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

	Artist findBySpotifyId(String spotifyId);

	List<Artist> findByName(String name);

	List<Artist> findByPopularity(int popularity);

	@Query("MATCH p=shortestPath((start:Artist {spotifyId:$spotifyId})-[*]-(end:Artist {spotifyId:$spotifyId2})) RETURN p")
	List<Artist> findShortestPath(@Param("spotifyId") String spotifyId, @Param("spotifyId2") String spotifyId2);

	@Query("MATCH p=shortestPath(({spotifyId:$spotifyId})-[*1..12]-({spotifyId:$spotifyId2})) RETURN p")
	List<Map<String, Object>> findShortestPathBetweenAnything(@Param("spotifyId") String spotifyId,
			@Param("spotifyId2") String spotifyId2);

	@Query("MATCH p=shortestPath((start:Track{spotifyId:$spotifyId})-[*1..12]-(end:Artist{spotifyId:$spotifyId2})) RETURN p")
	List<Artist> findShortestPathBtwnTrackNArtist(@Param("spotifyId") String spotifyId,
			@Param("spotifyId2") String spotifyId2);

	// MATCH (bacon:Artist {spotifyId:"1111"})-[*1..2]-(otherArtists)
	// RETURN DISTINCT otherArtists

}
