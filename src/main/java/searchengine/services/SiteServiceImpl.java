package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConf;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.model.StatusType;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService{

    private final SiteRepository siteRepository;
    private final NetworkService networkService;

    @Override
    public Site save(Site site) {
        return siteRepository.saveAndFlush(site);
    }

    @Override
    public void saveAll(List<Site> sites) {
        siteRepository.saveAllAndFlush(sites);
    }

    @Override
    public Site getByUrl(String url) {
        return siteRepository.getByUrl(url);
    }

    @Override
    public Site createSite(SiteConf siteConf) {
        Site site = null;
        boolean isAvailable = networkService
                .checkSiteConnection(siteConf.getUrl());
        String lastError = isAvailable ? "" : "Сайт не доступен";
        StatusType status = isAvailable ? StatusType.INDEXING : StatusType.FAILED;

        site = new Site();
        site.setSiteUrl(siteConf.getUrl());
        site.setSiteName(siteConf.getName());

        site.setStatus(status);
        site.setStatusTime(LocalDateTime.now());
        site.setLastErrorTxt(lastError);
        return site;
    }

    @Override
    public List<Site> getSitesToParsing(SitesList sites) {
        List<Site> sitesToParsing = new ArrayList<>();
        for (SiteConf siteCfg : sites.getSites()) {
            Site site = createSite(siteCfg);
            sitesToParsing.add(site);
        }
        return sitesToParsing;
    }

    @Override
    public Site addSiteToParsing(Site site) {
        return null;
    }

    @Override
    public List<Site> getAll() {
        return siteRepository.findAll();
    }

    @Override
    public void deleteAll() {
        siteRepository.deleteAll();
    }

    @Override
    public boolean isIndexing() {
        return siteRepository.findAnyStatus(StatusType.INDEXING) != null;
    }

    @Override
    public void dropIndexingStatus() {
        siteRepository.updateStatus(StatusType.INDEXING, StatusType.FAILED);
    }

    @Override
    public void updateSiteStatus(Site site, StatusType status, String lastError) {
        site.setStatus(status);
        site.setLastErrorTxt(lastError);
        site.setStatusTime(LocalDateTime.now());
        save(site);
    }
}
