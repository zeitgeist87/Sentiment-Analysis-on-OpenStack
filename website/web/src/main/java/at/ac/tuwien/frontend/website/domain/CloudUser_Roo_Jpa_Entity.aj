// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package at.ac.tuwien.frontend.website.domain;

import at.ac.tuwien.frontend.website.domain.CloudUser;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect CloudUser_Roo_Jpa_Entity {
    
    declare @type: CloudUser: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long CloudUser.id;
    
    @Version
    @Column(name = "version")
    private Integer CloudUser.version;
    
    public Long CloudUser.getId() {
        return this.id;
    }
    
    public void CloudUser.setId(Long id) {
        this.id = id;
    }
    
    public Integer CloudUser.getVersion() {
        return this.version;
    }
    
    public void CloudUser.setVersion(Integer version) {
        this.version = version;
    }
    
}