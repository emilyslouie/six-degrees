package ca.louie.spotify.data;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArtistRepository extends CrudRepository<Artist, Long>, CachingArtistRepository {

	@Cacheable(cacheNames = "artists", key = "#spotifyId")
	Artist findBySpotifyId(String spotifyId);

	List<Artist> findByNameContainingIgnoreCaseOrderByPopularityDesc(String name, Sort sort);

	List<Artist> findByPopularity(int popularity);

	List<Artist> findByProcessStatus(boolean status);

	@Query("MATCH p=shortestPath((start:Artist {spotifyId:$spotifyId})-[*]-(end:Artist {spotifyId:$spotifyId2})) RETURN p")
	List<Artist> findShortestPath(@Param("spotifyId") String spotifyId, @Param("spotifyId2") String spotifyId2);

}
