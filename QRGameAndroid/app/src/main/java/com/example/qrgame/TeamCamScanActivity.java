package com.example.qrgame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class TeamCamScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView ScannerView;
    private static final int cam = Camera.CameraInfo.CAMERA_FACING_BACK;
    private final DatabaseReference firebaseFieldRef_teams = FirebaseDatabase.getInstance().getReference("teams");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_scan);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }
    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(TeamCamScanActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionResult(int requestCode, String permission, int[] grantResult){
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResult.length > 0){
                    boolean cameraAccept = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccept){
                        Toast.makeText(this, "Permission for Camera Granted By User!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(this, "Permission for Camera Not Granted By User!", Toast.LENGTH_LONG).show();
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            showMessageOKCancel("You need to grant permission!", (dialog, which) -> {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                }
                            });
                            return;
                        }
                    }
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M){
            if(ScannerView == null){
                ScannerView = new ZXingScannerView(this);
                setContentView(ScannerView);
            }
            ScannerView.setResultHandler(this);
            ScannerView.startCamera();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScannerView.stopCamera();
        ScannerView = null;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener oklistener){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", oklistener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void handleResult(Result result) {
        final String rawResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Joining team:");
        builder.setMessage("\"" + rawResult + "\"");
        DatabaseReference dbTeamsFieldRef = FirebaseDatabase.getInstance().getReference("teams");
        builder.setPositiveButton("OK", (dialog, which) -> dbTeamsFieldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean teamNotInDB = true;
                for(DataSnapshot sp : snapshot.getChildren()){
                    if(rawResult.equals(sp.getKey())){
                        teamNotInDB = false;
                        writeToFile("team.txt", rawResult);
                        startActivity(new Intent(TeamCamScanActivity.this, GameDashboardActivity.class));
                    }
                }
                if(teamNotInDB){
                    Toast.makeText(TeamCamScanActivity.this, "No team with the name: " + rawResult, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(TeamCamScanActivity.this, MainActivity.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FIREBASE-ERROR", error.getMessage());
            }
        }));
        builder.setNegativeButton("Cancel", (dialog, which) -> onDestroy());
        builder.setMessage(rawResult);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (res != null){
            if (res.getContents() == null){
                Toast.makeText(this, "You exited the scanner!", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void writeToFile(String filename, String content){
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, filename));
            writer.write(content.getBytes());
            Log.d("PATH", path.toString());
        } catch (FileNotFoundException e) {
            Log.d("IO-ERROR", e.toString());
        } catch (IOException e) {
            Log.d("IO-ERROR", e.toString());
        }
    }
}
