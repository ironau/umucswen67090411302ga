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
package jenes.tutorials.problem13;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jenes.GeneticAlgorithm;
import jenes.GeneticAlgorithm.ElitismStrategy;
import jenes.chromosome.AlleleSet;
import jenes.chromosome.GenericAlleleSet;

import jenes.chromosome.ObjectChromosome;
import jenes.chromosome.ObjectChromosome.Gene;

import jenes.population.Fitness.SortingMode;
import jenes.population.Individual;
import jenes.population.Population.Statistics;
import jenes.population.Population.Statistics.Group;
import jenes.population.Population;

import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.tutorials.utils.Utils;

/**
 * This class set-up the algorithm for execution, it includes the main method.
 *
 * @since 2.1
 */
public class TaskSchedulingProblem {

    /** Standard parameters for a GeneticAlgorithm */
    private static final int POPULATION_SIZE = 100;
    private static final int GENERATION_LIMIT = 1000;
    private static final int TASK_HOUR_PER_WEEK = 28;

    public static void main(String[] args) {
        Utils.printHeader();
        System.out.println();

        System.out.println("TUTORIAL 13:");
        System.out.println("This tutorial shows an other example in using "
                + "ObjectChromosome in this case for task planning.");
        System.out.println();
        
        //create the student set
        Student[] students = createStudentSet();
        
        //create the week and assign to each day the given operativity hour interval
        Week week = new Week(9, 21);

        //create the fitness for the problem
        WeekFitness fitness = new WeekFitness(2, false, students, week, TASK_HOUR_PER_WEEK);
        fitness.setSortingMode(SortingMode.CROWDING);

        //create the sample individual for the population
        Individual<ObjectChromosome> sample = generateIndividual(students, week);
        Population<ObjectChromosome> pop = new Population<ObjectChromosome>(sample, POPULATION_SIZE);

        //create the genetic algorithm in a usual way
        GeneticAlgorithm<ObjectChromosome> ga =
                new GeneticAlgorithm<ObjectChromosome>(fitness, pop, GENERATION_LIMIT);
        ga.addStage(new TournamentSelector<ObjectChromosome>(3));
        ga.addStage(new OnePointCrossover<ObjectChromosome>(0.7));
        ga.addStage(new SimpleMutator<ObjectChromosome>(0.02));

        ga.setElitismStrategy(ElitismStrategy.WORST);
        ga.evolve();

        //...get statistics
        Statistics<ObjectChromosome> popStats = ga.getCurrentPopulation().getStatistics();
        GeneticAlgorithm.Statistics algoStats = ga.getStatistics();

        Group<ObjectChromosome> legals = popStats.getGroup(Population.LEGALS);

        System.out.println("Solution: ");
        System.out.println(prettyPrinter(legals.get(0), week));
        System.out.format("found in %d ms.\n", algoStats.getExecutionTime());
        System.out.println();

        Utils.printStatistics(popStats);
    }

    /**
     * Create the student set considered in the problem
     * @return 
     */
    private static Student[] createStudentSet() {
        Student[] students = {
            new Student("Jack") {
                @Override
                public boolean isBusy(Day day, int hour) {
                    if (day == Day.MONDAY || day == Day.WEDNESDAY || day == Day.FRIDAY) {
                        if (hour == 8 || hour == 9 || hour == 10 || hour == 11) {
                            return true;
                        }
                    }
                    return false;
                }
            },
            new Student("Bill") {
                @Override
                public boolean isBusy(Day day, int hour) {

                    if (day == Day.MONDAY || day == Day.WEDNESDAY || day == Day.FRIDAY) {
                        if (hour == 15 || hour == 16 || hour == 17 || hour == 18) {
                            return true;
                        }
                    }
                    return false;
                }
            },
            new Student("Chris") {
                @Override
                public boolean isBusy(Day day, int hour) {
                    if (day == Day.TUESDAY || day == Day.THURSDAY || day == Day.SATURDAY) {
                        if (hour == 8 || hour == 9 || hour == 10 || hour == 11) {
                            return true;
                        }
                    }
                    return false;
                }
            },
            new Student("John") {
                @Override
                public boolean isBusy(Day day, int hour) {
                    if (day == Day.TUESDAY || day == Day.THURSDAY || day == Day.SATURDAY) {
                        if (hour == 15 || hour == 16 || hour == 17 || hour == 18) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        };
        
        return students;
    }

    /**
     * This method creates a template of Individual for the first population to evolve.
     *
     * @param s the array representing students that are living in the house
     * @param w the week of interest for planning
     *
     * @return an Individual composed by an ObjectChromosome where each gene
     * represent a given hour in the day and it will contain the student
     * assigned to perform task in that temporal slise.
     */
    private static Individual<ObjectChromosome> generateIndividual(Student[] s, Week w) {

        List<AlleleSet> genes = new ArrayList<AlleleSet>();
        for (int i = 0; i < w.getDayCount(); i++) {	//create the alphabet for the week
            Day d = w.getDay(i);

            //...each hour is an AlleleSet
            List< AlleleSet<Student> > alphabet = getDayAlphabet(d, s);
            genes.addAll(alphabet);
        }

        return new Individual<ObjectChromosome>(new ObjectChromosome(genes));
    }

    /**
     * This method create the alphabet of students (a list of AlleleSet) for 
     * each hour in the given day
     *
     * @param student an array of the students that are living in the house 
     * (candidate for task execution)
     * @return a list that contains an alphabet for each legal temporal slice 
     * in the day; the legal slices are those in which the students can make some
     * task according to their availability
     */
    private static List< AlleleSet<Student>> getDayAlphabet(Day d, Student[] student) {

        List<AlleleSet<Student>> alphabet = new ArrayList<AlleleSet<Student>>();
        for (int i = 0; i < d.getHourInDay(); i++) {
            AlleleSet<Student> al = createAlleleSet(student, d, d.getStart() + i);
            alphabet.add(al); //add the alphabet for hour start+i
        }

        return alphabet;
    }

    /**
     * This method create an AlleleSet according to the student's agenda. In particular
     * the allele set represent the availability of the students for the given hour and 
     * day passed as arguments.
     *
     * @param student the students to verify availability
     * @param hour the hour in which we test if the students are available.
     * 
     * @return an AlleleSet with only the free students for that hour and day
     */
    private static AlleleSet<Student> createAlleleSet(Student[] student, Day d, int hour) {

        Set<Student> freeStudents = new HashSet<Student>();

        //the student "nobody" is present... 
        freeStudents.add(new Student.Nobody());

        for (int k = 0; k < student.length; k++) {
            if (!student[k].isBusy(d, hour)) {
                freeStudents.add(student[k]);
            }
        }

        //the allele set is composed by all of the students available at given time
        return new GenericAlleleSet<Student>(freeStudents);
    }

    /**
     * Print the calendar according to the individual provided as input
     *
     * @param individual the individual representing the solution to print
     * @param w the week of interest
     * @return the printable string
     */
    private static String prettyPrinter(Individual<ObjectChromosome> individual, Week w) {

        ObjectChromosome week = individual.getChromosome();

        //print a calendar in this way according to the expected order of genes in chromosome
        /*
         *      | Mon   | Tue   | Wed   | Thu   | Fri   | Sat   | Sun   |
         * --------------------------------------------------------------
         * minH | stud1 |  --   |  --   | stud2 ...
         * ...
         * maxH | stud4 |  --   |  --   | stud1 ...
         * --------------------------------------------------------------
         */

        int magicNumber = 65; // number of '-' to renders the separator line horizontally

        //research of the minH and maxH of a day in the week and print header
        int minH = 24;
        int maxH = 1;

        StringBuilder sb = new StringBuilder();
        sb.append("\t"); //reserve space for first column (row label column)

        Day[] days = w.getDays();
        for (Day d : days) {
            if (minH > d.getStart()) {
                minH = d.getStart();
            }

            if (maxH < d.getEnd()) {
                maxH = d.getEnd();
            }

            //print day header
            sb.append("| ");
            sb.append(d.getPrintableName());
            sb.append("\t");
        }
        sb.append("\n");

        //renders the horizontal separation line
        for (int i = 0; i < magicNumber; i++) {
            sb.append("-");
        }
        sb.append("\n");

        //renders the rows in the table
        int row = 0;
        for (int i = minH; i < maxH; i++, row++) {
            //print time column
            sb.append(i);
            sb.append("\t| ");

            //check for all days at this time who is selected for task
            int offset = row;
            for (Day d : days) {
                if (d.isWorkingHour(i)) {
                    int idx = offset;

                    Gene gene = week.getGene(idx);
                    Object value = gene.getValue();
                    sb.append(value.toString());

                } else {
                    sb.append(Student.Nobody.NAME);
                }
                sb.append("\t| ");
                offset += d.getHourInDay();
            }

            sb.append("\n");
        }

        //renders the last separation line under the table
        for (int i = 0; i < magicNumber; i++) {
            sb.append("-");
        }
        sb.append("\n");

        return sb.toString();
    }
}
