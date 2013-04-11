/*
 * JENES
 * A time and memory efficient Java library for genetic algorithms and more 
 * Copyright (C) 2011 Intelligentia srl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package jenes.tutorials.problem11;

import jenes.GeneticAlgorithm;
import jenes.GeneticAlgorithm.Statistics;
import jenes.algorithms.SimpleGA;
import jenes.chromosome.BitwiseChromosome;
import jenes.chromosome.codings.IntCoding;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.tutorials.utils.Utils;
import jenes.utils.multitasking.MultiThreadEvaluator;

/**
 * This class represent a simple example of how to use multi-thread feature in 
 * Jenes 2.0
 * 
 * @since 2.0
 */
public class MultiThreadExample {
    

    public static void main(String... a) throws Exception{
        Utils.printHeader();
        System.out.println();

        System.out.println("TUTORIAL 11:");
        System.out.println("This tutorial show how to use multi-thread execution"
                + " in evaluating complex fitness functions.");
        System.out.println();

        /** The angle to use as target for matching */
        int targetAngle = 10;
        
        //instatiate fitness function
        ImageMatchingFitness f = new ImageMatchingFitness(targetAngle);
        
        //code sample individual, initial population, than simple ga
        BitwiseChromosome.BitCoding coding = new IntCoding();
        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(1, coding));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, 100);
        SimpleGA<BitwiseChromosome> ga = new SimpleGA<BitwiseChromosome>(f, pop);
        
        //run a test-set
        testAlgorithm(ga);
        
        System.exit(0);
    }

    /**
     * Execute a test-set using different thread pool size configuration. Best
     * results are achieved using a num of thread equals to the avaible CPU cores of your hardware
     * @param algorithm the algorithm to test
     */
    private static void testAlgorithm(GeneticAlgorithm algorithm) {
        //parallel evaluation
        int[] testSet = new int[]{32, 16, 8, 4, 2, 1};
        for (int test : testSet) {
            MultiThreadEvaluator eval = new MultiThreadEvaluator(test);
            eval.execute(algorithm);
            printStatistics(algorithm, test);
            
            /* try to free memory between tests */
            System.gc();
        }
        
        //sequential evaluation
        algorithm.evolve();
        printStatistics(algorithm, 0);
        
    }

    /**
     * Print statistics
     * @param ga
     * @param threads 
     */
    private static void printStatistics(GeneticAlgorithm<BitwiseChromosome> ga, int threads) {
        Statistics stats = ga.getStatistics();
        
        String header;
        if (threads == 0) {
            header = "[sequential]";
        } else if (threads == 1) {
            header = "[1 thread] the main thread produces tasks the helper one evaluate fitness";
        } else {
            header = String.format("[%d threads] the main thread produces tasks and %d helpers "
                    + "evaluate fitness in parallel", threads, threads);
        }
        
        Group group = ga.getCurrentPopulation().getStatistics().getGroup(Population.LEGALS);
        Individual best = group.get(0);
        
        double distance = best.getScore();
        
        System.out.format("%s\n\tended in %d ms, "
                + "\n\ttime spent per fitness evaluation %d "
                + "\n\tfitness evaluations performed: %d"
                + "\n\ttime for fitness evaluation/total time: %f%%"
                + "\n\tdistance from target: %.2f (%s)\n", 
                header,
                stats.getExecutionTime(),
                stats.getTimeSpentForFitnessEval(),
                stats.getFitnessEvaluationNumbers(),
                stats.getTimeSpentForFitnessEval() * 100d / stats.getExecutionTime(),
                distance,
                distance == 0.0 ? "exact matching" : "");
    }
}
