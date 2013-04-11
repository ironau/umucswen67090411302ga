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

/**
 * This class represent a Day of the week. Each day has a single range of hours
 * in witch is possibile to perform tasks.
 *
 * @since 2.1
 */
public enum Day {

    MONDAY("Mon", 9, 21),
    TUESDAY("Tue", 9, 21),
    WEDNESDAY("Wed", 9, 21),
    THURSDAY("Thu", 9, 21),
    FRIDAY("Fri", 9, 21),
    SATURDAY("Sat", 9, 21),
    SUNDAY("Sun", 9, 21);
    /**
     * Range admissible for tasks
     */
    private int start, end;
    /**
     * Name of the day
     */
    private String printableName;

    /**
     * Default constructor
     *
     * @param printableName
     * @param start default start hour in the day
     * @param end default end hour in the day
     */
    private Day(String printableName, int start, int end) {
        this.printableName = printableName;
        this.start = start;
        this.end = end;
    }

    public String getPrintableName() {
        return this.printableName;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setRange(int hourStart, int hourEnd) {
        assert hourStart <= hourEnd
                && hourStart > 0 && hourStart <= 24
                && hourEnd > 0 && hourEnd <= 24 : "Invalid time range!";
        this.start = hourStart;
        this.end = hourEnd;
    }

    public int getHourInDay() {
        return this.end - start;
    }
    
    public boolean isWorkingHour(int hour) {
        return hour >= start && hour <= end;
    }

    @Override
    public String toString() {
        return this.getPrintableName();
    }
}
