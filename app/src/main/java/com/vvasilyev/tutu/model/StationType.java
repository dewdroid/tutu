package com.vvasilyev.tutu.model;

/**
 * Represents type of Station
 */

public interface StationType {

    /**
     * Corresponds to station (one of many) within a city
     */
    int STATION_IN_CITY = 0;

    /**
     * Corresponds to "All stops" station (i.e. City)
     */
    int CITY = 1;

    /**
     * Corresponds to single (no other stations available/known) station within a city
     */
    int STATION_SINGLE = 2;

    int getType();
}
