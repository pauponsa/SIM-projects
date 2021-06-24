package com.eventic.src.presentation.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.eventic.src.presentation.activities.companyHomePage.PopUpScanActivity;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.activities.event.PopUpTicketActivity;
import com.example.eventic.R;
import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;

public class QrScanFragment extends Fragment {
    private CodeScanner mCodeScanner;
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;

    public QrScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_qr_scan, container, false);
        super.onCreate(savedInstanceState);


        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        decodedString(result.getText());
                    }
                });
            }
        });
        mCodeScanner.setErrorCallback(error -> activity.runOnUiThread(
                () -> Toast.makeText(activity, "SCANNER ERROR", Toast.LENGTH_LONG).show()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    void decodedString(String text)
    {
        this.onPause();


        Intent intent = new Intent(getActivity(), PopUpScanActivity.class);
        intent.putExtra("scan", text);
        startActivity(intent);

    }
}