package com.te.tt.controllers;

import com.te.tt.entities.SubCategories;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.SubCategoriesFacade;

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

@Named("subCategoriesController")
@SessionScoped
public class SubCategoriesController implements Serializable {

    @EJB
    private com.te.tt.beans.SubCategoriesFacade ejbFacade;
    private List<SubCategories> items = null;
    private SubCategories selected;

    public SubCategoriesController() {
    }

    public SubCategories getSelected() {
        return selected;
    }

    public void setSelected(SubCategories selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getSubCategoriesPK().setCategoryName(selected.getCategories().getCategoryName());
    }

    protected void initializeEmbeddableKey() {
        selected.setSubCategoriesPK(new com.te.tt.entities.SubCategoriesPK());
    }

    private SubCategoriesFacade getFacade() {
        return ejbFacade;
    }

    public SubCategories prepareCreate() {
        selected = new SubCategories();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SubCategoriesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SubCategoriesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SubCategoriesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SubCategories> getItems() {
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

    public SubCategories getSubCategories(com.te.tt.entities.SubCategoriesPK id) {
        return getFacade().find(id);
    }

    public List<SubCategories> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SubCategories> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SubCategories.class)
    public static class SubCategoriesControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SubCategoriesController controller = (SubCategoriesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "subCategoriesController");
            return controller.getSubCategories(getKey(value));
        }

        com.te.tt.entities.SubCategoriesPK getKey(String value) {
            com.te.tt.entities.SubCategoriesPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.te.tt.entities.SubCategoriesPK();
            key.setSubCategoryName(values[0]);
            key.setCategoryName(values[1]);
            return key;
        }

        String getStringKey(com.te.tt.entities.SubCategoriesPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getSubCategoryName());
            sb.append(SEPARATOR);
            sb.append(value.getCategoryName());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof SubCategories) {
                SubCategories o = (SubCategories) object;
                return getStringKey(o.getSubCategoriesPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SubCategories.class.getName()});
                return null;
            }
        }

    }

}
