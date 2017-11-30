package com.te.tt.controllers;

import com.te.tt.entities.TroubleTickets;
import com.te.tt.controllers.util.JsfUtil;
import com.te.tt.controllers.util.JsfUtil.PersistAction;
import com.te.tt.beans.TroubleTicketsFacade;
import com.te.tt.entities.AssignmentGroups;
import com.te.tt.entities.Configurations;
import com.te.tt.entities.ConfigurationsPK;
import com.te.tt.entities.Nodes;
import com.te.tt.entities.TroubleTicketAttachments;
import com.te.tt.entities.TroubleTicketInvolvment;
import com.te.tt.entities.TroubleTicketLog;
import com.te.tt.entities.TroubleTicketNodes;
import com.te.tt.entities.TroubleTicketStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.apache.commons.io.IOUtils;

@Named("troubleTicketsController")
@SessionScoped
public class TroubleTicketsController implements Serializable {

    @EJB
    private com.te.tt.beans.TroubleTicketsFacade ejbFacade;
    private List<TroubleTickets> items = null;
    private List<TroubleTickets> searchResult = null;
    private TroubleTickets selected;
    private TroubleTickets selectedTT;
    private TroubleTickets searchAbleTT;
    private Date searchFrom;
    private Date searchTo;
    private List<Nodes> selectedNodes = null;
    private List<UploadedFile> selectedAttachments = null;
    private Configurations conf = null;
    
    @Inject
    private TroubleTicketStatusController troubleTicketStatusController;
    @Inject
    private UsersController usersController;
    @Inject
    private NodesController nodesController;
    @Inject
    private ConfigurationsController configurationController;
    @Inject
    private TroubleTicketAttachmentsController troubleTicketAttachmentController;
    @Inject
    private TroubleTicketInvolvmentController troubleTicketInvolvmentController;
    @Inject
    private TroubleTicketNodesController troubleTicketNodesController;
    @Inject
    private TroubleTicketLogController troubleTicketLogController;
    
    private AssignmentGroups selectedTTEdit_AssignmentGroup;
    private String selectedTTEdit_resolutionAction;
    private String selectedTTEdit_reopenComment;
    private String selectedTTEdit_rejectionReason;
    private boolean selectedTTEdit_returnToInititaor;
    private TroubleTicketStatus selectedTTEdit_Status;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

    public TroubleTickets getSearchAbleTT() {
        return searchAbleTT;
    }

    public void setSearchAbleTT(TroubleTickets searchAbleTT) {
        this.searchAbleTT = searchAbleTT;
    }

    public Date getSearchFrom() {
        return searchFrom;
    }

    public Date getSearchTo() {
        return searchTo;
    }

    public void setSearchFrom(Date searchFrom) {
        this.searchFrom = searchFrom;
    }

    public void setSearchTo(Date searchTo) {
        this.searchTo = searchTo;
    }
    
    

    public void prepareSearch(){
        searchAbleTT = new TroubleTickets();
    }
    public boolean isContextMenuChangeStatusDisabled() {
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Cancelled") ||
                    selectedTT.getTtStatus().getStatusName().equals("Closed")){
                return true;
            }
        }
        return false;
    }
   
    public boolean isContextMenuRejectDisabled() {
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Queued") || 
                    selectedTT.getTtStatus().getStatusName().equals("Assigned") ||
                      selectedTT.getTtStatus().getStatusName().equals("Pending")){
                return false;
            }
        }
        return true;
    }
    
    public boolean isContextMenuStartWorkingDisabled() {
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Cancelled") ||
                    selectedTT.getTtStatus().getStatusName().equals("Closed") || 
                     selectedTT.getTtStatus().getStatusName().equals("Resolved")){
                return true;
            }
        }
        return false;
    }
    
    public boolean isContextMenuReopenDisabled() {
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Resolved")){
                return false;
            }
        }
        return true;
    }

    public boolean isContextMenuReassignDisabled() {
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Cancelled") ||
                    selectedTT.getTtStatus().getStatusName().equals("Closed")){
                return true;
            }
        }
        return false;
    }

    public boolean isContextMenuResolveDisabled() {
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Cancelled") ||
                    selectedTT.getTtStatus().getStatusName().equals("Closed") || 
                    selectedTT.getTtStatus().getStatusName().equals("Resolved")){
                return true;
            }
        }
        return false;
    }
    
    public boolean isContextMenuCloseDisabled(){
        if(selectedTT!=null){
            if(selectedTT.getTtStatus().getStatusName().equals("Resolved")){
                return false;
            }
        }
        return true;
    }
    
    public TroubleTicketStatus getSelectedTTEdit_Status() {
        return selectedTTEdit_Status;
    }

    public void setSelectedTTEdit_Status(TroubleTicketStatus selectedTTEdit_Status) {
        this.selectedTTEdit_Status = selectedTTEdit_Status;
    }
    
    

    public boolean isSelectedTTEdit_returnToInititaor() {
        return selectedTTEdit_returnToInititaor;
    }

    public void setSelectedTTEdit_returnToInititaor(boolean selectedTTEdit_returnToInititaor) {
        this.selectedTTEdit_returnToInititaor = selectedTTEdit_returnToInititaor;
    }
    
    
    
    public String getSelectedTTEdit_resolutionAction() {
        return selectedTTEdit_resolutionAction;
    }

    public String getSelectedTTEdit_rejectionReason() {
        return selectedTTEdit_rejectionReason;
    }

    public void setSelectedTTEdit_rejectionReason(String selectedTTEdit_rejectionReason) {
        this.selectedTTEdit_rejectionReason = selectedTTEdit_rejectionReason;
    }

    
    public String getSelectedTTEdit_reopenComment() {
        return selectedTTEdit_reopenComment;
    }

    public void setSelectedTTEdit_reopenComment(String selectedTTEdit_reopenComment) {
        this.selectedTTEdit_reopenComment = selectedTTEdit_reopenComment;
    }
    
    

    public void setSelectedTTEdit_resolutionAction(String selectedTTEdit_resolutionAction) {
        this.selectedTTEdit_resolutionAction = selectedTTEdit_resolutionAction;
    }

    
    public AssignmentGroups getSelectedTTEdit_AssignmentGroup() {
        return selectedTTEdit_AssignmentGroup;
    }

    public void setSelectedTTEdit_AssignmentGroup(AssignmentGroups selectedTTEdit_AssignmentGroup) {
        this.selectedTTEdit_AssignmentGroup = selectedTTEdit_AssignmentGroup;
    }
    
    

    public TroubleTickets getSelectedTT() {
        return selectedTT;
    }

    public void setSelectedTT(TroubleTickets selectedTT) {
        this.selectedTT = selectedTT;
        troubleTicketLogController.prepareCreate();
        troubleTicketLogController.getSelected().setCommentBy(usersController.getLoggedInUser());
        troubleTicketLogController.getSelected().setTtId(selectedTT);
    }

    
    public TroubleTicketsController() {
    }

    public TroubleTickets getSelected() {
        return selected;
    }

    public void setSelected(TroubleTickets selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TroubleTicketsFacade getFacade() {
        return ejbFacade;
    }

    public TroubleTickets prepareCreate() {
        selected = new TroubleTickets();
        initializeEmbeddableKey();
        selected.setTtCreationDate(new Date());
        selected.setTtLastModificationDate(new Date());
        selected.setTtStatus(troubleTicketStatusController.getTroubleTicketStatus("Assigned"));
        selected.setTtCreator(usersController.getLoggedInUser());
        selected.setTtInitatorGroup(usersController.getLoggedInUserGroup());
        
        selectedNodes = new ArrayList<>();
        selectedAttachments = new ArrayList<>();
        
        conf = configurationController.getConfigurations(new ConfigurationsPK("UPLOAD_PATH", "PROD"));
        
        return selected;
    }

    public TroubleTickets create() {
        selected = persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketsUpdated"));
    }
    
     public void updateEdit() {
        selected = selectedTT;
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TroubleTicketsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TroubleTickets> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private TroubleTickets persist(PersistAction persistAction, String successMessage) {
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

    public TroubleTickets getTroubleTickets(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TroubleTickets> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TroubleTickets> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TroubleTickets.class)
    public static class TroubleTicketsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TroubleTicketsController controller = (TroubleTicketsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "troubleTicketsController");
            return controller.getTroubleTickets(getKey(value));
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
            if (object instanceof TroubleTickets) {
                TroubleTickets o = (TroubleTickets) object;
                return getStringKey(o.getTtId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TroubleTickets.class.getName()});
                return null;
            }
        }

    }

    public void addNode(){
        if(selected!=null && nodesController.getSelected()!=null){
            selectedNodes.add(nodesController.getSelected());
            nodesController.setSelected(null);
        }
    }

    public void removeNode(Nodes node){
        if(selected!=null && selectedNodes!=null){
            selectedNodes.remove(node);
        }
    }
    
    public void addAttachment(FileUploadEvent event){
           if(selected!=null && selectedAttachments!=null){
            if(event.getFile() != null) {
               selectedAttachments.add(event.getFile());
           }
           }
    }
    
    public void removeAttachment(UploadedFile file){
        if(selected!=null && selectedAttachments!=null){
        selectedAttachments.remove(file);
        }
    }
    
    public List<Nodes> getSelectedNodes() {
        return selectedNodes;
    }

    public List<UploadedFile> getSelectedAttachments() {
        return selectedAttachments;
    }
    
    public void save(UploadedFile file) {

    
    if(conf!=null && selected!=null){
        try {
            String uploadPath = conf.getConfValue();
            new File(uploadPath+"/"+selected.getTtId()).mkdirs();
            File f = new File(uploadPath+"/"+selected.getTtId()+"/"+file.getFileName());
            OutputStream outputStream;
            outputStream = new FileOutputStream(f);
            IOUtils.copy(file.getInputstream(), outputStream);
            outputStream.close();
            
            troubleTicketAttachmentController.prepareCreate();
            troubleTicketAttachmentController.getSelected().setTtId(selected);
            troubleTicketAttachmentController.getSelected().setFileLocation(f.getAbsolutePath());
            troubleTicketAttachmentController.getSelected().setFileName(f.getName());
            troubleTicketAttachmentController.getSelected().setUploadedBy(usersController.getLoggedInUser());
            troubleTicketAttachmentController.getSelected().setUploadTime(new Date());
                    
                    
            if(selected.getTroubleTicketAttachmentsCollection()!=null){
                selected.getTroubleTicketAttachmentsCollection().add(troubleTicketAttachmentController.create());
            }else{
                selected.setTroubleTicketAttachmentsCollection(new ArrayList<TroubleTicketAttachments>());
                selected.getTroubleTicketAttachmentsCollection().add(troubleTicketAttachmentController.create());
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TroubleTicketsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TroubleTicketsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    }
    
    public void createTT(){
        if(selected!=null){  
        selected = create();
        //<editor-fold defaultstate="collapsed" desc="Nodes">
            for (Nodes selectedNode : selectedNodes) {
                troubleTicketNodesController.prepareCreate();
                troubleTicketNodesController.getSelected().setTtId(selected);
                troubleTicketNodesController.getSelected().setNodes(selectedNode);
                if(selected.getTroubleTicketNodesCollection()!=null){
                    selected.getTroubleTicketNodesCollection().add(troubleTicketNodesController.create());
                }else{
                    selected.setTroubleTicketNodesCollection(new ArrayList<TroubleTicketNodes>());
                    selected.getTroubleTicketNodesCollection().add(troubleTicketNodesController.create());
                }
            }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Attachments">
        for (UploadedFile file : selectedAttachments) {
            save(file);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Involvments">
        troubleTicketInvolvmentController.prepareCreate();
        troubleTicketInvolvmentController.getSelected().setTtId(selected);
        troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
        troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
        troubleTicketInvolvmentController.getSelected().setInvolvmentType("Creation");
        troubleTicketInvolvmentController.getSelected().setCurrentGroup(selected.getTtAssignmentGroup());
        troubleTicketInvolvmentController.getSelected().setPreviousGroup(selected.getTtAssignmentGroup());
        if(selected.getTroubleTicketInvolvmentCollection()!=null){
            selected.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
        }else{
            selected.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
            selected.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
        }
        //</editor-fold>
        update();
        
        selectedAttachments = null;
        selectedNodes = null;
        
        //<editor-fold defaultstate="collapsed" desc="Navigate">
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/TE_W_TT/app/service_desk/incident_queue.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(TroubleTicketsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        //</editor-fold>
        }
    }

    public void downloadAttachment(TroubleTicketAttachments item){
            try {

                System.out.println("Downloading Attachment");
                File FB = new File(item.getFileLocation());
                FacesContext facesContext = FacesContext.getCurrentInstance();
                ExternalContext externalContext =  facesContext.getExternalContext();
                externalContext.responseReset();
                externalContext.setResponseContentType("application/csv");
                externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\""+FB.getName()+""+"\"");
                FileInputStream fin = new FileInputStream(FB);
                OutputStream output = externalContext.getResponseOutputStream();
                byte[] data;
                data = new byte[fin.available()];
                fin.read(data);
                output.write(data);
                output.flush();
                output.close();
                facesContext.responseComplete();
            } catch (IOException ex) {
                Logger.getLogger(TroubleTicketsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    public void addComment(){
        if(selectedTT!=null && troubleTicketLogController.getSelected()!=null){
        
        troubleTicketLogController.getSelected().setCommentTime(new Date());
        
       if(selectedTT.getTroubleTicketLogCollection()!=null){
            selectedTT.getTroubleTicketLogCollection().add(troubleTicketLogController.create());
        }else{
            selectedTT.setTroubleTicketLogCollection(new ArrayList<TroubleTicketLog>());
            selectedTT.getTroubleTicketLogCollection().add(troubleTicketLogController.create());
        } 
        updateEdit();
        troubleTicketLogController.prepareCreate();
        }
    }
    
    public void setAssignmentToInititaor(){
        if(selectedTT!=null){
            if(selectedTTEdit_returnToInititaor){
            selectedTTEdit_AssignmentGroup = selectedTT.getTtInitatorGroup();
            }else{
            selectedTTEdit_AssignmentGroup = selectedTT.getTtAssignmentGroup();
            }
        }
    }
    
    public void prepareEdit(){
        if(selectedTT!=null){
            selectedTTEdit_AssignmentGroup = null;
            selectedTTEdit_resolutionAction = null;
            selectedTTEdit_rejectionReason = null;
            selectedTTEdit_reopenComment = null;
            selectedTTEdit_returnToInititaor = true;
            selectedTTEdit_Status = null;
          
            selectedTTEdit_AssignmentGroup = selectedTT.getTtAssignmentGroup();
            setAssignmentToInititaor();
            selectedTTEdit_resolutionAction = selectedTT.getResolutionAction();
            selectedTTEdit_rejectionReason = selectedTT.getRejectionReason();
            selectedTTEdit_Status = selectedTT.getTtStatus();
        }
    }
    public void updateReassign(){
        if(selectedTT!=null && selectedTTEdit_AssignmentGroup!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Reassign");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTTEdit_AssignmentGroup);
            
            selectedTT.setTtAssignmentGroup(selectedTTEdit_AssignmentGroup);
        
        if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
        }else{
            selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
        }
           
          updateEdit();
        }
    }
    public void updateResolve(){
        if(selectedTT!=null && selectedTTEdit_resolutionAction!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Resolution");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTTEdit_AssignmentGroup);
            
            selectedTT.setResolutionAction(selectedTTEdit_resolutionAction);
            selectedTT.setTtAssignmentGroup(selectedTTEdit_AssignmentGroup);
            selectedTT.setTtStatus(troubleTicketStatusController.getTroubleTicketStatus("Resolved"));
            selectedTT.setResolvedBy(usersController.getLoggedInUser());
            
            if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }else{
                selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
                selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }
           
          updateEdit();
        }
    }
    public void updateReject(){
        if(selectedTT!=null && selectedTTEdit_rejectionReason!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Rejection");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTTEdit_AssignmentGroup);
            
            selectedTT.setRejectionReason(selectedTTEdit_rejectionReason);
            selectedTT.setTtAssignmentGroup(selectedTTEdit_AssignmentGroup);
            selectedTT.setTtStatus(troubleTicketStatusController.getTroubleTicketStatus("Rejected"));
            selectedTT.setRejectedBy(usersController.getLoggedInUser());
            selectedTT.setRejectionTime(new Date());
            
            if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }else{
                selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
                selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }
           
          updateEdit();
        }
    }
    public void updateStatus(){
      if(selectedTT!=null && selectedTTEdit_Status!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Status/Assignment Change");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTTEdit_AssignmentGroup);
            
            selectedTT.setTtAssignmentGroup(selectedTTEdit_AssignmentGroup);
            selectedTT.setTtStatus(selectedTTEdit_Status);
            
            if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }else{
                selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
                selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }
              updateEdit();
      }
    }
    public void updateClose(){
        if(selectedTT!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Closure");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTT.getTtAssignmentGroup());
            
            selectedTT.setTtStatus(troubleTicketStatusController.getTroubleTicketStatus("Closed"));
            selectedTT.setClosedBy(usersController.getLoggedInUser());
            selectedTT.setCloseTime(new Date());
            
            if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }else{
                selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
                selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }
           
          updateEdit();
        }
    }
    
    public void startWorking(){
        if(selectedTT!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Status/Assignment Change");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTT.getTtAssignmentGroup());
            
            selectedTT.setTtStatus(troubleTicketStatusController.getTroubleTicketStatus("Work In progress"));
            
            if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }else{
                selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
                selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }
              updateEdit();
      }
    }
    
    public void reopen(){
        if(selectedTT!=null){
            troubleTicketInvolvmentController.prepareCreate();
            troubleTicketInvolvmentController.getSelected().setTtId(selectedTT);
            troubleTicketInvolvmentController.getSelected().setInvolvmentBy(usersController.getLoggedInUser());
            troubleTicketInvolvmentController.getSelected().setInvolvmentTime(new Date());
            troubleTicketInvolvmentController.getSelected().setInvolvmentType("Status/Assignment Change");
            troubleTicketInvolvmentController.getSelected().setPreviousGroup(selectedTT.getTtAssignmentGroup());
            troubleTicketInvolvmentController.getSelected().setCurrentGroup(selectedTTEdit_AssignmentGroup);
            
            troubleTicketLogController.prepareCreate();
            troubleTicketLogController.getSelected().setCommentBy(usersController.getLoggedInUser());
            troubleTicketLogController.getSelected().setCommentText(selectedTTEdit_reopenComment);
            troubleTicketLogController.getSelected().setTtId(selectedTT);
            troubleTicketLogController.getSelected().setCommentTime(new Date());
        
            if(selectedTT.getTroubleTicketLogCollection()!=null){
                 selectedTT.getTroubleTicketLogCollection().add(troubleTicketLogController.create());
             }else{
                 selectedTT.setTroubleTicketLogCollection(new ArrayList<TroubleTicketLog>());
                 selectedTT.getTroubleTicketLogCollection().add(troubleTicketLogController.create());
             } 
            
            selectedTT.setTtAssignmentGroup(selectedTTEdit_AssignmentGroup);
            selectedTT.setTtStatus(troubleTicketStatusController.getTroubleTicketStatus("Assigned"));
            
            if(selectedTT.getTroubleTicketInvolvmentCollection()!=null){
            selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }else{
                selectedTT.setTroubleTicketInvolvmentCollection(new ArrayList<TroubleTicketInvolvment>());
                selectedTT.getTroubleTicketInvolvmentCollection().add(troubleTicketInvolvmentController.create());
            }
              updateEdit();
      }
    }

    public List<TroubleTickets> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(List<TroubleTickets> searchResult) {
        this.searchResult = searchResult;
    }
    
    public void searchTT(){
        String query = " select * from trouble_tickets ";
        boolean where_added=false;
        if(searchAbleTT.getTtId()!=null){
            query += " where  tt_id = "+searchAbleTT.getTtId()+" ";
            where_added=true;
        }
        if(searchAbleTT.getTtTitle()!=null){
            if(where_added){
            query += " and tt_title like '%"+searchAbleTT.getTtTitle()+"%'";
            }else{
            query += " where  tt_title like '%"+searchAbleTT.getTtTitle()+"%'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtDescription()!=null){
            if(where_added){
            query += " and tt_description like '%"+searchAbleTT.getTtDescription()+"%'";
            }else{
            query += " where  tt_description like '%"+searchAbleTT.getTtDescription()+"%'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtDomain()!=null){
            if(where_added){
            query += " and tt_domain = '"+searchAbleTT.getTtDomain().getDomainName()+"'";
            }else{
            query += " where  tt_domain = '"+searchAbleTT.getTtDomain().getDomainName()+"'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtCategory()!=null){
            if(where_added){
            query += " and tt_category = '"+searchAbleTT.getTtCategory().getCategoryName()+"'";
            }else{
            query += " where  tt_category = '"+searchAbleTT.getTtCategory().getCategoryName()+"'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtSubcategory()!=null){
            if(where_added){
            query += " and tt_subcategory = '"+searchAbleTT.getTtSubcategory()+"'";
            }else{
            query += " where  tt_subcategory = '"+searchAbleTT.getTtSubcategory()+"'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtStatus()!=null){
            if(where_added){
            query += " and tt_status = '"+searchAbleTT.getTtStatus().getStatusName()+"'";
            }else{
            query += " where  tt_status = '"+searchAbleTT.getTtStatus().getStatusName()+"'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtPriority()!=null){
            if(where_added){
            query += " and tt_priority = '"+searchAbleTT.getTtPriority().getPriorityName()+"'";
            }else{
            query += " where  tt_priority = '"+searchAbleTT.getTtPriority().getPriorityName()+"'";
            where_added=true;
            }
        }
        if(searchAbleTT.getTtAssignmentGroup()!=null){
            if(where_added){
            query += " and tt_assignment_group = '"+searchAbleTT.getTtAssignmentGroup().getGroupName()+"'";
            }else{
            query += " where  tt_assignment_group = '"+searchAbleTT.getTtAssignmentGroup().getGroupName()+"'";
            where_added=true;
            }
        }
        if(searchFrom!=null){
            if(where_added){
            query += " and tt_creation_date >= '"+sdf.format(searchFrom)+"' ";
            }else{
            query += " where  tt_creation_date >= '"+sdf.format(searchFrom)+"'";
            where_added=true;
            }
        }
        if(searchTo!=null){
            if(where_added){
            query += " and tt_creation_date <= '"+sdf.format(searchTo)+"' ";
            }else{
            query += " where  tt_creation_date <= '"+sdf.format(searchTo)+"'";
            where_added=true;
            }
        }
        System.out.println(query);
        
        searchResult = getFacade().executeSearch(query);
    }
    
}
