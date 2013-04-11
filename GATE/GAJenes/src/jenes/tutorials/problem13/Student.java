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
 * Rappresent an abstraction of a student who is bound. A student has an agenda
 * that must be mapped by overriding the method isBusy()
 * 
 * @since 2.1
 */
public abstract class Student {
    /**
     * Name of the student
     */
    protected String name;

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method maps the student's agenda, it tells us if the student is busy
     * or not
     *
     * @param day the day of the week in which we check if the student is busy
     * @param hour the hour of the day in which we check if the student is busy.
     * This parameter must be in the interval 0-24
     * @return true if the student is busy in that day at the specified hour,
     * else return false
     */
    public abstract boolean isBusy(Day day, int hour);

    /**
     * This class represents the student who never is busy or the concept that
     * nobody does something
     *
     * @author Roberto Falzarano <robertofalzarano@gmail.com>
     *
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Default class for "nobody" 
     */
    public static final class Nobody extends Student {

        public final static String NAME = "-";

        public Nobody() {
            super(Nobody.NAME);
        }

        public boolean isBusy(Day day, int hour) {
            return false; //he is free!
        }
    }
}
