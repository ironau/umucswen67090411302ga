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
package jenes.tutorials.problem10;

import java.util.logging.Logger;
import jenes.population.Fitness;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;
import jenes.stage.operator.common.RouletteWheelSelector;

/**
 * This class represent the fitness function for problem 10
 * 
 * @since 2.0
 */
public class FF4XYLessX4LessY4 extends Fitness<DoubleChromosome> {

    static final Logger log = java.util.logging.Logger.getLogger(RouletteWheelSelector.class.getName()) ;
    
    public FF4XYLessX4LessY4(boolean... bis) {
        super(bis);
    }

    /**
     * This is a no argument constructor that defaults to a single objective for maximization
     */
    public FF4XYLessX4LessY4() {
        super(true);
    }

    @Override
    public void evaluate(Individual<DoubleChromosome> individual) {
        DoubleChromosome chromosome = individual.getChromosome();
        //f(x , y) = - x^4 - y^4 + 4xy 
        double x, y, f, x4, y4;

        
        x = chromosome.getValue(0);
//        log.fine("Evaluating the X chromosome at index 0. X="+x);
        y = chromosome.getValue(1);
//        log.fine("Evaluating the Y chromosome at index 1. Y="+y);
        x4 = Math.pow(x,4);
        log.fine("X4="+x4);
        y4 = Math.pow(y,4);
        log.fine("Y4="+y4);

        f = -x4 - y4 + 4 * x * y;
//        log.fine("-X4 - Y4 + 4XY = f ="+f);
        individual.setScore(f);
        log.fine("set the score to "+individual.getScore());
    }

    @Override
    public Fitness<DoubleChromosome> createInstance() {
        return new FF4XYLessX4LessY4(true);
    }

    @Override
    protected void doStart() throws Exception {
        
    }

    @Override
    protected void doStop() throws Exception {
        
    }
}
