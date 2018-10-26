package netid.iastate.edu.messenger.Models;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import netid.iastate.edu.messenger.Interfaces.SensorUpdateCallback;

public class FlashModel implements SensorEventListener {

    /** The Android sensor manager */
    private SensorManager mSensorManager;
    /** The device's light sensor */
    private Sensor lightSensor;
    /** The instance registered to receive color sense events from this class */
    private SensorUpdateCallback mCallback;
    /** Used for data encoding and decoding */
    public static final String DOT = "DOT";
    /** Used for data encoding and decoding */
    public static final String DASH = "DASH";
    /** Used for data encoding and decoding */
    public static final String END_CHARACTER = "END_CHARACTER";

    /** value of the screen color to be used for data transmission of start of message */
    private static int RED = Color.parseColor("#FA0404");
    /** value of the screen color to be used for data transmission of characters */
    private static int WHITE = Color.parseColor("#FFFFFF");
    /** value of the screen color to be used for data transmission of characters */
    private static int BLACK = Color.parseColor("#000000");

    /** sets the initial previous light value seen to -10 and then keeps track of the last seen light value */
    private int prevLightValue = -10;
    /** keeps track if a message has started to be received or not */
    private Boolean messageHasStarted = false;


    /**
     * Constructor
     *
     * @param context  the Activity hosting this class
     * @param callback the instance of a class that will receive flash sense events
     */
    public FlashModel(Context context, SensorUpdateCallback callback) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); // Get the Sensor Service using the application context
        if (mSensorManager != null) {
            lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // Get a light sensor
        }
        mCallback = callback;//set where to send the callback method calls to
    }

    /**
     * Register listener
     */
    public void start() {
        // Register listener
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Unregister listener
     */
    public void stop() {
        // Unregister listener
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if the sensed value is from the light sensor, enter and do more logic
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            mCallback.testingValue(event.values[0]);//use callback to return the raw light sensor value to help with development/testing

            //check to see if the sensed light value is a certain color
            // - if its red, then use the callback with update("RED") and set the messageHasStarted accordingly
            if((event.values[0] > 10 && event.values[0] < 30 )&& prevLightValue != RED){
                mCallback.update("RED");
                messageHasStarted = true;
                prevLightValue = RED;
            }
            // - if its white, then use the callback with update("WHITE")
            if((event.values[0] > 0 && event.values[0] < 10 ) && prevLightValue != WHITE){
                mCallback.update("WHITE");
                prevLightValue = WHITE;
            }
            // - if its black, then use the callback with update("BLACK")
            if((event.values[0] > 100 && event.values[0] < 150 ) && prevLightValue != BLACK){
                mCallback.update("BLACK");
                prevLightValue = BLACK;
            }

            // - don't forget to use and then also set the prevLightValue accordingly
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing for this within our app
    }

    /**
     * This method is used for encoding a given string into morris code
     *
     * @param message String to be encoded
     * @return ArrayList of background color hex values that were assigned using the input String
     */
    public ArrayList<Integer> encodeMessage(String message) {
        // - encode the input string to an arrayList of DOTs,DASHes, and END_CHARACTERs to be matched up with colors, the first character encoding is done for you

        message = message.toLowerCase();//make the input string to lower case so it can be seen in the lookup table
        ArrayList<String> encodedMessage = new ArrayList<>();//create a new empty arrayList to put DOTs DASHes and END_CHARCTERs into
        for (int i = 0; i < message.length(); i++) {//for each character in the input string, append its encoding to the encodedMessage arrayList
            if (message.charAt(i) == 'a') {
                encodedMessage.addAll(Arrays.asList(DOT, DASH, END_CHARACTER));
            } else if (message.charAt(i) == 'b') {
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DOT,DOT, END_CHARACTER));
            }else if (message.charAt(i) == 'c'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,DOT, END_CHARACTER));
            }else if (message.charAt(i) == 'd'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == 'e'){
                encodedMessage.addAll(Arrays.asList(DOT,END_CHARACTER));
            }else if (message.charAt(i) == 'f'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DASH,DOT, END_CHARACTER));
            }else if (message.charAt(i) == 'g'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,DOT, END_CHARACTER));
            }else if (message.charAt(i) == 'h'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == 'i'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == 'j'){
                encodedMessage.addAll(Arrays.asList(DOT,DASH,DASH,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'k'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'l'){
                encodedMessage.addAll(Arrays.asList(DOT,DASH,DOT, DOT, END_CHARACTER));
            }else if (message.charAt(i) == 'm'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'n'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,END_CHARACTER));
            }else if (message.charAt(i) == 'o'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'p'){
                encodedMessage.addAll(Arrays.asList(DOT,DASH,DASH, DOT,END_CHARACTER));
            }else if (message.charAt(i) == 'q'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH, DOT, DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'r'){
                encodedMessage.addAll(Arrays.asList(DOT,DASH,DOT,END_CHARACTER));
            }else if (message.charAt(i) == 's'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == 't'){
                encodedMessage.addAll(Arrays.asList(DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'u'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'v'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'w'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'x'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'y'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == 'z'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == '1'){
                encodedMessage.addAll(Arrays.asList(DOT,DASH,DASH,DASH,DASH,END_CHARACTER));
            }else if (message.charAt(i) == '2'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DASH,DASH,DASH,END_CHARACTER));
            }else if (message.charAt(i) == '3'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DOT,DASH,DASH,END_CHARACTER));
            }else if (message.charAt(i) == '4'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DOT,DOT,DASH,END_CHARACTER));
            }else if (message.charAt(i) == '5'){
                encodedMessage.addAll(Arrays.asList(DOT,DOT,DOT,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == '6'){
                encodedMessage.addAll(Arrays.asList(DASH,DOT,DOT,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == '7'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,DOT,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == '8'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,DASH,DOT,DOT,END_CHARACTER));
            }else if (message.charAt(i) == '9'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,DASH,DASH,DOT,END_CHARACTER));
            }else if (message.charAt(i) == '0'){
                encodedMessage.addAll(Arrays.asList(DASH,DASH,DASH,DASH,DASH,END_CHARACTER));
            }
            //..
        }

        //encode to colors
        //For each item in the encodedMessage arrayList created directly above, assign a color message encoding to it
        ArrayList<Integer> colorMessageEncoding = new ArrayList<>(Arrays.asList(RED, RED));
        for (String item : encodedMessage) {
            if (item.equals(DASH)) {//DASHes are on the black screen for 2X time
                colorMessageEncoding.add(BLACK);
                colorMessageEncoding.add(BLACK);
                colorMessageEncoding.add(WHITE);
            } else if (item.equals(DOT)) {//DOTs are on the black screen for 1X time
                colorMessageEncoding.add(BLACK);
                colorMessageEncoding.add(WHITE);
            } else if (item.equals(END_CHARACTER)) {//END_CHARACTERs are on the black screen for 3X time
                colorMessageEncoding.add(BLACK);
                colorMessageEncoding.add(BLACK);
                colorMessageEncoding.add(BLACK);
                colorMessageEncoding.add(WHITE);
            }
        }

        //return the encoded color message arrayList
        return colorMessageEncoding;

    }

    /**
     * Used to decode an arrayList of patterns seen and output the corresponding character that the pattern represents
     *
     * @param patternList of DOTS, DASHES, and END_CHARACTERS
     * @return the character that corresponds to the input patternList
     */
    public char decodeCharacterPattern(ArrayList<String> patternList) {
        // - decode the arrayList of patterns seen and output the corresponding character that the pattern represents, the first character is done for you
        if (patternList.equals(Arrays.asList(DOT, DASH, END_CHARACTER))) {
            return 'a';
        } else if (patternList.equals(Arrays.asList(DASH,DOT,DOT,DOT, END_CHARACTER))) {
            return 'b';
        } else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,DOT, END_CHARACTER))){
           return 'c';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DOT,END_CHARACTER))){
            return 'd';
        }else if (patternList.equals(Arrays.asList(DOT,END_CHARACTER))){
            return 'e';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DASH,DOT, END_CHARACTER))){
            return 'f';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,DOT, END_CHARACTER))){
            return 'g';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DOT,DOT,END_CHARACTER))){
            return 'h';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,END_CHARACTER))){
            return 'i';
        }else if (patternList.equals(Arrays.asList(DOT,DASH,DASH,DASH,END_CHARACTER))){
            return 'j';
        }else if (patternList.equals(Arrays.asList(DOT,DASH,DASH,DASH,END_CHARACTER))){
            return 'k';
        }else if (patternList.equals(Arrays.asList(DOT,DASH,DOT, DOT, END_CHARACTER))){
            return 'l';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,END_CHARACTER))){
            return 'm';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,END_CHARACTER))){
            return 'n';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,DASH,END_CHARACTER))){
            return 'o';
        }else if (patternList.equals(Arrays.asList(DOT,DASH,DASH, DOT,END_CHARACTER))){
            return 'p';
        }else if (patternList.equals(Arrays.asList(DASH,DASH, DOT, DASH,END_CHARACTER))){
            return 'q';
        }else if (patternList.equals(Arrays.asList(DOT,DASH,DOT,END_CHARACTER))){
            return 'r';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DOT,END_CHARACTER))){
            return 's';
        }else if (patternList.equals(Arrays.asList(DASH,END_CHARACTER))){
            return 't';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,END_CHARACTER))){
            return 'u';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,END_CHARACTER))){
            return 'v';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,END_CHARACTER))){
            return 'w';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,END_CHARACTER))){
            return 'x';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,END_CHARACTER))){
            return 'y';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DASH,END_CHARACTER))){
            return 'z';
        }else if (patternList.equals(Arrays.asList(DOT,DASH,DASH,DASH,DASH,END_CHARACTER))){
            return '1';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DASH,DASH,DASH,END_CHARACTER))){
            return '2';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DOT,DASH,DASH,END_CHARACTER))){
            return '3';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DOT,DOT,DASH,END_CHARACTER))){
            return '4';
        }else if (patternList.equals(Arrays.asList(DOT,DOT,DOT,DOT,DOT,END_CHARACTER))){
            return '5';
        }else if (patternList.equals(Arrays.asList(DASH,DOT,DOT,DOT,DOT,END_CHARACTER))){
            return '6';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,DOT,DOT,DOT,END_CHARACTER))){
            return '7';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,DASH,DOT,DOT,END_CHARACTER))){
            return '8';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,DASH,DASH,DOT,END_CHARACTER))){
            return '9';
        }else if (patternList.equals(Arrays.asList(DASH,DASH,DASH,DASH,DASH,END_CHARACTER))){
            return '0';
        }
        //else if...
        //..

        else {//unknown
            return '*';
        }
    }
}
