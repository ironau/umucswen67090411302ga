/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gate;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 *
 * @author ironau
 */
public class StatsGenerationListener implements GenerationEventListener<Chromosome>{
    Population.Statistics stats;
    int generationCount;
    
    @Override
    public void onGeneration(GeneticAlgorithm<Chromosome> ga, long time) {
        generationCount = ga.getGeneration();
        stats = ga.getCurrentPopulation().getStatistics();
//        WorkerStateEvent genComplete = new GenerationEvent(stats, generationCount);
//        Event.fireEvent(WorkerStateEvent.WORKER_STATE_RUNNING , genComplete);
    }

}
