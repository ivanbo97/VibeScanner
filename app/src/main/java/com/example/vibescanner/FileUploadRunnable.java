package com.example.vibescanner;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadRunnable implements Runnable {

    private String fileLocation;
    private Service serviceRef;
    private Context mainActivity;
    public FileUploadRunnable(String fileLocation,Service serviceRef,Context mainActivity) {
        this.fileLocation = fileLocation;
        this.serviceRef = serviceRef;
        this.mainActivity =  mainActivity;
    }
    @Override
    public void run() {

        File fileStream = new File (fileLocation);
        String content_type ="application/octet-stream";
        Log.d("Content type!!!!!!!",content_type);
        String file_path = fileStream.getAbsolutePath();
        Log.d("Absolute path!!!!",file_path);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES);

        OkHttpClient client = builder.build();

        RequestBody file_body = RequestBody.create(MediaType.parse(content_type),fileStream);

        RequestBody request_body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type",content_type)
                .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.43.208/GPXUploads/save_file.php")
                .post(request_body)
                .build();

        try {
            Log.d("Response","!!!");
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                Handler h = new Handler(mainActivity.getMainLooper());

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity,"The file has been successfully uploaded!!!",Toast.LENGTH_LONG).show();

                    }
                });

            }
            else{
                response.close();
                throw new IOException("Error : "+response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        serviceRef.stopSelf();
    }
}
