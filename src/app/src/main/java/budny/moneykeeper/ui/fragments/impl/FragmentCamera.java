package budny.moneykeeper.ui.fragments.impl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import budny.moneykeeper.R;
import budny.moneykeeper.cv.Filters;
import budny.moneykeeper.cv.CVManager;
import budny.moneykeeper.cv.Operations;

/**
 * A fragment used to demonstrate camera-related functionality.
 */
public class FragmentCamera extends Fragment
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    @SuppressWarnings("unused")
    private static final String TAG = FragmentCamera.class.getSimpleName();

    private CameraBridgeViewBase mCameraView;
    private Mat mOutputFrame;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraView = (CameraBridgeViewBase) view.findViewById(R.id.fragment_camera_view_camera);
        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CVManager.init()) {
            mCameraView.enableView();
            mOutputFrame = new Mat();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat roi = new Mat();
        Mat inputMat = inputFrame.rgba();
        // select region of interest
        Operations.sliceCentered(inputMat, roi, 400, 100);
        Operations.darken(inputMat, mOutputFrame);
        // process region of interest
        Imgproc.cvtColor(roi, roi, Imgproc.COLOR_RGBA2GRAY);
        Filters.basic(roi, roi);
        Imgproc.cvtColor(roi, roi, Imgproc.COLOR_GRAY2RGBA);
        // merge results
        Operations.mergeCentered(mOutputFrame, roi, mOutputFrame);
        return mOutputFrame;
    }
}