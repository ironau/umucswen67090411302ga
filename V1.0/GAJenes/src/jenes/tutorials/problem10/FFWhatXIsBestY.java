/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.problem10;

import jenes.population.Fitness;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;

/**
 * This class represent the fitness function for Dr. Brown's Initial Problem:
 * What value of X will yield the best value of Y
 * 
 * @since 2.0
 *
 * @author klestraw
 */
public class FFWhatXIsBestY extends Fitness<DoubleChromosome>{
    
    public FFWhatXIsBestY(boolean... bis)  {
    super(bis);
    }

    /**
     * This is a no argument constructor that defaults to a single objective for maximization
     */
    
	public FFWhatXIsBestY() {
        super(true);
    }

    @Override
    public void evaluate(Individual<DoubleChromosome> individual) {
        DoubleChromosome chromosome = individual.getChromosome();
        //y = -2 * x^2 + 20 * x + 5
	double x, y, x2;

        x = chromosome.getValue(0);
        y = chromosome.getValue(1);
        x2 = x * x;
                
     	y = -2 * x2 + 20 * x + 5;
		
        individual.setScore(x);
        individual.setScore(y);
    }

    @Override
    public Fitness<DoubleChromosome> createInstance() {
    	return new FFWhatXIsBestY(true);
    }

    @Override
    protected void doStart() throws Exception {
        
    }

    @Override
    protected void doStop() throws Exception {
        
    }
}

