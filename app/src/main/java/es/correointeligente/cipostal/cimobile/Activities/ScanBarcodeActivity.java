package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScanBarcodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZBarScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

//    @Override
//    public void handleResult(Result rawResult) {
//        // Do something with the result here
////        Log.v(TAG, rawResult.getText()); // Prints scan results
////        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
//
//        Toast.makeText(this,rawResult.getText()+" "+rawResult.getBarcodeFormat().toString(),Toast.LENGTH_SHORT).show();
//        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
//    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
//        Toast.makeText(this,result.getContents()+" "+result.getBarcodeFormat().getName(),Toast.LENGTH_SHORT).show();
        // If you would like to resume scanning, call this method below:

        Intent i = new Intent();
        i.putExtra("barcode", result.getContents());
        setResult(CommonStatusCodes.SUCCESS, i);
        mScannerView.stopCamera();
        finish();
//        mScannerView.resumeCameraPreview(this);
    }


//public class ScanBarcodeActivity extends AppCompatActivity {
//
//    SurfaceView mSurfaceViewCameraPreview;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scan_barcode);
//
//        mSurfaceViewCameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
//        createCameraSource();
//    }
//
//    private void createCameraSource() {
//        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
//        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
//                .setAutoFocusEnabled(true)
//                .setRequestedPreviewSize(1600, 1024)
//                .build();
//
//        mSurfaceViewCameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                try {
//                    cameraSource.start(mSurfaceViewCameraPreview.getHolder());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//                cameraSource.stop();
//            }
//        });
//
//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//                if (barcodes.size() > 0) {
//                    Intent i = new Intent();
//                    i.putExtra("barcode", barcodes.valueAt(0)); // get latest barcode from the array
//                    setResult(CommonStatusCodes.SUCCESS, i);
//                    finish();
//                }
//            }
//        });
//    }
}
