package com.uom.happycelebrate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.Result;
//import com.koushikdutta.async.future.FutureCallback;
//import com.koushikdutta.ion.Ion;
import com.uom.happycelebrate.data.QRUniqueCode;
import com.uom.happycelebrate.utils.VideoFinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BarcordReader extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private TextureView textureView;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    FirebaseStorage storage;

    private String redirect_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);

        storage = FirebaseStorage.getInstance();


        redirect_page = getIntent().getStringExtra("redirect_page");

        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }














    }


    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BarcordReader.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
//        Toast.makeText(BarcordReader.this,myResult,Toast.LENGTH_SHORT).show();

        QRUniqueCode.qr_code = result.getText();
//        Toast.makeText(BarcordReader.this,"image url :::::::"+QRUniqueCode.qr_code,Toast.LENGTH_LONG).show();
        System.out.println("qr resultttttttttttttttttt:: "+QRUniqueCode.qr_code);
//
        redirect();





//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview(BarcordReader.this);
//            }
//        });
//        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
//                startActivity(browserIntent);
//            }
//        });
//        builder.setMessage(result.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
    }


    private  void  redirect() {


        if (redirect_page.equals("CREATE_CARD")) {


            Toast.makeText(BarcordReader.this, "hereeeeeeeeeee redired", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BarcordReader.this, CreateCard.class);
            startActivity(intent);
            BarcordReader.this.finish();

        } else if (redirect_page.equals("AR_PAGE")) {


//            downloadVideo();
//            downloadImage("");



            new VideoFinder() {
                @Override
                public void getData(DataSnapshot dataSnapshot) {
                    QRUniqueCode.qr_image_url = dataSnapshot.getValue().toString();
                    Toast.makeText(BarcordReader.this,"image url :::::::"+dataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();
//                    redirect();
                    new VideoFinder() {
                        @Override
                        public void getData(DataSnapshot dataSnapshot) {

                            QRUniqueCode.video_url = dataSnapshot.getValue().toString();

                            Toast.makeText(BarcordReader.this,"video url "+dataSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BarcordReader.this, ArActionPage.class);
                            startActivity(intent);
//                            BarcordReader.this.finish();

                        }
                    }.searchData(QRUniqueCode.qr_code);



                }
            }.searchImage(QRUniqueCode.qr_code);

        }


    }


    public void downloadImage(String url) {


        MyAsync obj = new MyAsync(){

            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);

                Bitmap bm = bmp;
                QRUniqueCode.image = bmp;
                System.out.println("sucesssssssssssssssssssssssssssssssssssss");

            }


        };

        obj.execute();

    }






    private void download() {
        Downback DB = new Downback();
        DB.execute("");

    }


    private class Downback extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final String vidurl = "https://firebasestorage.googleapis.com/v0/b/happycelebrate-95543.appspot.com/o/images%2Fvideoplayback.mp4?alt=media&token=8f039acc-66a2-4ff7-85c6-0a2ab70b3cfb";
            downloadfile(vidurl);
            return null;

        }


    }

    private void downloadfile(String vidurl) {

        SimpleDateFormat sd = new SimpleDateFormat("yymmhh");
//        String date = sd.format(new Date());
        String name = "video"  + ".mp4";

            int count;
            try {
                File folder = new File(Environment.getExternalStorageDirectory() + "/myFolder");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    // Do something on success
                } else {
                    // Do something else on failure
                }
                URL url = new URL("https://youtu.be/668nUCeBHyY");
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                //extension must change (mp3,mp4,zip,apk etc.)
                OutputStream output = new FileOutputStream("/sdcard/myFolder/"+"videos"+".mp4");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
//                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Errorrrrrrrrrrrrr: ", e.getMessage());
            }

    }



}



class MyAsync extends AsyncTask<Void, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(Void... params) {

        try {
            URL url = new URL("https://firebasestorage.googleapis.com/v0/b/happycelebrate-95543.appspot.com/o/images%2F971521813.png?alt=media&token=98f2b4a5-2018-4991-9cdb-83ff005b77ba");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}






