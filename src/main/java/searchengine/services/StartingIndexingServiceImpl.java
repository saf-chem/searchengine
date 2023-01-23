package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.ParserConf;
import searchengine.config.SitesList;
import searchengine.dto.statistics.indexing.IndexingResponse;
import searchengine.model.Site;
import searchengine.model.StatusType;
import searchengine.utils.Parser;
import searchengine.utils.ThreadHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class StartingIndexingServiceImpl implements StartIndexingService{

    private final SitesList sites;
    private final ParserConf parserConf;
    private final FactoryService factoryService;
    private ThreadPoolExecutor threadPoolExecutor;

    public StartingIndexingServiceImpl(SitesList sites, ParserConf parserConf, FactoryService factoryService) {
        this.sites = sites;
        this.parserConf = parserConf;
        this.factoryService = factoryService;
        this.factoryService.getSiteService().dropIndexingStatus();
    }
    @Override
    public IndexingResponse startIndexing() {

        if (factoryService.getSiteService().isIndexing())
        {
            return new IndexingResponse(false, "Идет индексация");
        }

        Parser.setIsCanceled(false);

        Thread thread = new Thread(() -> {
            factoryService.getLemmaService().deleteAll();
            factoryService.getPageService().deleteAll();
            factoryService.getSiteService().deleteAll();

            List<Site> sitesToParsing = factoryService.getSiteService().getSitesToParsing(sites);
            factoryService.getSiteService().saveAll(sitesToParsing);

            for (Site site : sitesToParsing) {
                if (site.getStatus() == StatusType.INDEXING) {
                    ThreadHandler task = new ThreadHandler(parserConf, factoryService, site);
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
}
