package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.config.SiteConf;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.model.StatusType;

import java.util.List;

@Service
public interface SiteService {

    Site save(Site site);
    void saveAll(List<Site> sites);
    Site getByUrl(String url);
    Site createSite(SiteConf siteConf);
    List<Site> getSitesToParsing(SitesList sites);
    Site addSiteToParsing(Site site);
    List<Site> getAll();
    void deleteAll();
    boolean isIndexing();
    void dropIndexingStatus();
    void updateSiteStatus(Site site, StatusType status, String lastError);
}
