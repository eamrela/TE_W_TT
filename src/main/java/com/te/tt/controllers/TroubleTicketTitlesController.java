package com.te.tt.controllers;

import com.te.tt.entities.TroubleTicketTitles;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.TroubleTicketTitlesFacade;

import java.io.Serializable;
import java.util.ArrayList;
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

@Named("troubleTicketTitlesController")
@SessionScoped
public class TroubleTicketTitlesController implements Serializable {

    @EJB
    private com.te.tt.beans.TroubleTicketTitlesFacade ejbFacade;
    private List<TroubleTicketTitles> items = null;
    private TroubleTicketTitles selected;

    public TroubleTicketTitlesController() {
    }

    public TroubleTicketTitles getSelected() {
        return selected;
    }

    public void setSelected(TroubleTicketTitles selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TroubleTicketTitlesFacade getFacade() {
        return ejbFacade;
    }

    public TroubleTicketTitles prepareCreate() {
        selected = new TroubleTicketTitles();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketTitlesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketTitlesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketTitlesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TroubleTicketTitles> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
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
    }

    public TroubleTicketTitles getTroubleTicketTitles(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TroubleTicketTitles> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TroubleTicketTitles> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TroubleTicketTitles.class)
    public static class TroubleTicketTitlesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TroubleTicketTitlesController controller = (TroubleTicketTitlesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "troubleTicketTitlesController");
            return controller.getTroubleTicketTitles(getKey(value));
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
            if (object instanceof TroubleTicketTitles) {
                TroubleTicketTitles o = (TroubleTicketTitles) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TroubleTicketTitles.class.getName()});
                return null;
            }
        }

    }

    public List<TroubleTicketTitles> autoCompleteTitle(String query){
        getItems();
        List<TroubleTicketTitles> results = new ArrayList<>();
        for (TroubleTicketTitles item : items) {
            if(item.getTitleText().toLowerCase().contains(query.toLowerCase())){
                results.add(item);
            }
        }
        if(results.isEmpty()){
            TroubleTicketTitles title = new TroubleTicketTitles();
            title.setTitleText(query);
            results.add(title);
        }
        return results;
    }
}
