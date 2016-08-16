package com.awesome.soon.snappysnakeprototype;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class MainActivity extends Activity {
    private GameView gameView;

    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable(){
        @Override
        public void run(){
            gameView.runOneStep();
            viewHandler.postDelayed(updateView, 50);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(R.layout.activity_main);
        setContentView(gameView);

        viewHandler.post(updateView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            gameView.setDirectionTo(new Point(event.getX(), event.getY()));
        }

        return super.onTouchEvent(event);
    }
}
