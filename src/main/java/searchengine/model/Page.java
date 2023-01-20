package searchengine.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "page", indexes = {
        @Index(name = "index_path", columnList = "path", unique = true)
})

public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "site_id", nullable = false, columnDefinition = "INT")
    private int siteId;

    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "code", nullable = false, columnDefinition = "INT")
    private int code;

    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    public Page(int siteId, String path, int code, String content){
        this.siteId = siteId;
        this.path = path;
        this.code = code;
        this.content = content;
    }




}
