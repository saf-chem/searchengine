package searchengine.services;

import searchengine.dto.statistics.indexing.IndexingResponse;

import java.util.Map;

public interface StartIndexingService {
    IndexingResponse startIndexing();
    IndexingResponse stopIndexing();
}
