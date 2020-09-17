package com.example.vibescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;


public class MainActivity extends AppCompatActivity {


    private static File gpxWriteFile;

    private Button startTrackingBtn;
    private Button stopTrackingBtn;
    private Button showOnMapBtn;
    private Button uploadFileBtn;
    private String fileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTrackingBtn = (Button) findViewById(R.id.startTrackingBtn);
        stopTrackingBtn = (Button) findViewById(R.id.stopTrackingBtn);
        showOnMapBtn = (Button) findViewById(R.id.showTrackBtn);
        uploadFileBtn = (Button) findViewById(R.id.uploadFileBtn);
        stopTrackingBtn.setEnabled(false);

        int newFileNum = getExternalCacheDir().listFiles().length + 1;
        gpxWriteFile = new File(getExternalCacheDir(), "trackInfo" + newFileNum + ".gpx");

        startTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });
        stopTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });
        showOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processFile();

            }
        });
        uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();

            }
        });

    }

    public static File getFile() {
        return gpxWriteFile;
    }

    private void startTracking() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        stopTrackingBtn.setEnabled(true);
        Log.d("INFOO", "CLICK");
    }

    private void stopTracking() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        stopTrackingBtn.setEnabled(false);
    }

    private void processFile() {
        FileChooser fileChooser = new FileChooser(MainActivity.this);

        fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {

                String filename = file.getAbsolutePath();
                Log.d("File path", filename);
                Log.d("File Name", filename);
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("filePath", filename);
                startActivity(intent);
            }
        }).showDialog();

        // Set up and filter files with the provided extension
        fileChooser.setExtension("gpx");
        fileChooser.showDialog();
    }

    private void chooseFile() {

        FileChooser fileChooser = new FileChooser(MainActivity.this);
        fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {

                fileLocation = file.getAbsolutePath();
                startUploading();
            }
        }).showDialog();

        // Set up and filter files with the provided extension
        fileChooser.showDialog();
    }

    private void startUploading() {
        Toast toast = Toast.makeText(getApplicationContext(), "File is uploading...", Toast.LENGTH_SHORT);
        toast.show();
        Intent serviceIntent = new Intent(this, UploadingService.class);
        serviceIntent.putExtra("filePath", fileLocation);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}