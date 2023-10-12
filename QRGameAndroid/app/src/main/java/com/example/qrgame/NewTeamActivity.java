package com.example.qrgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewTeamActivity extends AppCompatActivity {
    private interface TeamExistsCallback{
        void onResult(boolean teamExists);
    }
    boolean teamExists;
    FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();
    EditText teamNameEt;
    Button createTeamBtn;
    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_team);
        teamNameEt = findViewById(R.id.nameEt);
        createTeamBtn = findViewById(R.id.createBtn);
        createTeamBtn.setOnClickListener(view -> {
            if(validateTeamName(teamNameEt.getText().toString())){ // check if team name can be a variable name -> cannot start with num, then only alpha and numeric chars, etc.
                validateTeamNameExistsInDb(teamNameEt.getText().toString(), (teamExists) -> {
                    if(teamExists){
                        Log.d("TEAM-EXISTS", String.valueOf(this.teamExists));
                        Toast.makeText(this, "Team already exists in the database!", Toast.LENGTH_LONG).show();
                    } else {
                        insertNewTeam();
                    }
                });
            } else{
                Log.d("WRONG-TEAM-NAME", "WRONG TEAM NAME!");
                Toast.makeText(this, "Team name is in the incorrect format!", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void insertNewTeam(){
        DatabaseReference dbNumOfTeamsFieldRef = firebaseDb.getReference("numOfTeams");
        DatabaseReference dbTeamsFieldRef = firebaseDb.getReference("teams");
        team = new Team(0, "building_I");
        dbNumOfTeamsFieldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currentValue = (long) dataSnapshot.getValue();
                long newValue = currentValue + 1;
                dbNumOfTeamsFieldRef.setValue(newValue, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        Log.e("FIREBASE-TEAM-FAILURE", "Security error: " + databaseError.getMessage());
                        Toast.makeText(NewTeamActivity.this, "Team Creation Failed! There are already 10 teams in the game!", Toast.LENGTH_LONG).show();
                    } else { // no team limit was hit, new team can be inserted!
                        Log.d("FIREBASE-TEAM-SUCCESS", "Success! Number of teams incremented!");
                        dbTeamsFieldRef.child(teamNameEt.getText().toString()).setValue(team, (databaseError2, databaseReference2) -> {
                            if (databaseError2 == null) {
                                Log.d("FIREBASE-TEAM-SUCCESS", "Success! New team created!");
                                Toast.makeText(NewTeamActivity.this, "Team team created: " + teamNameEt.getText(), Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("FIREBASE-TEAM-FAILURE", "Error writing team: " + databaseError2.getMessage());
                            }
                        });
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FIREBASE-READ-ERROR", error.getMessage());
            }
        });
        startActivity(new Intent(NewTeamActivity.this, MainActivity.class));
    }
    public boolean validateTeamName(String teamName){ // team name has to be a valid variable name!
        boolean result = false;
        Pattern firstChar = Pattern.compile("[A-z_]");
        Pattern restOfTheChar = Pattern.compile("([[A-z][0-9]_])*");
        Matcher firstCharMatcher = firstChar.matcher(teamName.substring(0, 1));
        Matcher restOfTheCharMatcher = restOfTheChar.matcher(teamName.substring(1));
        if(firstCharMatcher.matches() && restOfTheCharMatcher.matches()){
            result = true;
        }
        return result;
    }
    public void validateTeamNameExistsInDb(String teamName, TeamExistsCallback callback){
        DatabaseReference firebaseFieldRef_teams = FirebaseDatabase.getInstance().getReference("teams");
        firebaseFieldRef_teams.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sp : snapshot.getChildren()){
                    if(sp.getKey().equals(teamName))
                    {
                        teamExists = true;
                        break;
                    }
                    teamExists = false;
                }
                callback.onResult(teamExists);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE-READ-ERROR", error.getMessage());
                callback.onResult(false);
            }
        });
    }
}

