package com.example.qrgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class JoinTeamActivity extends AppCompatActivity {
    TextView numOfTeamsTv;
    ListView teamsLv;
    ArrayList<String> teamsList;
    ArrayAdapter<String> teamsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        numOfTeamsTv = findViewById(R.id.numOfTeamsTv);
        teamsLv = findViewById(R.id.teamsLv);
        teamsList = new ArrayList<>();
        teamsListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teamsList);
        DatabaseReference firebaseFieldRef_teams = FirebaseDatabase.getInstance().getReference("teams");
        firebaseFieldRef_teams.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sp : snapshot.getChildren()){
                    teamsList.add(sp.getKey());
                    teamsListAdapter.notifyDataSetChanged();
                }
                numOfTeamsTv.setText("Current Number of Teams: " + teamsList.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE-READ-ERROR", error.getMessage());
            }
        });
        teamsLv.setAdapter(teamsListAdapter);
        teamsLv.setOnItemClickListener((adapterView, view, i, l) -> {
            String teamNameStr = teamsLv.getItemAtPosition(i).toString();
            writeToFile("team.txt", teamNameStr);
            startActivity(new Intent(JoinTeamActivity.this, GameDashboardActivity.class));
        });
    }
    public void writeToFile(String filename, String content) {
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
