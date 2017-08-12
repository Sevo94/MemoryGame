package com.example.admin.memorytraining.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.memorytraining.singletons.GameInstance;
import com.example.admin.memorytraining.utils.GameStatus;
import com.example.admin.memorytraining.R;
import com.example.admin.memorytraining.interfaces.LevelUpCallback;
import com.example.admin.memorytraining.interfaces.TimerCallbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, TimerCallbacks, LevelUpCallback {

    private final int circlesNumInRow = 6;
    private final int rowsCount = 6;
    private final int totalCirclesNumber = circlesNumInRow * rowsCount;

    private RelativeLayout[] circularButtons = new RelativeLayout[totalCirclesNumber];
    private List<Integer> randomlySelectedViews = new ArrayList<>();
    private GameInstance gameInstance;
    private TextView timerTV;

    private LinearLayout gameModeLayout;
    private LinearLayout overlayLayout;
    private LinearLayout backBtn;
    private LinearLayout continueBtn;
    private LinearLayout gameEndLayout;
    private LinearLayout gameEndBtn;

    private TextView textView;

    private int[] IdArrayButtons = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6};
    private int[] IdArrayRows = new int[]{R.id.row1, R.id.row2, R.id.row3, R.id.row4, R.id.row5, R.id.row6};

    private boolean[] isVisited = new boolean[totalCirclesNumber];

    boolean onStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameInstance = GameInstance.getInstance();
        gameInstance.setTimerCallbacks(this);
        gameInstance.setLevelUpCallback(this);

        setUpUserInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (onStop && gameInstance.getGameStatus() == GameStatus.NewGame) {
            gameInstance.startCountDownTimer();
            onStop = false;
            return;
        }

        switch (gameInstance.getGameStatus()) {
            case NewGame:
                initGame();
                break;
            case FAIL:
                showFailLayout();
                break;
            case SUCCESS:
                showLevelUpLayout();
                break;
            default:
                onStop = false;
        }
    }

    @Override
    public void onClick(View view) {
        GameStatus gameStatus = gameInstance.getGameStatus();

        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.game_end_btn:
                onBackPressed();
                break;
            case R.id.continue_btn:
                if (gameStatus == GameStatus.FAIL) {
                    initGame();
                } else if (gameStatus == GameStatus.SUCCESS) {
                    hideLevelUpLayout();
                    showRandomCircles();
                    gameInstance.setGameInstance();
                }
                break;
            default:
                handleCircleClicks(view);
        }
    }

    private void initGame() {
        showRandomCircles();
        gameInstance.setGameInstance();
        showGameLayout();
    }

    private void handleCircleClicks(View view) {
        int tag = (int) view.getTag();

        if (gameInstance.getRandomNumbersList().get(tag) == gameInstance.getMinIntegerInTree()) {
            circularButtons[randomlySelectedViews.get(tag)].setOnClickListener(null);
            circularButtons[randomlySelectedViews.get(tag)].setBackground(getResources().getDrawable(R.drawable.circular_shape_with_stroke));
            gameInstance.updateMinimumValue(tag);
        } else {
            gameInstance.setGameStatus(GameStatus.FAIL);
            showFailLayout();
        }
    }

    private void setUpUserInterface() {
        int count = 0;

        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < circlesNumInRow; j++) {
                RelativeLayout view = findViewById(IdArrayRows[i]).findViewById(IdArrayButtons[j]);
                circularButtons[count++] = view;
            }
        }

        backBtn = (LinearLayout) findViewById(R.id.back_btn);
        continueBtn = (LinearLayout) findViewById(R.id.continue_btn);

        backBtn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);

        timerTV = (TextView) findViewById(R.id.timer_tv);
        gameModeLayout = (LinearLayout) findViewById(R.id.game_mode_linear_layout);
        overlayLayout = (LinearLayout) findViewById(R.id.overlay_view);
        gameEndLayout = (LinearLayout) findViewById(R.id.game_finish_layout);
        gameEndBtn = (LinearLayout) findViewById(R.id.game_end_btn);
        textView = (TextView) findViewById(R.id.textView);

        gameEndBtn.setOnClickListener(this);
    }

    private void showRandomCircles() {
        Random random = new Random();

        if (randomlySelectedViews.size() > 0) {
            for (int i = 0; i < randomlySelectedViews.size(); i++) {
                circularButtons[randomlySelectedViews.get(i)].setVisibility(View.INVISIBLE);
            }
        }
        randomlySelectedViews.clear();

        for (int i = 0; i < totalCirclesNumber; i++) {
            isVisited[i] = false;
        }
        for (int i = 0; i < gameInstance.getGameLevel() + 2; i++) {

            int randomNumber;
            do {
                randomNumber = random.nextInt(circlesNumInRow * rowsCount);
            } while (isVisited[randomNumber]);

            isVisited[randomNumber] = true;
            randomlySelectedViews.add(randomNumber);
        }
    }

    @Override
    public void onLevelUp() {
        showLevelUpLayout();
    }

    @Override
    public void onGameFinish() {
        showGameEndLayout();
    }

    @Override
    public void onTimerStarted() {
        timerTV.setVisibility(View.VISIBLE);
        for (int i = 0; i < randomlySelectedViews.size(); i++) {

            int circlePos = randomlySelectedViews.get(i);
            circularButtons[circlePos].setOnClickListener(null);
            circularButtons[circlePos].setBackground(getResources().getDrawable(R.drawable.circular_shape_with_stroke));
            circularButtons[circlePos].setVisibility(View.VISIBLE);

            TextView textView = circularButtons[circlePos].findViewById(R.id.rand_num_tv);
            textView.setText(String.valueOf(gameInstance.getRandomNumbersList().get(i)));
        }
    }

    @Override
    public void onTimerInterval(long timerValue) {
        timerTV.setText(String.valueOf(timerValue));
    }

    @Override
    public void onTimerFinish() {
        gameInstance.setGameStatus(GameStatus.InProgress);
        timerTV.setText("");

        for (int i = 0; i < randomlySelectedViews.size(); i++) {
            int circlePos = randomlySelectedViews.get(i);
            circularButtons[circlePos].setBackground(getResources().getDrawable(R.drawable.circular_shape));
            circularButtons[circlePos].setOnClickListener(this);
            circularButtons[circlePos].setTag(i);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        onStop = true;
        timerTV.setText("");
        gameInstance.stopTimer();
    }

    private void showGameLayout() {
        gameModeLayout.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.GONE);
    }

    private void showFailLayout() {
        overlayLayout.setVisibility(View.VISIBLE);
        gameModeLayout.setVisibility(View.GONE);

        textView.setText(getResources().getString(R.string.you_failed));
        overlayLayout.setBackgroundColor(getResources().getColor(R.color.lightRedColor2));
        backBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_red_shape));
        continueBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_red_shape));
    }

    private void showLevelUpLayout() {
        overlayLayout.setVisibility(View.VISIBLE);
        gameModeLayout.setVisibility(View.GONE);

        textView.setText(getResources().getString(R.string.fantastic));
        overlayLayout.setBackgroundColor(getResources().getColor(R.color.lightGreen2));
        backBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_green_shape));
        continueBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_green_shape));
    }

    private void hideLevelUpLayout() {
        overlayLayout.setVisibility(View.GONE);
        gameModeLayout.setVisibility(View.VISIBLE);
    }

    private void showGameEndLayout() {
        gameEndLayout.setVisibility(View.VISIBLE);
        gameModeLayout.setVisibility(View.GONE);

        Log.d("", "");
    }

}
