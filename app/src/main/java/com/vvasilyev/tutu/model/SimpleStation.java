package com.vvasilyev.tutu.model;

import java.io.Serializable;

/**
 * Lightweight Model for SimpleStation object. Contains data enough to display in a list
 *
 */
public class SimpleStation implements Serializable, StationType {

    protected int type;

    public long id;

    public String name;

    public String city;

    public String country;

    public boolean single;

    public SimpleStation() {
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleStation station = (SimpleStation) o;

        if (type != station.type) return false;
        if (id != station.id) return false;
        if (single != station.single) return false;
        if (name != null ? !name.equals(station.name) : station.name != null) return false;
        if (city != null ? !city.equals(station.city) : station.city != null) return false;
        return country != null ? country.equals(station.country) : station.country == null;

    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (single ? 1 : 0);
        return result;
    }

    public static Builder builder() {
        return new SimpleStation().new Builder();
    }

    public class Builder {

        public SimpleStation build() {
            return SimpleStation.this;
        }

        public Builder id(long id) {
            SimpleStation.this.id = id;
            return this;
        }

        public Builder name(String name) {
            SimpleStation.this.name = name;
            return this;
        }

        public Builder city(String city) {
            SimpleStation.this.city = city;
            return this;
        }

        public Builder country(String country) {
            SimpleStation.this.country = country;
            return this;
        }

        public Builder type(int type) {
            SimpleStation.this.type = type;
            return this;
        }
    }
}
