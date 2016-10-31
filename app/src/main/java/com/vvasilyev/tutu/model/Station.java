package com.vvasilyev.tutu.model;

/**
 * Model for Station object
 */
public class Station extends SimpleStation{

    public String region;

    public static Builder stationBuilder() {
        return new Station().new Builder();
    }

    public class Builder {

        public Station build() {
            return Station.this;
        }

        public Station.Builder id(long id) {
            Station.this.id = id;
            return this;
        }

        public Station.Builder name(String name) {
            Station.this.name = name;
            return this;
        }

        public Station.Builder city(String city) {
            Station.this.city = city;
            return this;
        }

        public Station.Builder country(String country) {
            Station.this.country = country;
            return this;
        }

        public Station.Builder region(String region) {
            Station.this.region = region;
            return this;
        }

        public Station.Builder type(int type) {
            Station.this.type = type;
            return this;
        }
    }
}
