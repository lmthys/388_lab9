package netid.iastate.edu.messenger.Activities;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import netid.iastate.edu.messenger.Interfaces.SensorUpdateCallback;
import netid.iastate.edu.messenger.Models.FlashModel;
import netid.iastate.edu.messenger.R;

public class FlashActivity extends AppCompatActivity implements SensorUpdateCallback {

    /** an instance of the FlashModel class so that we can send and decode messages with it */
    private FlashModel flashModel;

    /** initial refresh rate of the handler */
    private int REFRESH_RATE = -1;
    /** the handler that gets used every so often to change the display background color for a certain amount of time */
    private Handler mHandler;
    /** holds the colors to be displayed/used within the handler */
    private ArrayList<Integer> encodedMessage = new ArrayList<>();
    /** used to keep track of the position within the encodedMessage array list */
    private int messageCounter = 0;
    /** keeps track of the message encodings received ("DOT", "DASH", "END_CHARACTER") */
    private ArrayList<String> messageReceived = new ArrayList<>();

    /** used to mark the current system time of a color change */
    private long rStartTime = 0;
    /** when a message has been detected as done with its transmission, this is set to true to keep the light sensor from sensing other values that wont matter */
    private Boolean endOfMessage = false;
    /** used to keep track of the activity's linear layout so that the background can be set to different colors when need be */
    private LinearLayout activityLayout;
    /** the textView that is used to display the received message */
    private TextView messageReceivedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        activityLayout = findViewById(R.id.activity_flash);//gets the layout reference so the background color can be set when need be within changeScreenColor()
        messageReceivedTextView = findViewById(R.id.flashMessage);//gets the textView that is used to display the received message

        mHandler = new Handler();//initialize the handler that will be used within this class
        flashModel = new FlashModel(this.getApplicationContext(), this);//initialize the flashModel that will be called in this activity
    }

    @Override
    protected void onResume() {
        super.onResume();
        flashModel.start();//register the light sensor listener
    }

    @Override
    protected void onPause() {
        super.onPause();
        flashModel.stop();//unregister the light sensor listener
    }

    /**
     * This method is triggered when the user clicks the send message button. It encodes the user's message and starts transmitting it
     *
     * @param view of the button
     */
    public void sendWithFlashes(View view) {
        EditText toSend = findViewById(R.id.input_layout);
        String messageToSend = toSend.getText().toString();
        if (!messageToSend.equals("")) {//if the user has entered a message, then go ahead

            //start transmission process
            messageCounter = 0;
            REFRESH_RATE = -1;

            // - encode the message and store it in a already defined global variable
            encodedMessage = flashModel.encodeMessage(messageToSend);



            // - start the handler
            mHandler.post(startTimer);
        }
    }

    /**
     * This method is triggered when the user clicks the "be sender" button, this will stop the sensor from retrieving light sensor values
     *
     * @param view of the button
     */
    public void beSender(View view) {
        TextView valueText = findViewById(R.id.valueTesting);
        Button beSenderButton = findViewById(R.id.senderToggleButton);
        Button redButton = findViewById(R.id.color1Button);
        Button blackButton = findViewById(R.id.color2Button);
        Button whiteButton = findViewById(R.id.color3Button);

        if (valueText.getVisibility() == View.INVISIBLE) {
            valueText.setVisibility(View.VISIBLE);
            flashModel.start();
            beSenderButton.setText(R.string.be_server_side);
            redButton.setVisibility(View.INVISIBLE);
            blackButton.setVisibility(View.INVISIBLE);
            whiteButton.setVisibility(View.INVISIBLE);
        } else {
            valueText.setVisibility(View.INVISIBLE);
            flashModel.stop();
            beSenderButton.setText(R.string.be_receiver_side);
            redButton.setVisibility(View.VISIBLE);
            blackButton.setVisibility(View.VISIBLE);
            whiteButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onColorSensed(String color) {

    }

    @Override
    public void update(String value) {
        // - Finish this method's implementation, Make sure you think about what exactly is suppose to happen in here and how you can make sure it happens
        //This method is called whenever a new screen color has been sensed, with that information
        // you can retrieve the screen color pattern seen, the character it represents, and therefore
        // the character received to be appended to the messageReceivedTextView

        String prevColor = "";

        if (!endOfMessage) {//if the end of the message hasn't yet been detected, then see what color has been sensed from the light sensor
            // - if value = "RED", initialize the messageReceived arrayList and clear the messageReceivedTextView
            if(value.equals("RED")){
                messageReceived = new ArrayList<String>();
                messageReceivedTextView.setText("");
                prevColor = "RED";
                Toast.makeText(getApplicationContext(), "red", Toast.LENGTH_SHORT).show();
            }
            // - if value = "BLACK", get the current time and store it into rStartTime
            if(value.equals("BLACK")){
                rStartTime = System.currentTimeMillis();
                prevColor = "BLACK";
                Toast.makeText(getApplicationContext(), "black", Toast.LENGTH_SHORT).show();
            }
            // - if value = "WHITE", find the total time that the screen was black, and add FlashModel.DOT, .DASH, or .END_CHARACTER accordingly to messageReceived arrayList
            if(value.equals("WHITE")){

                long time = System.currentTimeMillis() - rStartTime;
                Toast.makeText(getApplicationContext(),"time "+time, Toast.LENGTH_SHORT).show();
                if (time > 500 && time < 700) {
                    messageReceived.add(flashModel.DASH);
                    Toast.makeText(getApplicationContext(),"Dash", Toast.LENGTH_SHORT).show();
                } else if (time > 200 && time < 450) {
                    messageReceived.add(flashModel.DOT);
                    Toast.makeText(getApplicationContext(),"Dot", Toast.LENGTH_SHORT).show();
                } else if (time > 800 && time < 1000) {
                    messageReceived.add(flashModel.END_CHARACTER);
                    char y = flashModel.decodeCharacterPattern(messageReceived);
                    Toast.makeText(getApplicationContext(),y, Toast.LENGTH_SHORT).show();
                    messageReceivedTextView.append(Character.toString(y));


                }



            }

        }

    }

    @Override
    public void testingValue(float value) {
        TextView valueText = findViewById(R.id.valueTesting);
        valueText.setText(getString(R.string.format_light_val, value));//displays the current light sensor value on the UI
    }

    /**
     * Creates a Runnable startTimer to change the screen background color every so often
     */
    private Runnable startTimer = new Runnable() {
        public void run() {
            changeScreenColor();
            if (REFRESH_RATE == -1) {
                //the initial post delayed is longer to give the user time to get the device into position aligned with the other device's light sensor
                mHandler.postDelayed(this, 2000);
                REFRESH_RATE = 300;//after the initial post delayed of 2 seconds, set the time that the screen is on for each color to 300ms
            } else {//enters this if statement every time besides the first time
                mHandler.postDelayed(this, REFRESH_RATE);
            }
        }
    };

    /**
     * This method is used for changing the color of the screen.
     */
    private void changeScreenColor() {
        // - iterate through the encodedMessage arrayList using messageCounter as the index.
        // If the end of encodedMessage arrayList has not been reached, then set the background color to the current messageCounter index and then increment the counter
        // If the end of encodedMessage arrayList has been reached, then remove callbacks from the handler and reset the REFRESH_RATE to -1
        if(encodedMessage.size() > messageCounter){
            activityLayout.setBackgroundColor(encodedMessage.get(messageCounter));
            messageCounter++;
        }else {
            mHandler.removeCallbacks(startTimer);
            REFRESH_RATE = -1;
        }
    }

    /**
     * Button onclick used for help with testing different screen colors and what sensor values they produce
     *
     * @param view of the button
     */
    public void showRed(View view) {
        TextView valueText = findViewById(R.id.valueTesting);
        if (valueText.getVisibility() == View.INVISIBLE) {
            LinearLayout activityLayout = findViewById(R.id.activity_flash);
            activityLayout.setBackgroundColor(Color.parseColor("#FA0404"));
        }
    }

    /**
     * Button onclick used for help with testing different screen colors and what sensor values they produce
     *
     * @param view of the button
     */
    public void showBlack(View view) {
        TextView valueText = findViewById(R.id.valueTesting);
        if (valueText.getVisibility() == View.INVISIBLE) {
            LinearLayout activityLayout = findViewById(R.id.activity_flash);
            activityLayout.setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    /**
     * Button onclick used for help with testing different screen colors and what sensor values they produce
     *
     * @param view of the button
     */
    public void showWhite(View view) {
        TextView valueText = findViewById(R.id.valueTesting);
        if (valueText.getVisibility() == View.INVISIBLE) {
            LinearLayout activityLayout = findViewById(R.id.activity_flash);
            activityLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }
}
