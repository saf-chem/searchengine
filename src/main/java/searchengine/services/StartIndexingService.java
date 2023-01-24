package searchengine.services;

import org.jsoup.Connection;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.indexing.IndexingResponse;
import searchengine.model.Site;

import java.util.List;
import java.util.Map;

@Service
public interface StartIndexingService {
    IndexingResponse startIndexing();
    IndexingResponse stopIndexing();
    IndexingResponse indexPage(String url);
    void parsePage(Site site, Connection.Response response) throws Exception;
}
