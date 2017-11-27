package com.te.tt.controllers;

import com.te.tt.entities.Users;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.UsersFacade;
import com.te.tt.entities.AssignmentGroups;
import com.te.tt.entities.UserRoles;

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
import javax.inject.Inject;

@Named("usersController")
@SessionScoped
public class UsersController implements Serializable {

    @EJB
    private com.te.tt.beans.UsersFacade ejbFacade;
    private List<Users> items = null;
    private Users selected;
    private UserRoles selectedUserRole;
    private Users loggedInUser;
    
    @Inject
    private UserRolesController userRolesController;

    public Users getLoggedInUser() {
        if(loggedInUser==null){
            loggedInUser = getFacade().find("amr.el-ansary");
        }
        return loggedInUser;
    }

    
    
    public UsersController() {
    }

    public Users getSelected() {
        return selected;
    }

    public void setSelected(Users selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsersFacade getFacade() {
        return ejbFacade;
    }

    public Users prepareCreate() {
        selected = new Users();
        selectedUserRole = null;
        initializeEmbeddableKey();
        return selected;
    }

    public Users create() {
        selected = persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UsersCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsersUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UsersDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            selectedUserRole = null;
        }
    }

    public List<Users> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private Users persist(PersistAction persistAction, String successMessage) {
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

    public Users getUsers(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<Users> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Users> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public AssignmentGroups getLoggedInUserGroup() {
        if(loggedInUser!=null){
            if(loggedInUser.getAssignmentGroupsCollection()!=null){
            return (AssignmentGroups) loggedInUser.getAssignmentGroupsCollection().toArray()[0];
            }
        }
        return null;
    }

    @FacesConverter(forClass = Users.class)
    public static class UsersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsersController controller = (UsersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usersController");
            return controller.getUsers(getKey(value));
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
            if (object instanceof Users) {
                Users o = (Users) object;
                return getStringKey(o.getUserName());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Users.class.getName()});
                return null;
            }
        }

    }

    public UserRoles getSelectedUserRole() {
        return selectedUserRole;
    }

    public void setSelectedUserRole(UserRoles selectedUserRole) {
        this.selectedUserRole = selectedUserRole;
    }

    public void createUser(){
        if(selected!=null && selectedUserRole!=null){
            selected.setEnabled(Boolean.TRUE);
            selected = create();

            if(selectedUserRole.getUsersCollection()!=null){
                selectedUserRole.getUsersCollection().add(selected);
            }else{
                selectedUserRole.setUsersCollection(new ArrayList<Users>());
                selectedUserRole.getUsersCollection().add(selected);
            }
           userRolesController.setSelected(selectedUserRole);
           userRolesController.update();
        }
    }
    
    public List<Users> autoCompleteUser(String searchQry){
        getItems();
        List<Users> results = new ArrayList<>();
        searchQry = searchQry.toLowerCase();
        for (Users item : items) {
            if(item.getUserName().toLowerCase().contains(searchQry) || 
                    item.getEmailAddress().toLowerCase().contains(searchQry) || 
                        item.getFullName().toLowerCase().contains(searchQry) || 
                            item.getPhoneNumber().toLowerCase().contains(searchQry)){
                results.add(item);
            }
        }
        
        return results;
    }
}
