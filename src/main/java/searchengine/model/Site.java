package searchengine.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sites")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING','INDEXED', 'FAILED')", nullable = false)
    Status status;

    @Column(name = "status_time", nullable = false)
    LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    String lastError;

    @Column(name = "url", columnDefinition = "VARCHAR(255)", nullable = false)
    String url;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", nullable = false)
    String name;

}
