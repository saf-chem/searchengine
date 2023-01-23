package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;

import javax.transaction.Transactional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value ="INSERT INTO lemmas (site_id, lemma, frequency) VALUES (?1, ?2, ?3) ON DUPLICATE KEY UPDATE `frequency`=`frequency` + ?3", nativeQuery = true)
    void merge(int siteId, String lemma, int frequency);

    @Query(value ="SELECT COUNT(*) FROM lemmas WHERE site_id = :siteId", nativeQuery = true)
    Integer getLemmasCount(int siteId);
}
