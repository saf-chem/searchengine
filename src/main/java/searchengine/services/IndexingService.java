package searchengine.services;


import searchengine.config.SiteConf;
import searchengine.config.SitesList;
import searchengine.dto.statistics.indexing.IndexingResponse;
import searchengine.model.Site;
import searchengine.model.Status;

import java.util.List;

public interface IndexingService {
    IndexingResponse startIndexing();

    Site createSite(SiteConf siteConf, Status status, String lastError);

    List<Site> getSitesToParsing(SitesList sites);

    void saveAll(List<Site> sites);

    boolean isIndexing();
}
