/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.statistics;

import jenes.statistics.StatisticsLogger.LoggableStatistics;

/**
 *
 * @author ironau
 */
    /**
     * This class provides some basic statistics regarding the algorithm
     * execution.
     */
public class Statistics extends LoggableStatistics {

    private long startTime;
    private long stopTime;
    private long initTime;
    private long executionTime;
    private int generations;
    private int generationLimit;
    private long[] generationEndTimes;
    private boolean exceptionTerminated;
    private int fitnessEvaluationNumbers;
    private long[] fitnessEvalStageBegin;
    private long[] fitnessEvalStageEnd;
    private long timeSpentInFitnessEval;
    private long randomSeed;
    private double maxValue;
    private double minValue;
    private double averageValue;
    
    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(double averageValue) {
        this.averageValue = averageValue;
    }


    /**
     * Constructs a new GeneticAlgorithm.Statistics.
     * <p>
     *
     * @param generationLimit
     *            the max number of generations
     */
    public Statistics(final int generationLimit) {
        this.generationLimit = generationLimit;
        this.generationEndTimes = new long[generationLimit];
        this.fitnessEvalStageBegin = new long[generationLimit];
        this.fitnessEvalStageEnd = new long[generationLimit];
        this.exceptionTerminated = false;
    }

    /**
     * Returns the algorithm starting time.
     * <p>
     *
     * @return the starting time expressed in milliseconds
     */
    public final long getStartTime() {
        return this.startTime;
    }

    /**
     * Returns the algorithm stoppping time.
     * <p>
     *
     * @return the stopping time expressed in milliseconds
     */
    public final long getStopTime() {
        return this.stopTime;
    }

    /**
     * Returns the algorithm init time.
     * <p>
     *
     * @return the init time expressed in milliseconds
     */
    public final long getInitTime() {
        return this.initTime;
    }

    /**
     * Returns the algorithm execution time.
     * <p>
     *
     * @return the algorithm execution time expressed in milliseconds
     */
    public final long getExecutionTime() {
        return this.executionTime;
    }

    /**
     * Returns the last generation counter.
     * <p>
     *
     * @return the generation counter
     */
    public final int getGenerations() {
        return this.generations;
    }

    /**
     * Returns the algorithm generation limit.
     * <p>
     *
     * @return the generations limit
     */
    public final int getGenerationLimit() {
        return this.generationLimit;
    }

    /**
     * Returns the generation end time of the specified generation.
     * <p>
     *
     * @param gen
     *            the generation with end time is desidered
     * @return the end time of the generation gen
     */
    public final long getGenerationEndTime(final int gen) {
        return this.getGenerationEndTimes()[gen];
    }

    /**
     * Says if an exception terminated the evolution.
     * <p>
     *
     * @return true if an exception terminated the evolution, false
     *         otherwise
     */
    public final boolean isExceptionTerminated() {
        return this.exceptionTerminated;
    }

    /**
     * Returns the fitness evaluation number.
     * <p>
     *
     * @return the fitness evaluation number
     */
    public int getFitnessEvaluationNumbers() {
        return fitnessEvaluationNumbers;
    }

    /**
     * Returns the timestamp of the last fitness evaluation stage begin.
     * <p>
     *
     * @return the starting time expressed in milliseconds
     */
    public long getFitnessEvalStageBegin() {
        int last = this.getGenerations() - 1;
        return last >= 0 ? this.fitnessEvalStageBegin[this.getGenerations() - 1] : -1;
    }

    /**
     * Returns the timestamp (in millisecond) in which the fitness evaluation 
     * has begin for the given generation.
     * <p>
     * 
     * @param the generation to query for starting from <tt>1</tt>
     *
     * @return the starting time timestamp expressed in milliseconds
     */
    public long getFitnessEvalStageBegin(int gen) {
        return this.fitnessEvalStageBegin[gen - 1];
    }

    /**
     * Returns the timestamp of the last fitness evaluation stage end.
     * <p>
     *
     * @return the end time timestamp expressed in milliseconds
     */
    public long getFitnessEvalStageEnd() {
        int last = this.getGenerations() - 1;
        return last >= 0 ? this.fitnessEvalStageEnd[this.getGenerations() - 1] : -1;
    }

    /**
     * Returns the timestamp at wich the fitness evaluation stage has ended
     * for the given generation.
     * <p>
     * @param the generation to query for starting from <tt>1</tt>
     *
     * @return the stopping time expressed in milliseconds
     */
    public long getFitnessEvalStageEnd(int gen) {
        return this.fitnessEvalStageEnd[gen - 1];
    }

    /**
     * Returns the current time spent in fitness evaluation.
     * <p>
     *
     * @return the execution time expressed in milliseconds
     */
    public long getTimeSpentForFitnessEval() {
        return getTimeSpentInFitnessEval();
    }

    /**
     * Returns the random seed used during the execution of the algorithm instance
     * @return 
     * 
     * @see Random#getSeed()
     */
    public long getRandomSeed() {
        return randomSeed;
    }

    /**
     * Copies the statistics data to the target object.
     * <p>
     *
     * @param stats
     *            the statistics object to be filled
     */
    public void copyTo(final Statistics stats) {
        stats.setInitTime(this.getInitTime());
        stats.setStartTime(this.getStartTime());
        stats.setStopTime(this.getStopTime());
        stats.setExecutionTime(this.getExecutionTime());
        stats.setGenerationLimit(this.getGenerationLimit());
        stats.setGenerations(this.getGenerations());
        stats.setFitnessEvaluationNumbers(this.getFitnessEvaluationNumbers());

        stats.setTimeSpentInFitnessEval(this.getTimeSpentInFitnessEval());
        stats.setRandomSeed(this.getRandomSeed());
        for (int gen=0;gen<this.generations;gen++){
            stats.setFitnessEvalStageBegin(gen,this.getFitnessEvalStageBegin(gen));
            stats.setFitnessEvalStageEnd(gen,this.getFitnessEvalStageEnd(gen));
            stats.setGenerationEndTimes(gen,this.getGenerationEndTimes(gen));
        }

    }

    public void setGenerations(int i) {
        generations=i;
    }

    public void setExceptionTerminated(boolean b) {
        exceptionTerminated = b;
    }

    public void setGenerationEndTimes(int generation, long now) {
        getGenerationEndTimes()[generation]=now;
    }

    public void setStartTime(long now) {
        startTime=now;
    }

    public void setRandomSeed(long seed) {
        randomSeed = seed;
    }

    public void setInitTime(long now) {
        initTime = now;
    }

    public void setStopTime(long now) {
        stopTime= now;
    }

    public void setExecutionTime(long l) {
        executionTime=l;
    }

    /**
     * @param generationLimit the generationLimit to set
     */
    public void setGenerationLimit(int generationLimit) {
        this.generationLimit = generationLimit;
    }

    /**
     * @return the generationEndTimes
     */
    public long[] getGenerationEndTimes() {
        return generationEndTimes;
    }

    /**
     * @param generationEndTimes the generationEndTimes to set
     */
    public void setGenerationEndTimes(long[] generationEndTimes) {
        this.generationEndTimes = generationEndTimes;
    }

    /**
     * @param fitnessEvaluationNumbers the fitnessEvaluationNumbers to set
     */
    public void setFitnessEvaluationNumbers(int fitnessEvaluationNumbers) {
        this.fitnessEvaluationNumbers = fitnessEvaluationNumbers;
    }

    /**
     * @param fitnessEvalStageBegin the fitnessEvalStageBegin to set
     */
    public void setFitnessEvalStageBegin(long[] fitnessEvalStageBegin) {
        this.fitnessEvalStageBegin = fitnessEvalStageBegin;
    }

    /**
     * @param fitnessEvalStageEnd the fitnessEvalStageEnd to set
     */
    public void setFitnessEvalStageEnd(long[] fitnessEvalStageEnd) {
        this.fitnessEvalStageEnd = fitnessEvalStageEnd;
    }

    /**
     * @return the timeSpentInFitnessEval
     */
    public long getTimeSpentInFitnessEval() {
        return timeSpentInFitnessEval;
    }

    /**
     * @param timeSpentInFitnessEval the timeSpentInFitnessEval to set
     */
    public void setTimeSpentInFitnessEval(long timeSpentInFitnessEval) {
        this.timeSpentInFitnessEval = timeSpentInFitnessEval;
    }

    public void setFitnessEvalStageBegin(int generation, long now) {
        fitnessEvalStageBegin[generation]=now;
    }

    public void incrementFitnessEvaluationNumbers() {
        fitnessEvaluationNumbers++;
    }
    public void decrementFitnessEvaluationNumbers() {
        fitnessEvaluationNumbers--;
    }

    public void setFitnessEvalStageEnd(int generation, long now) {
        fitnessEvalStageEnd[generation]=now;
    }

    public void addTimeSpentInFitnessEval(long l) {
        timeSpentInFitnessEval+=l;
    }

    private long getGenerationEndTimes(int gen) {
        return generationEndTimes[gen];
    }
}