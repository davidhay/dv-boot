package org.datavaultplatform.common.model;


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
@ApiObject(name = "DataManager")
@Entity
@Table(name="DataManagers")
public class DataManager {

    // Data Manager Identifier
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, length = 36)
    private String id;
    
    @ApiObjectField(description = "Data Manager UUN")
    @Column(name = "uun", columnDefinition = "TEXT", length = 36)
    private String uun;
    
    @ManyToOne
    private Vault vault;

    public DataManager() {}
    
    public DataManager(String uun) {
        this.uun = uun;
    }

    public String getID() {
        return id;
    }
    
    public Vault getVault() { return vault; }
    
    public void setVault(Vault vault) {
        this.vault = vault;
    }
    
    public String getUUN() {
        return uun;
    }
    
    public void setUUN(String uun) {
        this.uun = uun;
    }
}
