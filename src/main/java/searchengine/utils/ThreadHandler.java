package searchengine.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import searchengine.config.ParserConf;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.services.FactoryService;
import searchengine.services.StartIndexingService;
import searchengine.services.NetworkService;
import searchengine.services.SiteService;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@Slf4j
public class ThreadHandler implements Runnable{
    private static ForkJoinPool forkJoinPool;
    private ParserConf parserConf;
    private NetworkService networkService;
    private SiteService siteService;
    private StartIndexingService indexingService;
    private Site site;
    private String startUrl;

    public ThreadHandler(ParserConf parserConf, NetworkService networkService,
                         SiteService siteService, StartIndexingService indexingService,
                         Site site, String startUrl) {
        this.parserConf = parserConf;
        this.indexingService = indexingService;
        this.networkService = networkService;
        this.siteService = siteService;
        this.site = site;
        this.startUrl = startUrl;
        if (forkJoinPool == null) {
            forkJoinPool = new ForkJoinPool(parserConf.getParallelism());
        }
    }

    @Override
    public void run() {
        try {
            Parser parser = new Parser(site, startUrl, networkService, indexingService, parserConf);
            if (forkJoinPool.invoke(parser)) {
                siteService.updateSiteStatus(site, Status.INDEXED, "");
            } else {
                siteService.updateSiteStatus(site, Status.FAILED, "Индексация остановлена пользователем");
            }
        } catch (Exception e) {
            log.info("Старт индексации ошибка " + e.getMessage());
        }
    }
}

