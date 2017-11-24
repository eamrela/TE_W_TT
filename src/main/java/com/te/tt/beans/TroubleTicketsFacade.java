/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.te.tt.beans;

import com.te.tt.entities.TroubleTickets;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author eamrela
 */
@Stateless
public class TroubleTicketsFacade extends AbstractFacade<TroubleTickets> {

    @PersistenceContext(unitName = "com.vodafone_TE_W_TT_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TroubleTicketsFacade() {
        super(TroubleTickets.class);
    }

    public TroubleTickets merge(TroubleTickets selected) {
        return em.merge(selected);
    }
    
}
