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
 * This class represents the abstraction of a week
 *
 * @since 2.1
 */
public class Week {

    /** The days considered in the week. By default all days are considered */
    private Day[] days;
    /** All hours in the week */
    private int weekHours;

    /**
     * Creates the whole week, from Monday to Sunday. The range of hour in wich 
     * is possibile to perform tasks is also uniformed to the range defined as
     * parameter
     *
     * @param hourStart this is the hour in which the days begin.
     * @param hourEnd this is the hour in which the days end
     */
    public Week(int hourStart, int hourEnd) {
        this.days = Day.values();
        this.weekHours = 0;

        for(Day d : this.days) {
            d.setRange(hourStart, hourEnd);
            this.weekHours += d.getHourInDay();
        }
    }

    /**
     * Default constructor
     * 
     */
    public Week() {
        this.days = Day.values();
        this.weekHours = 0;

        for(Day d : this.days) {
            this.weekHours += d.getHourInDay();
        }
    }

    public Day[] getDays() {
        return this.days;
    }
    
    public Day getDay(int index) {
        return this.days[index];
    }

    public int getDayCount() {
        return this.days.length;
    }

    public int getWeekHours() {
        return this.weekHours;
    }
}
