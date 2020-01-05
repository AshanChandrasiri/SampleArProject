package com.uom.happycelebrate.utils;



import android.graphics.Bitmap;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;


public class CustomArFragment extends ArFragment {

    private final Bitmap result;

    public CustomArFragment(Bitmap result) {
        this.result = result;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {

        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);

        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
//        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        aid.addImage("image", result);
        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);
        return config;
    }

}

