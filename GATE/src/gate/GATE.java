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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
    static final Logger log = java.util.logging.Logger.getLogger(GATE.class.getName()) ;
            
    @Override
    public void start(Stage stage) throws Exception {
        try {
//            Parent page= FXMLLoader.load(getClass().getResource("GATE.fxml"));
            AnchorPane page = (AnchorPane) FXMLLoader.load(GATE.class.getResource("GATE.fxml"));
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.setTitle("Gentic Algorithm Testing Envirionment");
            stage.show();
        } catch (Exception ex) {
            log.log(Level.SEVERE,GATE.class.getName(), ex);
            System.exit(-1);
        }
        
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
        Application.launch(args);
    }

    /**
     * This is a test method just to verify that a file can be written to from 
     * within a JavaFX Application.
     * 
     */
/*    private void testFileWrite(){
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
    }*/
}