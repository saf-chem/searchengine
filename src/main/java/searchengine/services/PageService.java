package searchengine.services;

import searchengine.model.Page;

public interface PageService {
    void save(Page page);
    void deleteAll();
    boolean existPagePath(int siteId, String path);
    int getPagesCount(int siteId);
}
