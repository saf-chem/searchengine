package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "parsing-settings")
public class ParserConf {

    private String userAgent;
    private String referer;
    private String contentType;
    private int timeout;
    private int lemmasTreshhold;
    private int parallelism;
    private int threadDelay;


}
