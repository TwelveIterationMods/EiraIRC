package net.blay09.mods.eirairc.api.config;

/**
 * A single property within an IConfigManager.
 * @param <T> the type of this property
 */
public interface IConfigProperty<T> {

    /**
     * @return the name of this property
     */
    String getName();

    /**
     * @return the category of this property
     */
    String getCategory();

    /**
     * @return the default value of this property
     */
    T getDefaultValue();

    /**
     * @return the current value of this property
     */
    T get();

    /**
     * Sets the value of this property to the given value
     * @param value the new value for this property
     */
    void set(T value);

    /**
     * String-property only. Sets the valid values of this property for the config GUI.
     * @param validValues array of valid strings
     */
    void setValidValues(String[] validValues);

    /**
     * Number-property only. Sets the valid range of values of this property for the config GUI.
     * @param min the minimum value of this property
     * @param max the maximum value of this property
     */
    void setMinMax(T min, T max);

}
