package com.uom.happycelebrate;


import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.uom.happycelebrate.data.QRUniqueCode;
import com.uom.happycelebrate.utils.CustomArFragment;

import java.io.IOException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.android.material.snackbar.Snackbar.*;
import static com.google.android.material.snackbar.Snackbar.LENGTH_LONG;

public class ArActionPage extends AppCompatActivity {

    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private CustomArFragment arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;
    private ConstraintLayout mainContainer;
    private ProgressBar progressBar;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekBarPositionUpdateTask;

    private BottomNavigationView bottomNavigationView;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_action_page);
        mediaPlayer = new MediaPlayer();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        new Thread(() -> {

            try {
                mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/awsomemurals.appspot.com" + "/o/markers%2Fvideoplayback.mp4?alt=media&token=2720204e-0839-4e4f-9f81-3529e76b27e5");
//                mediaPlayer.setDataSource(QRUniqueCode.video_url);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();



            } catch (Exception e) {
                e.printStackTrace();
            }
            mainContainer = findViewById(R.id.mainContainer);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fg1, new LoadingAssetsFragment());
            ft.commit();

            String skoda = "https://firebasestorage.googleapis.com/v0/b/awsomemurals.appspot.com/o/markers%2Fimage.jpg?alt=media&token=5dc9afb8-e04d-4cbf-95ef-534b8112e5e5";
//            String qr= "https://firebasestorage.googleapis.com/v0/b/awsomemurals.appspot.com/o/markers%2Fqr.jpg?alt=media&token=a1ff7b6f-95ee-4c17-a9c7-cc1ab74ac09f";
            String danceMonkey = "https://firebasestorage.googleapis.com/v0/b/awsomemurals.appspot.com/o/markers%2Fmaxresdefault.jpg?alt=media&token=6fb5700d-0e9f-48b6-ae03-1f94242a8a3f";
            String qr = "https://images-na.ssl-images-amazon.com/images/I/41KKcdSUEyL._SX466_.jpg";
            Ion.with(ArActionPage.this)
                    .load(danceMonkey)
                    .asBitmap()
                    .setCallback((e, result) -> {
                        make(mainContainer, "Scan the marker for few seconds, video will start playing soon after", LENGTH_LONG).show();
                        arFragment = new CustomArFragment(result);
                        arFragment.setOnSessionInitializationListener(session -> {
                            texture = new ExternalTexture();
                            mediaPlayer.setSurface(texture.getSurface());
                            mediaPlayer.setLooping(true);
                            ModelRenderable
                                    .builder()
                                    .setSource(ArActionPage.this, Uri.parse("video_screen.sfb"))
                                    .build()
                                    .thenAccept(modelRenderable -> {
                                        modelRenderable.getMaterial().setExternalTexture("videoTexture",
                                                texture);
                                        modelRenderable.getMaterial().setFloat4("keyColor",
                                                new Color(0.01843f, 1f, 0.098f));
                                        renderable = modelRenderable;
//                                        addNodeToScene(arFragment, anchor, modelRenderable))

                                    });

                            scene = arFragment.getArSceneView().getScene();
                            scene.addOnUpdateListener(ArActionPage.this::onUpdate);

                        });

//                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mediaPlayer) {

                                runOnUiThread(getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fg1, arFragment)::commit);

//                            }
//
//                        });


                    });



        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdate(FrameTime frameTime) {

        if (isImageDetected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage image : augmentedImages) {

            if (image.getTrackingState() == TrackingState.TRACKING) {

                if (image.getName().equals("image")) {

                    isImageDetected = true;

//                    Toast.makeText(ArActionPage.this,"detected",Toast.LENGTH_LONG).show();
                    playVideo(image.createAnchor(image.getCenterPose()), image.getExtentX(), image.getExtentZ());   // if marker is a square
//                    playVideo(image.createAnchor(image.getCenterPose()), image.getExtentX(), image.getExtentZ());       // if the marker equals to actual video frame width and height

                    break;
                }

            }

        }

    }

    private void playVideo(Anchor anchor, float extentX, float extentZ) {

        progressBar = findViewById(R.id.progressBar2);

        mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> progressBar.setSecondaryProgress(i));

        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
//                    updateProgressCallbackTask();
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                        int currentPosition = mediaPlayer.getCurrentPosition();
                        runOnUiThread(()->{
                            progressBar.setProgress((mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                        });
                    }
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekBarPositionUpdateTask,
                0,
                300,
                TimeUnit.MILLISECONDS
        );


        AnchorNode anchorNode = new AnchorNode(anchor);
        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });


        anchorNode.setWorldScale(new Vector3(extentX, 1f, extentZ));

        scene.addChild(anchorNode);

        mediaPlayer.start();
    }




//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void placeObject(ArFragment arFragment, Anchor anchor, Uri uri) {
//
//        ModelRenderable.builder()
//                .setSource(arFragment.getContext(), RenderableSource.builder().setSource(this,
//                        Uri.parse("http://192.168.1.8/3d/aventador.glb"),
//                        RenderableSource.SourceType.GLB).build())
//                .build()
//                .thenAccept(modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable))
//                .exceptionally(throwable -> {
//                            Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
//                            return null;
//                        }
//                );
//    }


//    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
//
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
//        node.setRenderable(renderable);
//        node.setParent(anchorNode);
//        arFragment.getArSceneView().getScene().addChild(anchorNode);
//        node.select();
//
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Toast.makeText(ArActionPage.this,"detected",Toast.LENGTH_LONG).show();
                        mediaPlayer.pause();
                        return true;
                    case R.id.navigation_sms:
                        mediaPlayer.start();
                        return true;
    //                case R.id.navigation_cart:
    //                    toolbar.setTitle("Cart");
    //                    return true;
    //                case R.id.navigation_profile:
    //                    toolbar.setTitle("Profile");
    //                    return true;
                }
                return false;
            };









}
