package com.te.tt.controllers;



import com.te.tt.beans.UserRolesFacade;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.entities.UserRoles;
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

@Named("userRolesController")
@SessionScoped
public class UserRolesController implements Serializable {

    @EJB
    private UserRolesFacade ejbFacade;
    private List<UserRoles> items = null;
    private UserRoles selected;

    public UserRolesController() {
    }

    public UserRoles getSelected() {
        return selected;
    }

    public void setSelected(UserRoles selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserRolesFacade getFacade() {
        return ejbFacade;
    }

    public UserRoles prepareCreate() {
        selected = new UserRoles();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, "UserRolesCreated");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE,"UserRolesUpdated");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "UserRolesDeleted");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<UserRoles> getItems() {
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

    public UserRoles getUserRoles(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<UserRoles> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<UserRoles> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = UserRoles.class)
    public static class UserRolesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserRolesController controller = (UserRolesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userRolesController");
            return controller.getUserRoles(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserRoles) {
                UserRoles o = (UserRoles) object;
                return getStringKey(o.getRoleName());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), UserRoles.class.getName()});
                return null;
            }
        }

    }

}
