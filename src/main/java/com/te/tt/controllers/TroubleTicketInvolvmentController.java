package com.te.tt.controllers;

import com.te.tt.entities.TroubleTicketInvolvment;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.TroubleTicketInvolvmentFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("troubleTicketInvolvmentController")
@SessionScoped
public class TroubleTicketInvolvmentController implements Serializable {

    @EJB
    private com.te.tt.beans.TroubleTicketInvolvmentFacade ejbFacade;
    private List<TroubleTicketInvolvment> items = null;
    private TroubleTicketInvolvment selected;

    public TroubleTicketInvolvmentController() {
    }

    public TroubleTicketInvolvment getSelected() {
        return selected;
    }

    public void setSelected(TroubleTicketInvolvment selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TroubleTicketInvolvmentFacade getFacade() {
        return ejbFacade;
    }

    public TroubleTicketInvolvment prepareCreate() {
        selected = new TroubleTicketInvolvment();
        initializeEmbeddableKey();
        return selected;
    }

    public TroubleTicketInvolvment create() {
        selected = persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketInvolvmentCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketInvolvmentUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketInvolvmentDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TroubleTicketInvolvment> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private TroubleTicketInvolvment persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    selected = getFacade().merge(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
                return selected;
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
        return null;
    }

    public TroubleTicketInvolvment getTroubleTicketInvolvment(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TroubleTicketInvolvment> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TroubleTicketInvolvment> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TroubleTicketInvolvment.class)
    public static class TroubleTicketInvolvmentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TroubleTicketInvolvmentController controller = (TroubleTicketInvolvmentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "troubleTicketInvolvmentController");
            return controller.getTroubleTicketInvolvment(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof TroubleTicketInvolvment) {
                TroubleTicketInvolvment o = (TroubleTicketInvolvment) object;
                return getStringKey(o.getInvolvmentId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TroubleTicketInvolvment.class.getName()});
                return null;
            }
        }

    }

}
