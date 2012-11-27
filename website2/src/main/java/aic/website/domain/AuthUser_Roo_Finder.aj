// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.domain;

import aic.website.domain.AuthUser;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect AuthUser_Roo_Finder {
    
    public static TypedQuery<AuthUser> AuthUser.findAuthUsersByNameEquals(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = AuthUser.entityManager();
        TypedQuery<AuthUser> q = em.createQuery("SELECT o FROM AuthUser AS o WHERE o.name = :name", AuthUser.class);
        q.setParameter("name", name);
        return q;
    }
    
}