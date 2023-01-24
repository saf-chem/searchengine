package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.springframework.stereotype.Service;
import searchengine.config.ParserConf;
import searchengine.config.SiteConf;
import searchengine.config.SitesList;
import searchengine.dto.statistics.indexing.IndexingResponse;
import searchengine.exception.ResourceNotFoundException;
import searchengine.model.*;
import searchengine.utils.LemmaFinder;
import searchengine.utils.Parser;
import searchengine.utils.ThreadHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@Service
public class StartingIndexingServiceImpl implements StartIndexingService{

    private final SitesList sites;
    private final ParserConf parserConf;
    private final NetworkService networkService;
    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;

    public StartingIndexingServiceImpl(SitesList sites, ParserConf parserConf,
                               NetworkService networkService, SiteService siteService,
                               PageService pageService, LemmaService lemmaService,
                               IndexService indexService) {
        this.sites = sites;
        this.parserConf = parserConf;
        this.networkService = networkService;
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        siteService.dropIndexingStatus();
    }

    @Override
    public IndexingResponse startIndexing() {
        if (siteService.isIndexing())
        {
            return new IndexingResponse(false, "Идет индексация");
        }

        Parser.setIsCanceled(false);

        Thread thread = new Thread(() -> {
            indexService.deleteAll();
            lemmaService.deleteAll();
            pageService.deleteAll();
            siteService.deleteAll();

            List<Site> sitesToParsing = siteService.getSitesToParsing(sites);
            siteService.saveAll(sitesToParsing);

            for (Site site : sitesToParsing) {
                if (site.getStatus() == Status.INDEXING) {
                    ThreadHandler task = new ThreadHandler(parserConf, networkService, siteService,
                            this, site, site.getUrl() + "/");
                    Thread parseSite = new Thread(task);
                    parseSite.start();
                }
            }
        });
        thread.start();

        return new IndexingResponse(true, "");
    }

    @Override
    public IndexingResponse stopIndexing() {
        Parser.setIsCanceled(true);
        return new IndexingResponse(true, "");
    }

    @Override
    public IndexingResponse indexPage(String url) {
        try {
            String finalUrl = url;
            Optional findUrl = sites.getSites().stream()
                    .filter(s -> finalUrl.startsWith(s.getUrl()))
                    .findFirst();
            if (findUrl.isEmpty()) {
                return new IndexingResponse(false, "Данная страница находится за пределами сайтов, " +
                        "указанных в конфигурационном файле");
            }
            SiteConf siteConf = (SiteConf) findUrl.get();
            url = url.equals(siteConf.getUrl()) ? url + "/" : url;

            Connection.Response response = networkService.getResponse(url);
            if (!networkService.isAvailableContent(response)) {
                return new IndexingResponse(false, "Ошибка обработки страницы " + url);
            }

            Site site = siteService.getByUrl(siteConf.getUrl());
            if (site == null) {
                site = siteService.createSite(siteConf);
                site.setStatus(Status.INDEXED);
                site = siteService.save(site);
            }

            parsePage(site, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new IndexingResponse(false, e.getMessage());
        }
        return new IndexingResponse(true, "");
    }

    @Override
    public void parsePage(Site site, Connection.Response response) throws Exception {
        Page page = pageService.addPage(site, response);

        LemmaFinder lemmaFinder = LemmaFinder.getInstance();
        Map<String, Integer> lemmaMap = lemmaFinder.collectLemmas(page.getContent());
        List<Lemma> lemmas = lemmaService.createLemmas(lemmaMap.keySet(), site);
        lemmaService.mergeFrequency(lemmas);

        List<Index> indexes = indexService.addIndexes(lemmaMap, site, page);
        indexService.saveAll(indexes);
    }
}
