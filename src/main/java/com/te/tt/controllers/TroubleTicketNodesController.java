package com.te.tt.controllers;

import com.te.tt.entities.TroubleTicketNodes;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.TroubleTicketNodesFacade;

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

@Named("troubleTicketNodesController")
@SessionScoped
public class TroubleTicketNodesController implements Serializable {

    @EJB
    private com.te.tt.beans.TroubleTicketNodesFacade ejbFacade;
    private List<TroubleTicketNodes> items = null;
    private TroubleTicketNodes selected;

    public TroubleTicketNodesController() {
    }

    public TroubleTicketNodes getSelected() {
        return selected;
    }

    public void setSelected(TroubleTicketNodes selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TroubleTicketNodesFacade getFacade() {
        return ejbFacade;
    }

    public TroubleTicketNodes prepareCreate() {
        selected = new TroubleTicketNodes();
        initializeEmbeddableKey();
        return selected;
    }

    public TroubleTicketNodes create() {
        selected = persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle2").getString("TroubleTicketNodesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle2").getString("TroubleTicketNodesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle2").getString("TroubleTicketNodesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TroubleTicketNodes> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private TroubleTicketNodes persist(PersistAction persistAction, String successMessage) {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle2").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle2").getString("PersistenceErrorOccured"));
            }
        }
        return null;
    }

    public TroubleTicketNodes getTroubleTicketNodes(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TroubleTicketNodes> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TroubleTicketNodes> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TroubleTicketNodes.class)
    public static class TroubleTicketNodesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TroubleTicketNodesController controller = (TroubleTicketNodesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "troubleTicketNodesController");
            return controller.getTroubleTicketNodes(getKey(value));
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
            if (object instanceof TroubleTicketNodes) {
                TroubleTicketNodes o = (TroubleTicketNodes) object;
                return getStringKey(o.getNodeTtId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TroubleTicketNodes.class.getName()});
                return null;
            }
        }

    }

}
