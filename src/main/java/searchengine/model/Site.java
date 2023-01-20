package searchengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private StatusType status;

    @Column(name = "status_time", nullable = false, columnDefinition = "DATETIME")
    private long statusTime;

    @Column(name = "last_error_txt", columnDefinition = "TEXT")
    private String lastErrorTxt;

    @Column(name = "site_url", nullable = false, columnDefinition = "VARCHAR(255)")
    private String siteUrl;

    @Column(name = "site_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String siteName;

    public Site (StatusType status, long statusTime, String lastErrorTxt, String siteUrl, String siteName){
        this.status = status;
        this.statusTime = statusTime;
        this.lastErrorTxt = lastErrorTxt;
        this.siteUrl = siteUrl;
        this.siteName = siteName;
    }


}
