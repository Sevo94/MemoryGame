package com.example.admin.memorytraining.singletons;

import com.example.admin.memorytraining.utils.GameStatus;
import com.example.admin.memorytraining.utils.MyCountDownTimer;
import com.example.admin.memorytraining.interfaces.LevelUpCallback;
import com.example.admin.memorytraining.interfaces.TimerCallbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by Admin on 8/8/2017.
 */

public class GameInstance {

    private static GameInstance gameInstance;

    private final int circlesNum = 36;
    private boolean[] randNumbers = new boolean[circlesNum];

    private List<Integer> randomNumbersList = new ArrayList<>();
    private TreeSet<Integer> numbersSet = new TreeSet<>();

    private Random random = new Random();
    private MyCountDownTimer myCountDownTimer;
    private TimerCallbacks timerCallbacks;
    private LevelUpCallback levelUpCallback;

    private GameStatus gameStatus;
    private int gameLevel;

    private final int totalGameLevels = 8;

    private int minIntegerInTree = -1;

    private GameInstance() {
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public List<Integer> getRandomNumbersList() {
        return randomNumbersList;
    }

    public int getMinIntegerInTree() {
        return minIntegerInTree;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setGameLevel(int gameLevel) {
        this.gameLevel = gameLevel;
    }

    public void setLevelUpCallback(LevelUpCallback levelUpCallback) {
        this.levelUpCallback = levelUpCallback;
    }

    public void setTimerCallbacks(TimerCallbacks timerCallbacks) {
        this.timerCallbacks = timerCallbacks;
    }

    public static GameInstance getInstance() {
        if (gameInstance == null) {
            synchronized (GameInstance.class) {
                if (gameInstance == null) {
                    gameInstance = new GameInstance();
                }
            }
        }
        return gameInstance;
    }

    private void getRandomIntegers() {
        randomNumbersList = new ArrayList<>();

        int randomNumber;
        for (int i = 0; i < gameLevel + 2; i++) {

            do {
                randomNumber = random.nextInt(gameLevel * 2 + circlesNum / 3);
            } while (randNumbers[randomNumber]);

            randNumbers[randomNumber] = true;
            randomNumbersList.add(randomNumber);
            numbersSet.add(randomNumber);
        }

        minIntegerInTree = numbersSet.first();

        if (timerCallbacks != null) {
            timerCallbacks.onTimerStarted();
        }
        startCountDownTimer();
    }

    public void setGameInstance() {
        gameStatus = GameStatus.NewGame;

        for (int i = 0; i < circlesNum; i++) {
            randNumbers[i] = false;
        }
        numbersSet.clear();
        randomNumbersList.clear();

        minIntegerInTree = -1;

        getRandomIntegers();
    }

    public void updateMinimumValue(int tag) {
        numbersSet.remove(randomNumbersList.get(tag));

        if (!numbersSet.isEmpty()) {
            minIntegerInTree = numbersSet.first();
        } else {
            gameStatus = GameStatus.SUCCESS;

            if (levelUpCallback != null) {

                if (gameLevel == totalGameLevels) {
                    levelUpCallback.onGameFinish();
                } else {
                    ++gameLevel;
                    levelUpCallback.onLevelUp();
                }
            }
        }
    }

    public void startCountDownTimer() {
        myCountDownTimer = new MyCountDownTimer(3000, 1000, new TimerCallbacks() {
            @Override
            public void onTimerFinish() {
                if (timerCallbacks != null) {
                    timerCallbacks.onTimerFinish();
                }
            }

            @Override
            public void onTimerStarted() {

            }

            @Override
            public void onTimerInterval(long timerVal) {
                if (timerCallbacks != null) {
                    timerCallbacks.onTimerInterval(timerVal);
                }
            }
        });

        myCountDownTimer.start();
    }

    public void stopTimer() {
        myCountDownTimer.stop();
    }
}
