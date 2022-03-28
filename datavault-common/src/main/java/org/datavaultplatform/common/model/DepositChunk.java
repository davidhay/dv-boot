package org.datavaultplatform.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiObject(name = "DepositChunk")
@Entity
@Table(name="DepositChunks")
public class DepositChunk {

    // Deposit Identifier
    @Id
    @ApiObjectField(description = "Universally Unique Identifier for the Deposit Path", name="Deposit Path")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, length = 36)
    private String id;
    
    @JsonIgnore
    @ManyToOne
    private Deposit deposit;
    
    // Record the file path that the user selected for this deposit.
    @ApiObjectField(description = "Filepath of the origin deposit")
    @Column(columnDefinition = "INT")
    private int chunkNum;
    
    // Hash of the deposit chunk
    @Column(columnDefinition = "TEXT")
    private String archiveDigest;
    @Column(columnDefinition = "TEXT")
    private String archiveDigestAlgorithm;
    
    // Encryption
    @Column(columnDefinition = "BLOB")
    private byte[] encIV;
    @Column(columnDefinition = "TEXT")
    private String ecnArchiveDigest;

    public DepositChunk() {}
    public DepositChunk(Deposit deposit, int chunkNum, String archiveDigest, String archiveDigestAlgorithm) {
        this.deposit = deposit;
        this.chunkNum = chunkNum;
        this.archiveDigest = archiveDigest;
        this.archiveDigestAlgorithm = archiveDigestAlgorithm;
    }

    public String getID() { return id; }
    
    public int getChunkNum() {
        return chunkNum;
    }
    
    public void setChunkNum(int chunkNum) {
        this.chunkNum = chunkNum;
    }
    
    public String getArchiveDigest() {
        return archiveDigest;
    }
    
    public void setArchiveDigest(String archiveDigest) {
        this.archiveDigest = archiveDigest;
    }
    
    public String getArchiveDigestAlgorithm() {
        return archiveDigestAlgorithm;
    }
    
    public void setArchiveDigestAlgorithm(String archiveDigestAlgorithm) {
        this.archiveDigestAlgorithm = archiveDigestAlgorithm;
    }
    
    public byte[] getEncIV() {
        return encIV;
    }
    
    public void setEncIV(byte[] encIV) {
        this.encIV = encIV;
    }
    
    public String getEcnArchiveDigest() {
        return ecnArchiveDigest;
    }
    
    public void setEcnArchiveDigest(String ecnArchiveDigest) {
        this.ecnArchiveDigest = ecnArchiveDigest;
    }

    public Deposit getDeposit() {
        return deposit;
    }
}
