package com.te.tt.controllers;



import com.te.tt.beans.ConfigurationsFacade;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.entities.Configurations;
import com.te.tt.entities.ConfigurationsPK;
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

@Named("configurationsController")
@SessionScoped
public class ConfigurationsController implements Serializable {

    @EJB
    private ConfigurationsFacade ejbFacade;
    private List<Configurations> items = null;
    private Configurations selected;

    public ConfigurationsController() {
    }

    public Configurations getSelected() {
        return selected;
    }

    public void setSelected(Configurations selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
        selected.setConfigurationsPK(new ConfigurationsPK());
    }

    private ConfigurationsFacade getFacade() {
        return ejbFacade;
    }

    public Configurations prepareCreate() {
        selected = new Configurations();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Configurations> getItems() {
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

    public Configurations getConfigurations(ConfigurationsPK id) {
        return getFacade().find(id);
    }

    public List<Configurations> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Configurations> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Configurations.class)
    public static class ConfigurationsControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConfigurationsController controller = (ConfigurationsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "configurationsController");
            return controller.getConfigurations(getKey(value));
        }

        ConfigurationsPK getKey(String value) {
            ConfigurationsPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new ConfigurationsPK();
            key.setConfName(values[0]);
            key.setConfEnviroment(values[1]);
            return key;
        }

        String getStringKey(ConfigurationsPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getConfName());
            sb.append(SEPARATOR);
            sb.append(value.getConfEnviroment());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Configurations) {
                Configurations o = (Configurations) object;
                return getStringKey(o.getConfigurationsPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Configurations.class.getName()});
                return null;
            }
        }

    }

}
