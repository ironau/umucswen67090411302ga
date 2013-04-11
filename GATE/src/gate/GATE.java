/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginRegistry;
import org.java.plugin.standard.StandardPluginLocation;

/**
 *
 * @author ironau
 */
public class GATE extends Application {
    private PluginManager pluginManager= ObjectFactory.newInstance().createManager();
    static final Logger log = java.util.logging.Logger.getLogger(GATE.class.getName()) ;
    private Map<String, Identity> publishedPlugins;
    PluginRegistry plugReg = pluginManager.getRegistry();
            
    @Override
    public void start(Stage stage) throws Exception {
        testFileWrite();
        configureLogger();
        log.fine("Logging enabled");
        
        //loadStandardPlugins();
        loadPlugins();
        log.fine("loadPlugins complete");
        addPlugins();
        log.fine("addplugins complete");
        
        Parent root = FXMLLoader.load(getClass().getResource("GATE.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
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
        PluginLocation[] locations = null;
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
        locations = new PluginLocation[plugins.length];
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
            for (PluginLocation loc : locations){
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
     * This is a test method just to verify that a file can be written to from 
     * within a JavaFX Application.
     * 
     */
    private void testFileWrite(){
        FileOutputStream testOut = null;
        try {
            File test=new File("TestOutput.log");
            log.fine(test.getAbsoluteFile().toString());
            testOut = new FileOutputStream(test,true);
            testOut.write("Can I write to a file from the FXML App???".getBytes());
            testOut.write(Long.toString(System.currentTimeMillis()).getBytes());
            testOut.write("\n".getBytes());
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(GATE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                java.util.logging.Logger.getLogger(GATE.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                testOut.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(GATE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

/*    private void loadStandardPlugins() {
        PluginLocation[] locations = null;
        File pluginsDir = new File ("plugins");
        FilenameFilter pluginFilter = new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.toLowerCase().endsWith("zip");
            }
        };
        File[] plugins = pluginsDir.listFiles(pluginFilter);
        if (null == plugins){
            log.info("No Plugins to Load");
            return;
        }
        log.info("Number of plugins is"+Integer.toString(plugins.length)+" "+plugins[0].getAbsolutePath());
        locations = new PluginLocation[plugins.length];
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
            log.fine("Plugin Manager: "+pluginManager.toString()); 
            log.fine("Plugin Location Array: "+locations.toString());
            log.info("number of locations found is: "+Integer.toString(locations.length));
            for (PluginLocation loc : locations){
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
    }*/
}
