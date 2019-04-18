package com.nhancv.m3dviewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.andresoviedo.util.android.AssetUtils;
import org.andresoviedo.util.android.ContentUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class M3DActivityLite extends Activity {
    private static final String TAG = M3DActivityLite.class.getSimpleName();

    private static boolean FINISH_AFTER_RECORD = true;
    private View overlay;
    private M3DSurfaceView gLView;
    private boolean isRecording;
    private File outputFile;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3dlite);

        gLView = findViewById(R.id.gLView);
        overlay = findViewById(R.id.overlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionsHelper.hasPermissions(this)) {
                // Note that order matters - see the note in onPause(), the reverse applies here.
                gLView.resume();
                try {
                    outputFile = createVideoOutputFile();
                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getRealSize(size);
                    gLView.initRecorder(outputFile, size.x, size.y, null, null);

                    if (dialog == null) {
                        openDialog();
                    }

                } catch (IOException ioex) {
                    Log.e(TAG, "Couldn't re-init recording", ioex);
                }
            } else {
                PermissionsHelper.requestPermissions(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_hidesurfaceview:
                if (overlay.getVisibility() == View.INVISIBLE) {
                    overlay.setVisibility(View.VISIBLE);
                    item.setTitle("Show SurfaceView");
                } else {
                    overlay.setVisibility(View.INVISIBLE);
                    item.setTitle("Hide SurfaceView");
                }
                break;
            case R.id.menu_main_record:
                if (isRecording) {
                    gLView.stopRecording();

                    Uri contentUri = FileProvider.getUriForFile(M3DActivityLite.this,
                            getPackageName() + ".fileprovider", outputFile);

                    share(contentUri);
                    isRecording = false;
                    Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
                    item.setTitle("Record");

                    //Finish activity
                    if (FINISH_AFTER_RECORD) finish();
                    else {
                        outputFile = createVideoOutputFile();

                        try {
                            int screenWidth = gLView.getWidth();
                            int screenHeight = gLView.getHeight();
                            gLView.initRecorder(outputFile, (int) screenWidth, (int) screenHeight, null,
                                    null);
                        } catch (IOException ioex) {
                            Log.e(TAG, "Couldn't re-init recording", ioex);
                        }
                    }

                } else {
                    gLView.startRecording();
                    Log.v(TAG, "Recording Started");
                    Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
                    item.setTitle("Stop");
                    isRecording = true;
                }
                break;
            case R.id.menu_main_choosefile:
                openDialog();
                break;
        }
        return true;
    }

    private File createVideoOutputFile() {

        File tempFile = null;
        try {
            File dirCheck = new File(
                    getFilesDir().getCanonicalPath() + "/" + "captures");

            if (!dirCheck.exists()) {
                dirCheck.mkdirs();
            }

            String filename = new Date().getTime() + "";
            tempFile = new File(
                    getFilesDir().getCanonicalPath() + "/" + "captures" + "/"
                            + filename + ".mp4");
        } catch (IOException ioex) {
            Log.e(TAG, "Couldn't create output file", ioex);
        }

        return tempFile;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Note that order matters - see the note in onPause(), the reverse applies here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gLView.resume();
        }
        try {
            outputFile = createVideoOutputFile();
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(size);
            gLView.initRecorder(outputFile, size.x, size.y, null, null);
        } catch (IOException ioex) {
            Log.e(TAG, "Couldn't re-init recording", ioex);
        }
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

    private void share(Uri contentUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("video/mp4");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    private void openDialog() {
        if (dialog != null) dialog.dismiss();
        dialog = AssetUtils.create3DChooserDialog(this, "Select file", null, "models", "(?i).*\\.(obj|stl|dae)",
                (String file) -> {
                    if (file != null) {
                        ContentUtils.provideAssets(this);
                        Uri uri = Uri.parse("assets://" + getPackageName() + "/" + file);

                        M3DSceneLoader scene = new M3DSceneLoader(this);
                        scene.init(uri, 0, gLView);
                        gLView.setupScene(scene);

                    }
                });
        dialog.show();
    }

}
