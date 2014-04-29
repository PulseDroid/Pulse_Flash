package com.pulsedroid.pulseflash;
 
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

public class MainActivity extends SlidingActivity {
 
    ImageButton btnSwitch;
 
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;
    MediaPlayer mp;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //About sliding menu
        
        setBehindContentView(R.layout.activity_menu);
       
        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);
        sm.setBehindOffset(150);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
       
        // Flashlight
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
 
     
        // Diagnostic      
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
 
        if (!hasFlash) {
            // Show alert
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Whoopsie");
            alert.setMessage("Poop, your device doesn't support flashlight!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // To close app after alert:
                    // finish();
                }
            });
            alert.show();
            return;
        }
 
        // Initiate camera
        getCamera();
         
        // Display button
        toggleButtonImage();
         
         
        // What happens when button clicks
        btnSwitch.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    turnOnFlash();
                }
            }
        });
    }
 
     
    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }
 
     
     // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            
             
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
             
            // changing button/switch image
            toggleButtonImage();
        }
 
    }
 
 
    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
           
             
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
             
            // changing button/switch image
            toggleButtonImage();
        }
    }
     
 
  
     
    // Toggle switch button images
     
     
    private void toggleButtonImage(){
        if(isFlashOn){
            btnSwitch.setImageResource(R.drawable.btn_switch_on);
        }else{
            btnSwitch.setImageResource(R.drawable.btn_switch_off);
        }
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
 
    @Override
    protected void onPause() {
        super.onPause();
         
        // Turn off flash when out of app
        turnOffFlash();
    }
 
    @Override
    protected void onRestart() {
        super.onRestart();
    }
 
    @Override
    protected void onResume() {
        super.onResume();
         
        // Turn on flash when back in app
        if(hasFlash)
            turnOnFlash();
    }
 
    @Override
    protected void onStart() {
        super.onStart();
         
        // Get camera info 
        getCamera();
    }
 
    @Override
    protected void onStop() {
        super.onStop();
         
        // Release camera when exit
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
 
}