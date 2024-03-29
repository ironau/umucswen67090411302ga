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
package jenes.tutorials.problem6;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.BooleanChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.ExclusiveDispenser;
import jenes.stage.Parallel;
import jenes.stage.Sequence;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;

/**
 * Tutorial showing how to minimization and maximization sub-prolems can cohesists in
 * the breeding structure of Jenes.
 *
 * This class implements a genetic algorithm for solving the Knapsack problem.
 *
 * @version 2.0
 * @since 1.0
 */
public class KnapsackGA extends GeneticAlgorithm<BooleanChromosome>{
    
    private class KnapsackFitness extends Fitness<BooleanChromosome> {
                
        private KnapsackFitness(boolean maximize) {
            super( maximize );
        }
        
        @Override
        public void evaluate(Individual<BooleanChromosome> individual) {
            double utility = getUtilityOf(individual);
            double weight = getWeightOf(individual);
            individual.setScore(utility);
            individual.setLegal(weight <= KnapsackGA.this.capacity);
        }

        @Override
        public Fitness<BooleanChromosome> createInstance() {
            return this;
        }

        @Override
        protected void doStart() throws Exception {
        }

        @Override
        protected void doStop() throws Exception {
        }
        
    }
    
    private double capacity;
    private double[] weights;
    private double[] utilities;
    
    private KnapsackFitness maximize = new KnapsackFitness(true);
    private KnapsackFitness minimize = new KnapsackFitness(false);

    public KnapsackGA( int popsize, int generations, double[] utilities, double[] weights) {
        super( new Population<BooleanChromosome>(new Individual<BooleanChromosome>(new BooleanChromosome(utilities.length)), popsize), generations);
        
        this.utilities = utilities;
        this.weights = weights;
        
        Parallel<BooleanChromosome> parallel =
                new Parallel<BooleanChromosome>(new ExclusiveDispenser<BooleanChromosome>(2){
            
            @Override
            public int distribute(Individual<BooleanChromosome> ind) {
                return ind.isLegal() ? 0 :1;
            }
            
        });
        
        this.setFitness(this.maximize);
        
        Sequence<BooleanChromosome> seq_legal = new Sequence<BooleanChromosome>();
        seq_legal.appendStage(new TournamentSelector<BooleanChromosome>(2));
        seq_legal.appendStage(new OnePointCrossover<BooleanChromosome>(0.8));
        seq_legal.appendStage(new SimpleMutator<BooleanChromosome>(0.02));
        
        Sequence<BooleanChromosome> seq_illegal = new Sequence<BooleanChromosome>();
        seq_illegal.appendStage(new TournamentSelector<BooleanChromosome>(2));
        seq_illegal.appendStage(new OnePointCrossover<BooleanChromosome>(0.8));
        seq_illegal.appendStage(new SimpleMutator<BooleanChromosome>(0.2));
        
        parallel.add(seq_legal);
        parallel.add(seq_illegal);
        
        this.addStage(parallel);
        
        seq_legal.setFitness(this.maximize);
        seq_illegal.setFitness(this.minimize);
    }
    
    public double getCapacity() {
        return this.capacity;
    }
    
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
    
    public double getUtilityOf(Individual<BooleanChromosome> individual) {
        BooleanChromosome chrom = individual.getChromosome();
        double utility = 0.0;
        int size = chrom.length();
        for(int i = 0; i < size; ++i){
            utility += chrom.getValue(i) ? this.utilities[i] : 0.0;
        }
        return utility;
    }
    
    public double getWeightOf(Individual<BooleanChromosome> individual) {
        BooleanChromosome chrom = individual.getChromosome();
        double weight=0.0;
        int size = chrom.length();
        for(int i = 0; i < size; ++i){
            weight += chrom.getValue(i) ? this.weights[i] : 0.0;
        }
        return weight;
    }    
    
}
