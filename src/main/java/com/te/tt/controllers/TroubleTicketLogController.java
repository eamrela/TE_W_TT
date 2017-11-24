package com.te.tt.controllers;

import com.te.tt.entities.TroubleTicketLog;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.TroubleTicketLogFacade;

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

@Named("troubleTicketLogController")
@SessionScoped
public class TroubleTicketLogController implements Serializable {

    @EJB
    private com.te.tt.beans.TroubleTicketLogFacade ejbFacade;
    private List<TroubleTicketLog> items = null;
    private TroubleTicketLog selected;

    public TroubleTicketLogController() {
    }

    public TroubleTicketLog getSelected() {
        return selected;
    }

    public void setSelected(TroubleTicketLog selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TroubleTicketLogFacade getFacade() {
        return ejbFacade;
    }

    public TroubleTicketLog prepareCreate() {
        selected = new TroubleTicketLog();
        initializeEmbeddableKey();
        return selected;
    }

    public TroubleTicketLog create() {
        selected = persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketLogCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketLogUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketLogDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TroubleTicketLog> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private TroubleTicketLog persist(PersistAction persistAction, String successMessage) {
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

    public TroubleTicketLog getTroubleTicketLog(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TroubleTicketLog> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TroubleTicketLog> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TroubleTicketLog.class)
    public static class TroubleTicketLogControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TroubleTicketLogController controller = (TroubleTicketLogController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "troubleTicketLogController");
            return controller.getTroubleTicketLog(getKey(value));
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
            if (object instanceof TroubleTicketLog) {
                TroubleTicketLog o = (TroubleTicketLog) object;
                return getStringKey(o.getCommentId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TroubleTicketLog.class.getName()});
                return null;
            }
        }

    }

}
