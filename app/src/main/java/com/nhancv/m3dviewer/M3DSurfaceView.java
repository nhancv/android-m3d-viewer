package com.nhancv.m3dviewer;

import android.content.Context;
import android.util.AttributeSet;

import org.andresoviedo.android_3d_model_engine.model.Camera;

public class M3DSurfaceView extends RecordableSurfaceView implements RecordableSurfaceView.RendererCallbacks {

    private M3DRenderer renderer;

    /**
     * Background GL clear color. Default is light gray
     */
    private float[] backgroundColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};

    private M3DSceneLoader scene;
    private Camera camera;

    public M3DSurfaceView(Context context) {
        this(context, null);
    }

    public M3DSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        camera = new Camera();
        // This is the actual renderer of the 3D space
        renderer = new M3DRenderer(this);
        setRendererCallbacks(this);
    }

    public void setupScene(M3DSceneLoader sceneLoader) {
        this.scene = sceneLoader;
    }

    public M3DRenderer getModelRenderer() {
        return renderer;
    }

    public float[] getBackgroundColor() {
        return backgroundColor;
    }

    public M3DSceneLoader getScene() {
        return scene;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void onSurfaceCreated() {
        renderer.onSurfaceCreated(null, null);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        renderer.onSurfaceChanged(null, width, height);
    }

    @Override
    public void onSurfaceDestroyed() {

    }

    @Override
    public void onContextCreated() {

    }

    @Override
    public void onPreDrawFrame() {

    }

    @Override
    public void onDrawFrame() {
        renderer.onDrawFrame(null);
    }
}
