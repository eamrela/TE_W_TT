/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.te.tt.beans;

import com.te.tt.entities.SlaRules;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author eamrela
 */
@Stateless
public class SlaRulesFacade extends AbstractFacade<SlaRules> {

    @PersistenceContext(unitName = "com.vodafone_TE_W_TT_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SlaRulesFacade() {
        super(SlaRules.class);
    }
    
}
