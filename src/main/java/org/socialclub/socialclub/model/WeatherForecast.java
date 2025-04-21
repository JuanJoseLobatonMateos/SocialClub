package org.socialclub.socialclub.model;

import java.util.List;

/**
 * La clase WeatherForecast representa el pronóstico del clima para una ciudad específica.
 */
@SuppressWarnings("ALL")
public class WeatherForecast {
    private City city;
    private List<WeatherData> list;

    /**
     * Obtiene la ciudad para la cual se ha realizado el pronóstico del clima.
     *
     * @return la ciudad del pronóstico del clima.
     */
    public City getCity() {
        return city;
    }

    /**
     * Establece la ciudad para la cual se ha realizado el pronóstico del clima.
     *
     * @param city la ciudad del pronóstico del clima.
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * Obtiene la lista de datos del clima.
     *
     * @return la lista de datos del clima.
     */
    public List<WeatherData> getList() {
        return list;
    }

    /**
     * Establece la lista de datos del clima.
     *
     * @param list la lista de datos del clima.
     */
    public void setList(List<WeatherData> list) {
        this.list = list;
    }

    /**
     * La clase City representa una ciudad con un nombre.
     */
    public static class City {
        private String name;

        /**
         * Obtiene el nombre de la ciudad.
         *
         * @return el nombre de la ciudad.
         */
        public String getName() {
            return name;
        }

        /**
         * Establece el nombre de la ciudad.
         *
         * @param name el nombre de la ciudad.
         */
        public void setName(String name) {
            this.name = name;
        }
    }
}