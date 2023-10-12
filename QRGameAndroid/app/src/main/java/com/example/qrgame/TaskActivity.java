package com.example.qrgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class TaskActivity extends AppCompatActivity {

    TextView descriptionTv;
    TextView titleTv;
    EditText answerEt;
    Button sendBtn;
    Integer score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        descriptionTv = findViewById(R.id.descriptionTv);
        titleTv = findViewById(R.id.titleTv);
        answerEt = findViewById(R.id.answerEt);
        sendBtn = findViewById(R.id.sendBtn);
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, "task.txt");
        FileInputStream is;
        try {
            is = new FileInputStream(readFrom);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String task = reader.readLine();
            String station = reader.readLine();
            String title = reader.readLine();
            String question = reader.readLine();
            titleTv.setText(title);
            descriptionTv.setText(question);
            DatabaseReference dbTeamFieldRef = FirebaseDatabase.getInstance().getReference("teams").
                    child(readFromFile("team.txt"));
//            Team team = new Team(0, station);
//            dbTeamFieldRef.setValue(team);
            DatabaseReference dbTaskFieldRef = FirebaseDatabase.getInstance().getReference("tasks");
            DatabaseReference firebaseFieldRef_tasks_child = dbTaskFieldRef.child(task);
            sendBtn.setOnClickListener(view -> firebaseFieldRef_tasks_child.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String answerHashInDB = snapshot.child("correct_answer").getValue().toString();
                    String providedAnswer = answerEt.getText().toString();
                    String answerHash;
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashBytes = digest.digest(providedAnswer.getBytes(StandardCharsets.UTF_8));
                        StringBuilder hexString = new StringBuilder();
                        for (byte b : hashBytes) {
                            hexString.append(String.format("%02x", b));
                        }
                        answerHash = hexString.toString();
                    } catch (NoSuchAlgorithmException ex) {
                        Log.d("HASHING-ALGORITHM", ex.getMessage());
                        answerHash = "nosuchalgorithm";
                    }
                    if(answerHashInDB.equals(answerHash)){
                        dbTeamFieldRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                score = Integer.parseInt(snapshot.child("score").getValue().toString());
                                if (score == 4){
                                    Toast.makeText(TaskActivity.this, "Team already answered all questions!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Integer incrementedScore = Integer.parseInt(readFromFile("score.txt")) + 1;
                                Team cs = new Team(incrementedScore, station);
                                dbTeamFieldRef.setValue(cs);
                                Toast.makeText(TaskActivity.this, "Correct Answer.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(TaskActivity.this, GameDashboardActivity.class));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("FIREBASE-READ-ERROR", error.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(TaskActivity.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("SENDING-ANSWER-ERROR", error.getMessage());
                }
            }));
        } catch (Exception ex) {
            Toast.makeText(TaskActivity.this, "Error loading task!", Toast.LENGTH_SHORT).show();
            Log.d("ON-CREATE", ex.getMessage());
        }
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
            Log.d("ERROR", e.toString());
            return e.toString();
        }
    }
}