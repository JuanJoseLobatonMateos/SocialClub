package org.socialclub.socialclub.model;

/**
 * La clase WeatherData representa los datos meteorológicos para una ciudad específica.
 */
@SuppressWarnings("ALL")
public class WeatherData {
    private String name; // Nombre de la ciudad
    private Main main;
    private Weather[] weather;
    private long dt; // Fecha y hora de la medición

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

    /**
     * Obtiene los datos principales del clima.
     *
     * @return los datos principales del clima.
     */
    public Main getMain() {
        return main;
    }

    /**
     * Establece los datos principales del clima.
     *
     * @param main los datos principales del clima.
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Obtiene la lista de condiciones climáticas.
     *
     * @return la lista de condiciones climáticas.
     */
    public Weather[] getWeather() {
        return weather;
    }

    /**
     * Establece la lista de condiciones climáticas.
     *
     * @param weather la lista de condiciones climáticas.
     */
    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    /**
     * Obtiene la fecha y hora de la medición.
     *
     * @return la fecha y hora de la medición.
     */
    public long getDt() {
        return dt;
    }

    /**
     * Establece la fecha y hora de la medición.
     *
     * @param dt la fecha y hora de la medición.
     */
    public void setDt(long dt) {
        this.dt = dt;
    }

    /**
     * La clase Main representa los datos principales del clima, como la temperatura y la humedad.
     */
    public static class Main {
        private double temp;
        private int humidity;

        /**
         * Obtiene la temperatura.
         *
         * @return la temperatura.
         */
        public double getTemp() {
            return temp;
        }

        /**
         * Establece la temperatura.
         *
         * @param temp la temperatura.
         */
        public void setTemp(double temp) {
            this.temp = temp;
        }

        /**
         * Obtiene la humedad.
         *
         * @return la humedad.
         */
        public int getHumidity() {
            return humidity;
        }

        /**
         * Establece la humedad.
         *
         * @param humidity la humedad.
         */
        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }

    /**
     * La clase Weather representa las condiciones climáticas, como la descripción y el ícono.
     */
    public static class Weather {
        private String description;
        private String icon; // Ícono del clima

        /**
         * Obtiene la descripción del clima.
         *
         * @return la descripción del clima.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Establece la descripción del clima.
         *
         * @param description la descripción del clima.
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Obtiene el ícono del clima.
         *
         * @return el ícono del clima.
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Establece el ícono del clima.
         *
         * @param icon el ícono del clima.
         */
        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}