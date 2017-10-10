package com.akilommar.rpi3_androidthings_basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageSwitcher;
import android.app.ActionBar.LayoutParams;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ImageSwitcher LedSwitcher;

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
