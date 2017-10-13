package com.akilommar.rpi3_androidthings_basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.app.ActionBar.LayoutParams;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.View;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private Switch PushButtonSwitch;
    private ImageSwitcher LedSwitcher;
    private ImageView rpi3Image;
    public EditText LogCatText;

    // GPIO var for push button input
    private static final String BUTTON_PIN_NAME = "BCM27";
    private Gpio mButtonGpio;

    // GPIO var for LED output
    private static final String LED_PIN_NAME = "BCM22";
    private Gpio mLedGpio;

    public PeripheralManagerService PeripheralService = new PeripheralManagerService();

    /* Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Push button switch
        PushButtonSwitch = (Switch) findViewById(R.id.on_off_pushbutton);
        PushButtonSwitch.setChecked(false);

        // Led Image Viewer
        LedSwitcher = (ImageSwitcher) findViewById(R.id.led_image_switcher);
        LedSwitcher.setFactory(new ViewFactory(){
                        @Override
                        public View makeView() {
                            ImageView myView = new ImageView(getApplicationContext());
                            myView.setLayoutParams(new
                                    ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                            myView.setImageResource(R.drawable.led_off);
                            return myView;
                        }
        });

        // Text View for log
        try {
            Process process = Runtime.getRuntime().exec("logcat -d akilommar");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            LogCatText = (EditText) findViewById(R.id.logcat_text);
            LogCatText.setMovementMethod(new ScrollingMovementMethod());
            LogCatText.setText("**** LOG CAT LOG DISPLAYED HERE *******\n\n");
            LogCatText.append(log.toString());

        } catch (IOException e) {
            // Handle Exception
            Log.d("LOGCAT","Error creating object to read from logcat", e);
        }

        // rpi3 pinout image
        rpi3Image = (ImageView) findViewById(R.id.pi3_pines_image_view);
        rpi3Image.setImageResource(R.drawable.pinout_raspberrypi);

        // Check all peripheral on raspberryPi3 and add it to log cat
        try {
            LogCatText.append("\n\n***** PERIPHERAL AVAILABLE GPIO PI 3 ******\n\n");
            LogCatText.append(PeripheralService.getGpioList().toString() + "\n");
        }
        catch (RuntimeException e){
            LogCatText.append("PI3 ERROR PERIPHERAL: " + e.toString());
        }

        /*
        Initialize GPIO Connection with raspberry pi 3, using BPM2 as pushbutton input
        and BPM3 as led output
        */
        try {
            // Create GPIO connection.
            mButtonGpio = PeripheralService.openGpio(BUTTON_PIN_NAME);
            mLedGpio = PeripheralService.openGpio(LED_PIN_NAME);

            // PUSH BUTTON ON BCM27
            // Configure as an input, trigger events on every change.
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            // Value is true when the pin is LOW
            mButtonGpio.setActiveType(Gpio.ACTIVE_LOW);
            // Register the event callback.
            mButtonGpio.registerGpioCallback(mCallback);

            // LED ON BCM22
            // Configure as an output and with initial value of low
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        } catch (IOException e) {
            LogCatText.append("PI3 ERROR GPIO: " + e.toString());
        }

        /* Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        */

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     public native String stringFromJNI();
     */

    /* Declare a GpioCallback to receive edge trigger events.
       rpi3 GPIO when state changes to on
        - Need to change the on_off_switch to on
        - Need to set the image in ImageSwitcher with Led_On_Image
       rpi3 GPIO when state changes to off
        - Need to change the on_off_switch to off
        - Need to set the image in ImageSwitcher with Led_Off_Image
    */
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                boolean buttonValue = gpio.getValue();
                PushButtonSwitch.setChecked(buttonValue);
                mLedGpio.setValue(buttonValue);
                if (buttonValue) {
                    LedSwitcher.setImageResource(R.drawable.green_led_on);
                    LogCatText.append("PI3 LED STATUS CHANGE: SUCCESS ON\n");
                }
                else{
                    LedSwitcher.setImageResource(R.drawable.led_off);
                    LogCatText.append("PI3 LED STATUS CHANGE: SUCCESS OFF\n");
                }
            } catch (IOException e) {
                LogCatText.append("PI3 ERROR GPIO: " + e.toString());
            }
            // Return true to keep callback active.
            return true;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the LED.
        if (mLedGpio != null) {
            try {
                mLedGpio.close();
            } catch (IOException e) {
                LogCatText.append("PI3 ERROR CLOSING GPIO: " + e.toString());
            }
        }
    }
}
