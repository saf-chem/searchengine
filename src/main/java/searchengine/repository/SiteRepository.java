package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;
import searchengine.model.Status;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    @Query(value = "SELECT * from sites where status LIKE %:status%  LIMIT 1", nativeQuery = true)
    Site findAnyStatus(Status status);
}
