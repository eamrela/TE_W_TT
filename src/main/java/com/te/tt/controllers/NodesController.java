package com.te.tt.controllers;

import com.te.tt.entities.Nodes;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.NodesFacade;

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

@Named("nodesController")
@SessionScoped
public class NodesController implements Serializable {

    @EJB
    private com.te.tt.beans.NodesFacade ejbFacade;
    private List<Nodes> items = null;
    private Nodes selected;

    public NodesController() {
    }

    public Nodes getSelected() {
        return selected;
    }

    public void setSelected(Nodes selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getNodesPK().setNodeDomain(selected.getDomains().getDomainName());
    }

    protected void initializeEmbeddableKey() {
        selected.setNodesPK(new com.te.tt.entities.NodesPK());
    }

    private NodesFacade getFacade() {
        return ejbFacade;
    }

    public Nodes prepareCreate() {
        selected = new Nodes();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("NodesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("NodesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("NodesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Nodes> getItems() {
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

    public Nodes getNodes(com.te.tt.entities.NodesPK id) {
        return getFacade().find(id);
    }

    public List<Nodes> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Nodes> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter("NodesControllerConverter")
    public static class NodesControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            NodesController controller = (NodesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "nodesController");
            return controller.getNodes(getKey(value));
        }

        com.te.tt.entities.NodesPK getKey(String value) {
            com.te.tt.entities.NodesPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.te.tt.entities.NodesPK();
            key.setNodeName(values[0]);
            key.setNodeDomain(values[1]);
            return key;
        }

        String getStringKey(com.te.tt.entities.NodesPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getNodeName());
            sb.append(SEPARATOR);
            sb.append(value.getNodeDomain());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Nodes) {
                Nodes o = (Nodes) object;
                return getStringKey(o.getNodesPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Nodes.class.getName()});
                return null;
            }
        }

    }

    public List<Nodes> autoCompleteNodes(String query){
        getItems();
        List<Nodes> results = new ArrayList<>();
        for (Nodes item : items) {
            if(item.getNodesPK().getNodeName().toLowerCase().contains(query.toLowerCase())
                    || item.getNodesPK().getNodeDomain().toLowerCase().contains(query.toLowerCase()) 
                    || item.getNodeType().toLowerCase().contains(query.toLowerCase())
                ){
                results.add(item);
            }
        }
        return results;
    }
}
