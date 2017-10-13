package com.akilommar.rpi3_androidthings_basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageSwitcher;
import android.app.ActionBar.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private ImageSwitcher LedSwitcher;
    private ImageView rpi3Image;
    private TextView LogCatText;

    /* Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            LogCatText = (TextView) findViewById(R.id.logcat_text);
            LogCatText.setMovementMethod(new ScrollingMovementMethod());
            LogCatText.setText("**** LOG CAT LOG DISPLAYED HERE *******");
            LogCatText.append(log.toString());

        } catch (IOException e) {
            // Handle Exception
        }


        // rpi3 pinout image
        rpi3Image = (ImageView) findViewById(R.id.pi3_pines_image_view);
        rpi3Image.setImageResource(R.drawable.pinout_raspberrypi);

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

    /* rpi3 GPIO when state changes to on
        - Need to change the on_off_switch to on
        - Need to set the image in ImageSwitcher with Led_On_Image
    */


    /* rpi3 GPIO when state changes to off
        - Need to change the on_off_switch to off
        - Need to set the image in ImageSwitcher with Led_Off_Image
    */
}
