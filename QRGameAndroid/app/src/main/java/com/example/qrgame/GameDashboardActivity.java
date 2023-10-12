package com.example.qrgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameDashboardActivity extends AppCompatActivity {
    TextView teamNameTv;
    TextView scoreTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_dashboard);
        teamNameTv = findViewById(R.id.teamNameTv);
        scoreTv = findViewById(R.id.scoreTv);
        teamNameTv.setText(readFromFile("team.txt"));
        DatabaseReference firebaseFieldRef_teams = FirebaseDatabase.getInstance().getReference("teams");
        DatabaseReference firebaseFieldRef_teams_child = firebaseFieldRef_teams.child(readFromFile("team.txt"));
        firebaseFieldRef_teams_child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String scoreStr = snapshot.child("score").getValue().toString();
                writeToFile("score.txt", scoreStr);
                scoreTv.setText("Current Score: " + scoreStr);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FIREBASE-READ-ERROR", error.getMessage());
            }
        });
    }
    public void exitButtonClick(View v){
        startActivity(new Intent(GameDashboardActivity.this, MainActivity.class));
    }
    public void readTaskQrButtonClick(View view) {
        startActivity(new Intent(GameDashboardActivity.this, TaskCamScanActivity.class));
    }
    public String readFromFile(String fileName){
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        byte[] content = new byte[(int) readFrom.length()];
        try {
            FileInputStream stream = new FileInputStream(readFrom);
            stream.read(content);
            return new String(content);
        } catch (Exception e) {
            Log.d("IO-ERROR", e.toString());
            return e.toString();
        }
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