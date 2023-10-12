package com.example.qrgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    Button joinTeamBtn, newTeamBtn;
    ImageView gameLogoIv;
    TextView loginTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        signInAnonymously();
    }
    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("FIREBASE-AUTH", "signInAnonymously:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("FIREBASE-AUTH", "signInAnonymously:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    public void joinTeamBtnClick(View view){
        showDialog();
    }

    private void showDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                .setTitle("JOINING TEAM")
                .setMessage("Select how you want to join a team!")
                .setPositiveButton("QR Code", (dialog, which) -> startActivity(new Intent(MainActivity.this, TeamCamScanActivity.class)))
                .setNegativeButton("List Teams", (dialog, which) -> startActivity(new Intent(MainActivity.this, JoinTeamActivity.class)));
        alert.show();
    };

    public void newTeamBtnClick(View v){
        startActivity(new Intent(MainActivity.this, NewTeamActivity.class));
    }

    private void updateUI(FirebaseUser user) {
        if (user != null)
        {
            setContentView(R.layout.activity_main);
            joinTeamBtn = findViewById(R.id.joinTeamBtn);
            newTeamBtn = findViewById(R.id.newTeamBtn);
            gameLogoIv = findViewById(R.id.gameLogoIv);
            Animation gameLogoAnim = AnimationUtils.loadAnimation(this, R.anim.game_logo_animation);
            gameLogoIv.startAnimation(gameLogoAnim);
            loginTv = findViewById(R.id.loginTv);
            Animation loginTextAnim = AnimationUtils.loadAnimation(this, R.anim.login_text_ainmation);
            loginTv.startAnimation(loginTextAnim);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int startColor = getWindow().getStatusBarColor();
                int endColor = ContextCompat.getColor(this, R.color.black);
                ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
            }
        }
        else
            setContentView(R.layout.error_activity_main);
    }
}
