package org.datavaultplatform.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name="Archives")
public class Archive {

    // Archive Identifier
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, length = 36)
    private String id;

    // Serialise date in ISO 8601 format
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    // A Deposit can have many Archives
    @ManyToOne
    private Deposit deposit;

    // An ArchiveStore can have many Archives
    @ManyToOne
    private ArchiveStore archiveStore;

    @Column(columnDefinition = "TEXT")
    private String archiveId;


    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public void setArchiveStore(ArchiveStore archiveStore) {
        this.archiveStore = archiveStore;
    }

    public ArchiveStore getArchiveStore() {
        return archiveStore;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getArchiveId() {
        return archiveId;
    }
}
