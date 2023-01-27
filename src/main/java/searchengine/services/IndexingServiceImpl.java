package searchengine.services;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.Connection;
import searchengine.config.SiteConf;
import searchengine.config.SitesList;
import searchengine.dto.statistics.indexing.IndexingResponse;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class IndexingServiceImpl implements IndexingService{

    private final SiteRepository siteRepository;
    private final Connection connection;
    private final SitesList sites;



    public IndexingServiceImpl(SiteRepository siteRepository, Connection connection, SitesList sites) {
        this.siteRepository = siteRepository;
        this.connection = connection;
        this.sites = sites;

    }

    @Override
    public IndexingResponse startIndexing(){

        if (isIndexing()){
            return new IndexingResponse(false, "Идет индексация");
        }
        for (SiteConf siteConf : sites.getSites()){
            Site site = new Site();
            site.setUrl(siteConf.getUrl());
            site.setName(siteConf.getName());
            String lastError = "";
            Status status = Status.INDEXING;
            site.setLastError(lastError);
            site.setStatus(status);
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);

        }
        return new IndexingResponse(true, "");
    }
    @Override
    public Site createSite(SiteConf siteConf, Status status, String lastError) {
        Site site = new Site();
        site.setUrl(siteConf.getUrl());
        site.setName(siteConf.getName());
        site.setStatus(status);
        site.setStatusTime(LocalDateTime.now());
        site.setLastError(lastError);
        return site;
    }
    @Override
    public List<Site> getSitesToParsing(SitesList sites){
        List<Site> sitesToParsing = new ArrayList<>();
        for (SiteConf siteConf : sites.getSites()){
            String lastError = "Ok";
            Status status = Status.INDEXING;
            Site site = createSite(siteConf, status, lastError);
            sitesToParsing.add(site);
        }
        return sitesToParsing;
    }
    @Override
    public void saveAll(List<Site> sites){
        siteRepository.saveAllAndFlush(sites);
    }
    @Override
    public boolean isIndexing() {
        return siteRepository.findAnyStatus(Status.INDEXING) != null;
    }
}
