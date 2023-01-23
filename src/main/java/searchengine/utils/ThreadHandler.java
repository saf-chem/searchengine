package searchengine.utils;

import lombok.extern.slf4j.Slf4j;
import searchengine.config.ParserConf;
import searchengine.model.Site;
import searchengine.model.StatusType;
import searchengine.services.FactoryService;

import java.util.concurrent.ForkJoinPool;

@Slf4j
public class ThreadHandler implements Runnable{
    private static ParserConf parserConf;
    private static FactoryService factoryService;
    private static ForkJoinPool forkJoinPool;
    private Site site;

    public ThreadHandler(ParserConf parserConf, FactoryService factoryService, Site site) {
        ThreadHandler.parserConf = parserConf;
        ThreadHandler.factoryService = factoryService;
        this.site = site;
        if (forkJoinPool == null) {
            forkJoinPool = new ForkJoinPool(parserConf.getParallelism());
        }
    }
    @Override
    public void run(){
        try {
            Long start = System.currentTimeMillis();
            LemmaFinder lemmaFinder = LemmaFinder.getInstance();
            Parser parser = new Parser(site, site.getSiteUrl() + "/", factoryService, lemmaFinder, parserConf);
            if (forkJoinPool.invoke(parser)) {
                factoryService.getLemmaService().mergeFrequency(parser.getLemmaFrequency());

                factoryService.getSiteService().updateSiteStatus(site, StatusType.INDEXED, "");
                log.info(site.getSiteUrl() + " - " + String.valueOf(System.currentTimeMillis() - start));
            } else {
                factoryService.getSiteService().updateSiteStatus(site, StatusType.FAILED, "Индексация остановлена пользователем");
            }
        } catch (Exception e) {
            log.info("Старт индексации ошибка " + e.getMessage());
        }
    }
}

