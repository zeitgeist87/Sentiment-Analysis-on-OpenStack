// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.domain;

import aic.website.domain.AuthUser;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect AuthUser_Roo_Jpa_Entity {
    
    declare @type: AuthUser: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long AuthUser.id;
    
    @Version
    @Column(name = "version")
    private Integer AuthUser.version;
    
    public Long AuthUser.getId() {
        return this.id;
    }
    
    public void AuthUser.setId(Long id) {
        this.id = id;
    }
    
    public Integer AuthUser.getVersion() {
        return this.version;
    }
    
    public void AuthUser.setVersion(Integer version) {
        this.version = version;
    }
    
}
