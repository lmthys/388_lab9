package netid.iastate.edu.messenger.Interfaces;

/**
 * This interface is used for sending values back to the FlashActivity from the FlashModel class
 */
public interface SensorUpdateCallback {
    /**
     * This method is called when a color has been sensed within the FlashModel in order to
     * pass the color seen to the FlashActivity
     *
     * @param color is a string value of the color that was sensed ("BLACK", "WHITE", or "RED")
     */
    void onColorSensed(String color);

    void update(String value);

    /**
     * This method is used to constantly return the sensed value from the FlashModel to the
     * FlashActivity. This is so we can see the value on the UI so we can test it.
     *
     * @param value is the float light value that was sensed within the onSensorChanged method
     */
    void testingValue(float value);
}
