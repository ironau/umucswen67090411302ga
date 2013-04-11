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
package jenes.stage.operator.common;

import java.util.List;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Filter;
import jenes.stage.operator.Selector;

/**
 * A classic roulette wheel selection operator. If s if the input population size, s individuals will be selected. The best ones
 * have a higher selection probability.
 * A {@link #preSelect(Population)} method is useful to set up the selection state according to the population state to undergo at the 
 * selection process.
 * <p>
 * @param <T> The class of chromosomes to work with.
 * 
 * @version 2.0
 * @since 1.0
 * 
 * @see Individual
 * @see Population
 */
public class RouletteWheelSelector<T extends Chromosome> extends Selector<T> {

    double[] cumulativeFit = null;
    double[] fit = null;
    double[] partialFit = null;

    @Override
    protected void preSelect(Population<T> pop, Filter filter) {
        //call to the super selector implementation in order to fill the 'mating pool'
        super.preSelect(pop, filter);
        
        //roulette wheel uses all individuals
        int popsize = pop.size();

        if (cumulativeFit == null || cumulativeFit.length < popsize + 1) {
            cumulativeFit = new double[popsize + 1];
            partialFit = new double[popsize + 1];
            fit = new double[popsize];
        }


        int m = this.fitness != null ? this.fitness.getNumOfObjectives() : 1;
        boolean[] bib = this.fitness != null ? this.fitness.getBiggerIsBetter() : new boolean[]{this.biggerIsBetter};

        for (int i = 0; i < cumulativeFit.length; ++i) {
            cumulativeFit[i] = 0;
        }

        for (int h = 0; h < m; ++h) {

            boolean maximize = bib[h];

            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;

            for (int i = 0; i < popsize; ++i) {
                fit[i] = pop.getIndividual(i).getScore(h);
                if (fit[i] > max) {
                    max = fit[i];
                }
                if (fit[i] < min) {
                    min = fit[i];
                }
            }

            partialFit[0] = 0;
            for (int i = 0; i < popsize; ++i) {
                partialFit[i + 1] = partialFit[i] + (maximize ? fit[i] : max + min - fit[i]);
            }

            double den = partialFit[partialFit.length - 1];
            for (int i = 0; i < popsize; ++i) {
                cumulativeFit[i + 1] += partialFit[i + 1] / den;
            }
        }

    }

    @Override
    protected Individual<T> select(List<Individual<T>> list) {

        int popsize = list.size();

        if (this.cumulativeFit[popsize] == 0.0) {
            return list.get(this.random.nextInt(popsize));
        }

        double r = random.nextDouble(cumulativeFit[popsize]);

        // Find individual by binary chop;
        int low = 0;
        int high = popsize;
        int p;

        while (true) {

            p = low + ((high - low) / 2);

            if (r < cumulativeFit[p]) {
                high = p;
            } else if (r >= cumulativeFit[p + 1]) {
                low = p + 1;
            } else {
                break;
            }

        }

        return list.get(p);
    }

}
