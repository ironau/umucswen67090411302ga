/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gate;
/**
 *
 * @author ironau
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginAttribute;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginRegistry;
import org.java.plugin.standard.StandardPluginLocation;

public class GateController implements Initializable {

    private PluginManager pluginManager= ObjectFactory.newInstance().createManager();
    static final Logger log = java.util.logging.Logger.getLogger(GATE.class.getName()) ;
    private Map<String, Identity> publishedPlugins;
    PluginRegistry plugReg = pluginManager.getRegistry();
    ObservableList stageList;
    
    @FXML
    ChoiceBox ChromSelect;
    @FXML
    ListView AvailStages;
    @FXML
    ListView StageOrd;
    @FXML
    ListView ExperimentQueue;
    @FXML
    TextField MaxPopTxt;
    @FXML
    TextField MutRate;
    @FXML
    TextField MaxGen;
    @FXML
    TextArea AddedParams;
    @FXML
    TextArea RunningLog;
    @FXML
    LineChart CurProg;
    @FXML
    Button InitExperiment;
    @FXML
    Button StartExperiment;
    @FXML
    Button AbortExperiment;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.fine(this.getClass().getSimpleName() + ".initialize");
        assert ChromSelect != null : "fx:id=\"ChromSelect\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert AvailStages != null : "fx:id=\"AvailStages\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert StageOrd != null : "fx:id=\"StageOrd\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert ExperimentQueue != null : "fx:id=\"ExperimentQueue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert MaxPopTxt != null : "fx:id=\"MaxPopTxt\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert MutRate != null : "fx:id=\"MutRate\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert MaxGen != null : "fx:id=\"MaxGen\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert AddedParams != null : "fx:id=\"AddedParams\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert RunningLog != null : "fx:id=\"RunningLog\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert CurProg != null : "fx:id=\"CurProg\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert InitExperiment != null : "fx:id=\"InitExperiment\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert StartExperiment != null : "fx:id=\"StartExperiment\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        
        configureLogger();
        log.info ("logging configured");
        //loadStandardPlugins();
        loadPlugins();
        log.info ("loadPlugins complete");
        addPlugins();
        log.info("addplugins complete");
        configureStages();
        log.info("configureStages complete");
        configureButtons();
        log.info("configureButtons complete");
        configureGraph();
        log.info("configureGraph complete");
        configureProperties();
        log.info("configureProperties complete");
        configureStats();
        log.info("configureStats complete");
    }

    /**
     * 
     */
    private void configureButtons() {
        log.info(this.getClass().getSimpleName()+"configureButtons");
    }

    /**
     * 
     */
    private void configureStages() {
        configureExtensionPoint("jenes.stage.AbstractStage");
        configureExtensionPoint("jenes.stage.operator.Crossover");
        configureExtensionPoint("jenes.stage.operator.Mutator");
        configureExtensionPoint("jenes.stage.operator.Scaling");
        configureExtensionPoint("jenes.stage.operator.Selector");
        configureExtensionPoint("jenes.stage.operator.Crowder");
//        configureExtensionPoint("jenes.population.Fitness");
    }
    /**
     * 
     */
    private void configureExtensionPoint(String etpName) {
        log.info(this.getClass().getSimpleName()+" configureStages");
        PluginDescriptor abstractStageDescriptor = plugReg.getPluginDescriptor(etpName);
        log.fine("found the abstractStage descriptor");
        ExtensionPoint extPointAbstractStages = plugReg.getExtensionPoint(abstractStageDescriptor.getId(), etpName);
        log.fine("loaded the abstractStage extension point");
        ArrayList<String> stageGatherer = new ArrayList();
        for (Iterator it = extPointAbstractStages.getConnectedExtensions().iterator(); it.hasNext();){
            log.fine("Loading a stage plugin ");
            Extension ext = (Extension) it.next();
            PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
            log.log(Level.INFO, "Loading the stage plugin called {0}", ext.getParameter("name").valueAsString());
            stageGatherer.add(ext.getParameter("name").valueAsString());
            
            // These lines will likely have to move into the GA configuring method triggered by the Initialize Button
            ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
            try {
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        stageList = FXCollections.observableArrayList(stageGatherer);
        AvailStages.setItems(stageList); 
        log.fine("tied the available stage plugins to the list view");
        log.exiting("configureStages", this.getClass().toString());
    }

    /**
     * 
     */
    private void configureGraph() {
        log.info(this.getClass().getSimpleName()+"configureGraph");
    }

    /**
     *
     */
    private void configureStats() {
        log.info(this.getClass().getSimpleName()+"configureStats");
    }

    /**
     * 
     */
    private void configureProperties() {
        log.info(this.getClass().getSimpleName()+"configureProperties");
    }
    
        /**
     * This method configures logging for the application from the property file.
     */
    private void configureLogger() {
        try {
            LogManager logMan=LogManager.getLogManager();
            File appLogProps = new File("logging.properties");
            log.info(appLogProps.getAbsolutePath());
            FileInputStream logPropsStream= new FileInputStream(appLogProps);
            logMan.readConfiguration(logPropsStream);
        } catch (IOException ex) {
            Logger.getLogger(GATE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(GATE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
   }
    
        /**
     * This method adds the plug-ins to the running environment so they may be 
     * used later.
     */
    private void addPlugins() {
        try{
            plugReg = pluginManager.getRegistry();
            ArrayList plugDescList = new ArrayList(plugReg.getPluginDescriptors());
            Iterator iter = plugDescList.iterator();
            
            while(iter.hasNext()){
                PluginDescriptor plugDesc = (PluginDescriptor) iter.next();
                log.info("Added a Plugin "+plugDesc.getPluginClassName()+" "+plugDesc.getUniqueId()); 
                /*@TODO apply logic on which plugins to load.
                * mostlikely will have to load chromosomes first, 
                * Then Fitness Functions.
                * Then additional stages
                */
            }
        }catch(NullPointerException npe){
            log.log(Level.INFO,"NullPointer Exception ", npe);
        }
        
    }

    /**
     * This method finds plug-ins located in the ".\plugins" directory and stores
     * them into the plug-in Manager
     */
    private void loadPlugins() {
        PluginManager.PluginLocation[] locations = null;
        File pluginsDir = new File ("plugins");
        FilenameFilter pluginFilter = new FilenameFilter(){
            public boolean accept(File dir, String name){
                return (name.toLowerCase().endsWith("jar")||name.toLowerCase().endsWith("zip"));
            }
        };
        File[] plugins = pluginsDir.listFiles(pluginFilter);
        if (null == plugins){
            log.info("No Plugins to Load");
            return;
        }
        log.info("Number of plugins is"+Integer.toString(plugins.length)+" "+plugins[0].getAbsolutePath());
        locations = new PluginManager.PluginLocation[plugins.length];
        for (int i=0;i<plugins.length;i++)
        {
            try {
                locations[i]=StandardPluginLocation.create(plugins[i]);
                log.fine("Loaded plugin from file "+plugins[i].getName());
                log.fine("Location array is "+locations[i].getContextLocation());
            } catch (NullPointerException npe){
                log.log(Level.FINE,"File invalid "+plugins[i].getName(),npe);
            }catch (Exception e) {
                log.log(Level.WARNING,"Failed to loaded plugin from file "+plugins[i].getName(),e);
            }
        }
        try{
            for (PluginManager.PluginLocation loc : locations){
                log.info(loc.hashCode()+" "+loc.getContextLocation());
            }
            Map<String, Identity> publishPlugins = pluginManager.publishPlugins(locations);
            log.fine("Finished Loading plugins from "+pluginsDir.getName());
        } catch (JpfException jpfe){
            log.log(Level.WARNING,"Failed to publish plugins, Plugin Framework exception ",jpfe);
            System.exit(-1);
        } catch (NullPointerException npe){
            log.log(Level.WARNING,"Failed to publish plugins, null pointer: ",npe);
            System.exit(-1);
           
        }
               
    }

}