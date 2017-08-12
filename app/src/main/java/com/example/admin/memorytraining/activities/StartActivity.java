package com.example.admin.memorytraining.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.admin.memorytraining.singletons.GameInstance;
import com.example.admin.memorytraining.utils.GameStatus;
import com.example.admin.memorytraining.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Button newGameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_acticity);

        initUserInterface();

        newGameBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GameInstance.getInstance().setGameStatus(GameStatus.NewGame);
        GameInstance.getInstance().setGameLevel(1);
        btnScaleAnimation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_game_btn:
                startGameActivity();
                break;
        }
    }

    private void initUserInterface() {
        newGameBtn = (Button) findViewById(R.id.new_game_btn);
    }

    private void startGameActivity() {
        Intent gameActivityIntent = new Intent(this, GameActivity.class);
        startActivity(gameActivityIntent);
    }

    private void btnScaleAnimation() {
        Animation bubbleAnimation = AnimationUtils.loadAnimation(this, R.anim.bubble_anim);
        newGameBtn.startAnimation(bubbleAnimation);
    }
}
