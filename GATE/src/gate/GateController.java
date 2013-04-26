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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;
import jenes.AlgorithmEventListener;
import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Fitness;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics;
import jenes.stage.AbstractStage;
import jenes.utils.CSVLogger;
//import jenes.utils.XLSLogger;
import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginRegistry;
import org.java.plugin.standard.StandardPluginLocation;

public class GateController implements Initializable, GenerationEventListener,AlgorithmEventListener{

    private PluginManager pluginManager= ObjectFactory.newInstance().createManager();
    private SimpleDateFormat runningLogDateFormat = new SimpleDateFormat("ddMMMyyy-HHmmss.SSS");
    static final Logger log = java.util.logging.Logger.getLogger(GATE.class.getName()) ;
    private Map<String, Identity> publishedPlugins;
    private PluginRegistry plugReg = pluginManager.getRegistry();
    private ObservableList stageList;
    private ArrayList<String> selectedStageList = new ArrayList();
    private ArrayList<String> experimentQueueList = new ArrayList();
    private XYChart.Series chartDataMax = new XYChart.Series();
    private XYChart.Series chartDataMean = new XYChart.Series();
    private XYChart.Series chartDataMin = new XYChart.Series();
    private ObservableList chromList;
    private ObservableList fitnessFunctionList;
    private Population.Statistics stats;
    private HashMap fitnessFunctions = new HashMap<String,PluginDescriptor>();
    private HashMap genericStages = new HashMap<String,PluginDescriptor>();
    private HashMap chromosomeTypes = new HashMap<String,PluginDescriptor>();
    private HashMap experiments = new HashMap<String,GeneticAlgorithm>();
//    private XLSLogger resultsXSLLogger= null;
    private CSVLogger resultsCSVLogger= null;
    String resultsLoggerSchema[] = {"startTime","generation","randomSeed","maxValue","minValue","averageValue"};
    long maxPopSize;
    float mutationRate;
    long maxGenerations;
    GeneticAlgorithm runningAlgorithm=null;
    
    
    
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
    @FXML
    ChoiceBox SelectedFitnessFunction;
    @FXML
    Label MessageBar;
    @FXML
    Slider JeneSize;
    @FXML
    CheckBox AutoRun;
    
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
        configureStats();
        log.info("configureStats complete");
        configureOrdering();
        log.info("configureOrdering complete");
        configureExperimentQueue();
        log.info("configureExperimentQueue complete");
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
        ArrayList gaStages = new ArrayList();
        gaStages.addAll(configureExtensionPoint("jenes.stage.AbstractStage",genericStages));
        gaStages.addAll(configureExtensionPoint("jenes.stage.operator.Crossover",genericStages));
        gaStages.addAll(configureExtensionPoint("jenes.stage.operator.Mutator",genericStages));
        gaStages.addAll(configureExtensionPoint("jenes.stage.operator.Scaling",genericStages));
        gaStages.addAll(configureExtensionPoint("jenes.stage.operator.Selector",genericStages));
        gaStages.addAll(configureExtensionPoint("jenes.stage.operator.Crowder",genericStages));
        stageList = FXCollections.observableArrayList(gaStages);
        AvailStages.setItems(stageList); 
        log.finer("tied the available stage plugins to the list view");
        log.exiting("configureStages", this.getClass().toString());

        ArrayList gaChromosomes = new ArrayList();
        gaChromosomes.addAll(configureExtensionPoint("jenes.chromosome.Chromosome",chromosomeTypes));
        chromList = FXCollections.observableArrayList(gaChromosomes);
        ChromSelect.setItems(chromList); 
        log.finer("tied the available chromosome plugins to the list view");
        log.exiting("configureStages", this.getClass().toString());

        //fitnessFunctionList
        //gaStages.addAll(configureExtensionPoint("jenes.population.Fitness"));
        ArrayList gaFitnessFunctions = new ArrayList();
        gaFitnessFunctions.addAll(configureExtensionPoint("jenes.population.Fitness",fitnessFunctions));
        fitnessFunctionList = FXCollections.observableArrayList(gaFitnessFunctions);
        SelectedFitnessFunction.setItems(fitnessFunctionList); 
        log.finer("tied the available Fitness Function plugins to the list view");

        log.exiting("configureStages", this.getClass().toString());
    }
    /**
     * 
     */
    private ArrayList configureExtensionPoint(String etpName,HashMap<String,PluginDescriptor> map) {
        log.info(this.getClass().getSimpleName()+" configureStages");
        AvailStages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        PluginDescriptor abstractStageDescriptor = plugReg.getPluginDescriptor(etpName);
        log.fine("found the abstractStage descriptor for "+etpName);
        ExtensionPoint extPointAbstractStages = plugReg.getExtensionPoint(abstractStageDescriptor.getId(), etpName);
        log.fine("loaded the extension point");
        ArrayList<String> stageGatherer = new ArrayList();
        for (Iterator it = extPointAbstractStages.getConnectedExtensions().iterator(); it.hasNext();){
            log.fine("Loading a stage plugin ");
            Extension ext = (Extension) it.next();
            PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
            log.log(Level.INFO, "Loading the stage plugin called {0}", ext.getParameter("name").valueAsString());
            stageGatherer.add(ext.getParameter("name").valueAsString());
            map.put(ext.getParameter("name").valueAsString(),descr);
            // These lines will likely have to move into the GA configuring method triggered by the Initialize Button
            ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
            try {
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return stageGatherer;
    }

    /**
     * 
     */
    private void configureGraph() {
        log.info(this.getClass().getSimpleName()+"configureGraph");
        setupChart();
      
/*        int oldMaxValue=10;
        int oldMeanValue=4;
        int oldMinValue =2;
        for(int gen=1;gen<500;gen++){
            Random ranGen = Random.getInstance();
            int maxValue = Math.min(ranGen.nextInt(oldMaxValue,oldMaxValue+10),5000);
            int meanValue = ranGen.nextInt(oldMeanValue-1, maxValue);
            int minValue =ranGen.nextInt(oldMinValue-1, meanValue);
            chartDataMax.getData().add(new XYChart.Data(gen,maxValue));
            chartDataMean.getData().add(new XYChart.Data(gen,meanValue));
            chartDataMin.getData().add(new XYChart.Data(gen,minValue));
            oldMaxValue=maxValue;
            oldMeanValue=Math.max(meanValue,4);
            oldMinValue=Math.max(minValue,2);
            
        }
        */ 
    }
    /**
     * 
     */
    private void configureOrdering() {
        log.info(this.getClass().getSimpleName()+"configureOrdering");
        StageOrd.setItems(FXCollections.observableArrayList(selectedStageList));
    }

    /**
     * 
     */
    private void configureExperimentQueue() {
       log.info(this.getClass().getSimpleName()+"configureExperimentQueue");
       ExperimentQueue.setItems(FXCollections.observableArrayList(experimentQueueList));
       ExperimentQueue.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    /**
     *
     */
    private void configureStats() {
        log.info(this.getClass().getSimpleName()+"configureStats");
        
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
        try {
            //Setup the XLS data logger
//            resultsXSLLogger = new XLSLogger(resultsLoggerSchema,"AllExperimentsData.xls");
            resultsCSVLogger= new CSVLogger(resultsLoggerSchema,"AllExperimentsData.csv");
        } catch (IOException ex) {
            Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
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
                log.finer("Location array is "+locations[i].getContextLocation());
            } catch (NullPointerException npe){
                log.log(Level.FINE,"File invalid "+plugins[i].getName(),npe);
            }catch (Exception e) {
                log.log(Level.WARNING,"Failed to loaded plugin from file "+plugins[i].getName(),e);
            }
        }
        try{
            for (PluginManager.PluginLocation loc : locations){
                log.finer(loc.hashCode()+" "+loc.getContextLocation());
            }
            Map<String, Identity> publishPlugins = pluginManager.publishPlugins(locations);
            log.finer("Finished Loading plugins from "+pluginsDir.getName());
        } catch (JpfException jpfe){
            log.log(Level.WARNING,"Failed to publish plugins, Plugin Framework exception ",jpfe);
            System.exit(-1);
        } catch (NullPointerException npe){
            log.log(Level.WARNING,"Failed to publish plugins, null pointer: ",npe);
            System.exit(-1);
           
        }
               
    }

    /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void toggleStage() {
            ObservableList selectedStage = (ObservableList) AvailStages.getSelectionModel().getSelectedItems();
            log.fine("Event: a stage was selected " + selectedStage);
            selectedStageList.clear();
            selectedStageList.addAll(selectedStage);
            StageOrd.setItems(FXCollections.observableArrayList(selectedStageList));
            
        }
        
     /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void ChromosomeSelected() {
            ObservableList selectedChromosome = (ObservableList) ChromSelect.getSelectionModel().getSelectedItem();
            log.fine("Event: a chromosome was selected " + selectedChromosome);
            //todo: Filter Stages and FitnessFunciton list to only those tha support this chromosome or ANY
        }
        /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void FitnessFunctionSelected() {
            ObservableList selectedFF = (ObservableList) SelectedFitnessFunction.getSelectionModel().getSelectedItem();
            log.fine("Event: a chromosome was selected " + selectedFF);
            //todo: filter the Stage available list to the chromosome required by this fitness function or a stage that can use Any chromosome
            //todo: Set the chromosome list to the required chromosome it not yet selected.
            //todo: Display warning if the chromosome type for the fitness function doesn't match the type selected in chromosome selection.  I'm not sure this will every happen.
            
            
        }

                        /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void MoveStageUp(ActionEvent event){
            String selected = (String) StageOrd.getSelectionModel().getSelectedItem();
            if(selected != null){
                int positionOfSelected = selectedStageList.indexOf(selected);
                log.fine(selected.toString() + "started in possition: " + (positionOfSelected+1));
                selectedStageList.set(positionOfSelected, selectedStageList.get(positionOfSelected-1));
                selectedStageList.set(positionOfSelected-1,selected);
                StageOrd.setItems(FXCollections.observableArrayList(selectedStageList));
                StageOrd.getSelectionModel().select(positionOfSelected-1);
            }
            event.consume();
        }
        
                        /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void MoveStageDown(ActionEvent event){
            String selected = (String) StageOrd.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int positionOfSelected = selectedStageList.indexOf(selected);
                log.fine(selected.toString() + "started in possition: " + (positionOfSelected+1));
                selectedStageList.set(positionOfSelected, selectedStageList.get(positionOfSelected+1));
                selectedStageList.set(positionOfSelected+1,selected);
                StageOrd.setItems(FXCollections.observableArrayList(selectedStageList));
                StageOrd.getSelectionModel().select(positionOfSelected+1);
            }
            event.consume();
        }
        
                /**
     * This is the event handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void InitializeExperiment(ActionEvent event) throws PluginLifecycleException, Exception {
            MessageBar.setText("");
            StringBuffer msg = new StringBuffer("This is an error message");
        //Check to ensure all the needed slections are made
            if(!checkSelecitons(msg)){
                MessageBar.setText(msg.toString());
                return;
            }
            
        //Create the population to use for the algorithm
            Population pop = new Population();
            boolean populationCreated = createPopulation(pop);
            log.fine("Population size is: "+pop.size());
            if(!populationCreated && pop.size() <=0) {
                MessageBar.setText("experiment not initialized: " + MessageBar.getText());
                return;
            }
            
        //Get the generation limit for this algorithm
            int maxGenerations = 100;
            String rawMaxGen = MaxGen.getText();
            try{
            maxGenerations = Integer.valueOf(rawMaxGen);
            }catch(NumberFormatException nfe){
                MessageBar.setText("experiment not initialized: " + MessageBar.getText()+" MaxGenerations must be an integer");
            }
        //Create the fitness function to use for the algorithm
            String selectedFF = (String) SelectedFitnessFunction.getSelectionModel().getSelectedItem();
            if (selectedFF == null) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null);
                throw new Exception("No Fitness Function was Selected");
            }
            Fitness ff = CreateFitnessFuction(selectedFF);
            log.fine("Fitness Function " + selectedFF + " is loaded");

        //Double check that the fitness funciton exists.
            assert ff != null :"The Fitness function was never found";
            ff.setBiggerIsBetter((int) Math.floor(JeneSize.getValue()), true);
        //Create the Genetic Algorithm
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat df = new SimpleDateFormat("ddMMMyyy-HHmmss.SSS");
            String title = selectedFF + "-"+ df.format(date);
            GeneticAlgorithm ga = new GeneticAlgorithm(ff,pop,maxGenerations);
            ga.setTaskTitle(title);
        //Add the indicated states in order
            boolean stagesAdded = addStages(ga);
        
        //If the stages didn't add, then return without scheduling
            if(!stagesAdded) {
                MessageBar.setText("experiment not initialized: " + MessageBar.getText());
                return;
            }
        // And run the algorithm with the population
            experimentQueueList.add(ga.getTitle()); 
            experiments.put(ga.getTitle(), ga);
            ExperimentQueue.setItems(FXCollections.observableArrayList(experimentQueueList));
    }

                /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void StartExperiment(ActionEvent event) {
            if (runningAlgorithm != null){
                MessageBar.setText("Cannot Start: There is currently an experiment running");
                return;
            }
            if (event.getSource()==null){
                log.fine("This was started from the OnApplicaitonStop listner");
            }
            MessageBar.setText("Starting Experiment: ");
            //clear output before starting
            RunningLog.setText("");
            CurProg.getData().removeAll(chartDataMax);
            CurProg.getData().removeAll(chartDataMean);
            CurProg.getData().removeAll(chartDataMin);
            chartDataMax.getData().clear();
            chartDataMean.getData().clear();
            chartDataMin.getData().clear();
            
            GeneticAlgorithm ga=null;
            String experimentTitle="";
        //Check to see if there are experiments
            if (experimentQueueList.size()==0){
                MessageBar.setText("There are no experiments to start");
                return;
            }
        //If no selected experiment, then start the first in the list
            if (ExperimentQueue.getSelectionModel().isEmpty()){
            //This is a neat trick I clear the not selected item and the set the selected item to the first item 
                ExperimentQueue.getSelectionModel().select(0);
            }

        //Start the selected experiment
            experimentTitle= (String) ExperimentQueue.getSelectionModel().getSelectedItem();
            log.fine("Event: a experiment was started " + experimentTitle);
            ga = (GeneticAlgorithm) experiments.get(experimentTitle);
            assert ga != null : "The genetic Algorithm was mull cannot start.";
            log.fine("The experiment found was "+ga.getTitle());
            ga.addGenerationEventListener(this);
            ga.addAlgorithmEventListener(this);
            setupChart();
            MessageBar.setText("Conductin Experiment: " +ga.getTitle());
            runningAlgorithm = ga;
        //Create an XLS Logfile
            Path resultsXSLLoggerLocation = Paths.get(System.getProperty("user.home"),ga.getTitle()+".xls");
            Path resultsCSVLoggerLocation = Paths.get(System.getProperty("user.home"),ga.getTitle()+".csv");
            try {
//                resultsXSLLogger = new XLSLogger(resultsLoggerSchema,resultsXSLLoggerLocation.toString());
                resultsCSVLogger = new CSVLogger(resultsLoggerSchema,resultsCSVLoggerLocation.toString());
            } catch (IOException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        //Now Start The Experiment
/*            Thread thisExperiment = new Thread(ga);
            log.fine("setup the thread.  the Call method was called");
            thisExperiment.setDaemon(true);
            log.fine("set the threadinto Daemon mode. about to call start");
            thisExperiment.start();
            log.fine("finished the call to start()");*/
            ga.evolve();
        }
    /**
     * This is the even handler for selecting a stage.
     * Currently this doesn't work.
     * @param event 
     */
        public void AbortExperiment(ActionEvent event) {
            if(runningAlgorithm == null){
                return;
            }
            log.fine("Aborting Experiment "+runningAlgorithm.getTitle());
            runningAlgorithm.cancel(true);
            MessageBar.setText(runningAlgorithm.getTitle()+ "was aborted");
            experimentQueueList.remove(runningAlgorithm.getTitle()); 
            experiments.remove(runningAlgorithm.getTitle());
            ExperimentQueue.setItems(FXCollections.observableArrayList(experimentQueueList));

            runningAlgorithm=null;
            //todo: Stop the current experiment.
        }

    private Fitness CreateFitnessFuction(String selectedFF) {
        Class pluginClass = null;
        //get the descriptor for the Fitness Extension Point
        PluginDescriptor fitnessExtensionType = plugReg.getPluginDescriptor("jenes.population.Fitness");
        log.fine("Fitness function extension point is " + fitnessExtensionType.getPluginClassName());
        //get the plugins that contain this extension point.
        ExtensionPoint extPointAbstractStages = plugReg.getExtensionPoint(fitnessExtensionType.getId(), "jenes.population.Fitness");
        log.fine("Fitness function extension point is " + extPointAbstractStages.toString());
        //find the extension point that has the name selectedFF
        Collection<Extension> theListOfFitnessFunctions = extPointAbstractStages.getConnectedExtensions();
        log.fine("The list of FitenssFunctions is " + Integer.toString(theListOfFitnessFunctions.size()));
        for (Iterator it = theListOfFitnessFunctions.iterator(); it.hasNext();){
            Extension ext = (Extension) it.next();
            log.fine("Looking for this FitnessFunction: " + selectedFF + " Found this fitness funciton: "+ext.getParameter("name").toString());
            if(selectedFF.equals(ext.getParameter("name").rawValue() )){
                //Ensure the plugin is active
//                    pluginManager.activatePlugin(ext.getDeclaringPluginDescriptor().getId());
                // Get the specific class loader for this set or plugins.
                ClassLoader classLoader = pluginManager.getPluginClassLoader(ext.getDeclaringPluginDescriptor());
                try {
                    pluginClass = classLoader.loadClass(ext.getParameter("class").valueAsString());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    return (Fitness) pluginClass.newInstance();
                } catch (InstantiationException ex) {
                    Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private boolean addStages(GeneticAlgorithm ga) {
        AbstractStage thisStage=null;
        log.fine("The ordered list of stages is " + selectedStageList.toString());
    //look up the plugin descriptor in the hashmaps?
        for (Iterator it = selectedStageList.iterator(); it.hasNext();){
            String stageName = (String) it.next();
            log.fine("Creating the stage " + stageName);
            PluginDescriptor stagePluginDesc = (PluginDescriptor) genericStages.get(stageName);
            log.fine("Found " + stageName +" Plugin Descriptor " + stagePluginDesc.getPluginClassName());
        //create an instance of the stage.
            ClassLoader classLoader = pluginManager.getPluginClassLoader(stagePluginDesc);
            Class pluginClass = null;
            try {
                pluginClass = classLoader.loadClass(stagePluginDesc.getPluginClassName());
                log.fine("Created an instance of " + pluginClass.getCanonicalName());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        //Pass the Additional Properties to the stage for self configuration
            try {
                thisStage =(AbstractStage) pluginClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                MessageBar.setText(stageName +" needs a no arg constructor.");
                return false;
            }
                String params = AddedParams.getText();
                if (params == null){params="";}
                thisStage.processProperties(params);
        //Add the stage to the GA
                ga.addStage(thisStage);
                log.fine("Added Stage to Algorithm");

        }
    //Then all stages were added without throwing an error, so return true.
        return true;
    }

    private boolean checkSelecitons(StringBuffer msg) {
        //empty the parameter then recreate it.
        msg.delete(0,msg.capacity());
        msg.append("The following Selections Must Be Made: ");
        boolean allSelected = true;
        if (ChromSelect.getSelectionModel().getSelectedItem() == null){
            msg.append(" Chomosome Type ");
            allSelected = false;
        }
        if (SelectedFitnessFunction.getSelectionModel().getSelectedItem() == null){
            msg.append(" Fitness Function ");
            allSelected = false;
        }
        if (selectedStageList.size() <1){
            msg.append(" Additional Stages ");
            allSelected = false;
        }
        log.fine(msg.toString());
        
        int tempMaxPop=0;
        try{
             tempMaxPop = Integer.valueOf(MaxPopTxt.getText());
        }catch (NumberFormatException nfe){
            msg.append(" Maximum Population must be an integer");
        }
        if(tempMaxPop < 1) {
            msg.append(" Maximum Population must be 1 or more");
            allSelected = false;
        }
        return allSelected;
    }

    private boolean createPopulation(Population p) {
        MessageBar.setText("");
        int maxPop = Integer.valueOf(MaxPopTxt.getText());
        Chromosome chrom = null;
    //        MaxGen;
        log.fine("The chromosome type is " + ChromSelect.getSelectionModel().getSelectedItem().toString());
    //look up the plugin descriptor in the hashmaps?
            String chromosomeName = ChromSelect.getSelectionModel().getSelectedItem().toString();
            log.fine("Creating the stage " + chromosomeName);
            PluginDescriptor chromosomePluginDesc = (PluginDescriptor) chromosomeTypes.get(chromosomeName);
            log.fine("Found " + chromosomeName +" Plugin Descriptor " + chromosomePluginDesc.getPluginClassName());
        //load the plugin for the chromosome.
            ClassLoader classLoader = pluginManager.getPluginClassLoader(chromosomePluginDesc);
            Class chromosomeClass = null;
            try {
                chromosomeClass = classLoader.loadClass(chromosomePluginDesc.getPluginClassName());
                log.fine("Created an instance of " + chromosomeClass.getCanonicalName());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                MessageBar.setText("Could not load plugin for the " + chromosomeName + " chromosome");
                return false;
            }
        //Create an instance of the chromosome
            try {
                chrom = (Chromosome) chromosomeClass.newInstance();
                log.fine("We have a chromosome of type "+chrom.getClass().getCanonicalName());
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(GateController.class.getName()).log(Level.SEVERE, null, ex);
                MessageBar.setText(chromosomeName +" needs a no arg constructor.");
                return false;
            }
        //now create an individual with the chromosome
            double sliderValue = JeneSize.getValue();
            Individual indie = new Individual(chrom,sliderValue);
        //Now we need to create a population of Maximum Population Szie with the individuals
            Population newPop = new Population(indie,maxPop);
            log.fine("the newpopulation size is "+newPop.size());
            p.add(newPop);
            log.fine("We have a population of individuals with "+sliderValue+" chromosomes of type "+chrom.getClass().getCanonicalName()+"");
        return true;
    }
        public void onGeneration(GeneticAlgorithm ga, long time){
            log.fine("Statistics were generated for generation: "+ga.getGeneration());
            AllPopulationFilter popfilter = new AllPopulationFilter();

//            Population.Statistics thisGenStats = ga.getHistoryAt(ga.getGeneration()).getStatistics();
            Population.Statistics thisGenStats = ga.getHistoryAt(ga.getGeneration()-1).getStatistics();
//            Population.Statistics.Group thisGenStats = ga.getHistoryAt(ga.getGeneration()).getStatistics().addGroup(popfilter,true);
            if(thisGenStats == null){
                log.fine("The population statistics group was null");
                return;
            }
            double max4gen = thisGenStats.getLegalHighestScore();
            double mean4gen =thisGenStats.getLegalScoreAvg();
            double min4gen =thisGenStats.getLegalLowestScore();
//            double max4gen = thisGenStats.getMax()[ga.getGeneration()];
//            double mean4gen =thisGenStats.getMean()[ga.getGeneration()];
//            double min4gen =thisGenStats.getMin()[ga.getGeneration()];
            log.fine("The max value is :"+max4gen);
            
            chartDataMax.getData().add(new XYChart.Data(ga.getGeneration(),max4gen));
            chartDataMean.getData().add(new XYChart.Data(ga.getGeneration(),mean4gen));
            chartDataMin.getData().add(new XYChart.Data(ga.getGeneration(),min4gen));

            //Addjust the range on the chart
//            CurProg.getYAxis().invalidateRange();
            
            log.fine("graph is updated for generation: "+ga.getGeneration()+"with max="+max4gen+" min="+min4gen+" avg="+mean4gen);
//            RunningLog.setPrefRowCount(RunningLog.getPrefRowCount()+1);
            RunningLog.appendText(ga.getTitle()+": Started at "+runningLogDateFormat.format(ga.statistics.getFitnessEvalStageBegin())+": generation: "+ga.getGeneration()+" [max="+max4gen+", min="+min4gen+", avg="+mean4gen+"]\n");
        /* logging schema is "startTime","generation","randomSeed","maxValue","minValue","averageValue"
         */
            writeStatistics(ga, thisGenStats);
        }

    @Override
    public void onAlgorithmStart(GeneticAlgorithm ga, long time) {
        MessageBar.setText(ga.getTitle() + " has started");
        runningAlgorithm=ga;
    }

    @Override
    public void onAlgorithmStop(GeneticAlgorithm ga, long time) {
        MessageBar.setText(ga.getTitle() + " has stopped");
        //Remove the experiment from the queue
        
        //Check for AutoStart  If yes start the next experiment
        //if not autoStaart then do not start the next experiment. 
        experimentQueueList.remove(ga.getTitle()); 
        experiments.remove(ga.getTitle());
        ExperimentQueue.setItems(FXCollections.observableArrayList(experimentQueueList));
        ExperimentQueue.getSelectionModel().select(0);
//        resultsXSLLogger.close();
        resultsCSVLogger.close();
        runningAlgorithm=null;
        MessageBar.setText(MessageBar.getText()+" Starting Next Experiment");

        if(AutoRun.isSelected()){
            this.StartExperiment(new ActionEvent());
        }
        
    }

    @Override
    public void onAlgorithmInit(GeneticAlgorithm ga, long time) {
        MessageBar.setText(ga.getTitle() + " is initialized");;
    }

    private void setupChart() {
        chartDataMax.setName("Maximum Population Value");
        chartDataMean.setName("Mean Population Value");
        chartDataMin.setName("Min Population Value");
        
        CurProg.getData().add(chartDataMax);
        CurProg.getData().add(chartDataMean);
        CurProg.getData().add(chartDataMin);
    }

    private void writeStatistics(GeneticAlgorithm ga, Statistics thisGenStats) {
        /*          The Excel Logger doesn't seem to write lines properly
         *          log.fine("Output line to results Excel file");
                    resultsXSLLogger.setLine(ga.getGeneration());
                    resultsXSLLogger.put("startTime",ga.statistics.getStartTime());
                    resultsXSLLogger.put("generation",ga.getGeneration());
                    resultsXSLLogger.put("randomSeed",ga.statistics.getRandomSeed());
                    resultsXSLLogger.put("maxValue",thisGenStats.getLegalHighestScore());
                    resultsXSLLogger.put("minValue",thisGenStats.getLegalScoreAvg());
                    resultsXSLLogger.put("averageValue",thisGenStats.getLegalLowestScore());
                    resultsXSLLogger.save();
                    log.fine("saved XSL restuls line "+resultsXSLLogger.getLine());*/
                    log.fine("Output line to results CSV file");
                    resultsCSVLogger.put("startTime",ga.statistics.getStartTime());
                    resultsCSVLogger.put("generation",ga.getGeneration());
                    resultsCSVLogger.put("randomSeed",ga.statistics.getRandomSeed());
                    resultsCSVLogger.put("maxValue",thisGenStats.getLegalHighestScore());
                    resultsCSVLogger.put("minValue",thisGenStats.getLegalScoreAvg());
                    resultsCSVLogger.put("averageValue",thisGenStats.getLegalLowestScore());
                    resultsCSVLogger.save();
                    log.fine("saved CSV restuls line.");
    }


    private static class AllPopulationFilter implements Population.Filter{

        public AllPopulationFilter() {
            
        }
        public boolean pass(Individual<?> individual){
            double score = individual.getScore();
            if ((score != Double.NaN) && (score != Double.NEGATIVE_INFINITY) && (score != Double.POSITIVE_INFINITY)){
                return true;
            }else{
                return false;
            }
        }
    }

}

