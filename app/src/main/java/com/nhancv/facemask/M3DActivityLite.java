package com.nhancv.facemask;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.nhancv.m3dviewer.R;
import org.andresoviedo.util.android.AssetUtils;
import org.andresoviedo.util.android.ContentUtils;

public class M3DActivityLite extends Activity {

    private M3DSceneLoader scene;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3dlite);
    }

    @Override
    protected void onResume() {
        super.onResume();


        AssetUtils.createChooserDialog(this, "Select file", null, "models", "(?i).*\\.(obj|stl|dae)",
                (String file) -> {
                    if (file != null) {
                        ContentUtils.provideAssets(this);
                        Uri uri = Uri.parse("assets://" + getPackageName() + "/" + file);

                        scene = new M3DSceneLoader(this);
                        M3DSurfaceView gLView = findViewById(R.id.gLView);
                        scene.init(uri, 0, gLView);
                        gLView.setupRender(scene);
                    }
                });

    }

    private void loadModelFromAssets() {
        AssetUtils.createChooserDialog(this, "Select file", null, "models", "(?i).*\\.(obj|stl|dae)",
                (String file) -> {
                    if (file != null) {
                        ContentUtils.provideAssets(this);
                        Uri.parse("assets://" + getPackageName() + "/" + file);
                    }
                });
    }


}
