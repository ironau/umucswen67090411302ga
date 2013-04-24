/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gate;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
            stage.setTitle("Gentic Algorithm Tool for Experimentation");
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