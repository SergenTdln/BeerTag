package application_projet4_groupe12.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.QRResultActivity;
import application_projet4_groupe12.data.preference.AppPreference;
import application_projet4_groupe12.data.preference.PrefKey;
import application_projet4_groupe12.utils.ActivityUtils;

public class QRScanFragment extends Fragment  {

    private Activity mActivity;
    private Context mContext;

    private ViewGroup contentFrame;
    private ZXingScannerView zXingScannerView;
    private ArrayList<Integer> mSelectedIndices;

    private boolean isFlash, isAutoFocus;
    private int camId, frontCamId, rearCamId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVar();
        zXingScannerView = new ZXingScannerView(mActivity);
        setupFormats();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qrscan, container, false);


        initView(rootView);
        initListener();

        return rootView;
    }

    private void initVar() {
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();

        isFlash = AppPreference.getInstance(mContext).getBoolean(PrefKey.FLASH, false); // flash off by default
        isAutoFocus = AppPreference.getInstance(mContext).getBoolean(PrefKey.FOCUS, true); // auto focus on by default
        camId = AppPreference.getInstance(mContext).getInteger(PrefKey.CAM_ID); // back camera by default
        if(camId == -1) {
            camId = rearCamId;
        }

        loadCams();
    }

    private void initView(View rootView) {
        contentFrame = rootView.findViewById(R.id.content_frame);
        //todo : corriger le toolbar sur le scanner
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Scanning");

    }


    private void initListener() {
        zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {

                String resultStr = result.getText();
                ArrayList<String> previousResult = AppPreference.getInstance(mContext).getStringArray(PrefKey.RESULT_LIST);
                previousResult.add(resultStr);
                AppPreference.getInstance(mContext).setStringArray(PrefKey.RESULT_LIST, previousResult);

                zXingScannerView.resumeCameraPreview(this);

                ActivityUtils.getInstance().invokeActivity(mActivity, QRResultActivity.class, false);

            }
        });

    }

    private void activateScanner() {
        if(zXingScannerView != null) {

            if(zXingScannerView.getParent()!=null) {
                ((ViewGroup) zXingScannerView.getParent()).removeView(zXingScannerView); // to prevent crush on re adding view
            }
            contentFrame.addView(zXingScannerView);

            if(zXingScannerView.isActivated()) {
                zXingScannerView.stopCamera();
            }

            zXingScannerView.startCamera(camId);
            zXingScannerView.setFlash(isFlash);
            zXingScannerView.setAutoFocus(isAutoFocus);
        }
    }


    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(zXingScannerView != null) {
            zXingScannerView.setFormats(formats);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        activateScanner();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(zXingScannerView != null) {
            zXingScannerView.stopCamera();
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if(zXingScannerView != null) {
            if (visible) {
                zXingScannerView.setFlash(isFlash);
            } else {
                zXingScannerView.setFlash(false);
            }
        }
    }


    private void loadCams() {
        AppPreference.getInstance(mContext).setInteger(PrefKey.CAM_ID, 0);
    }

    //todo : corriger le retour à Home
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                finish()

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
