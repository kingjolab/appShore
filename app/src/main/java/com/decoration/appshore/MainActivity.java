package com.decoration.appshore;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.decoration.appshore.utils.WaterFall;

public class MainActivity extends Activity {

    LinearLayout asthmaActionPlan, controlledMedication, asNeededMedication,
            rescueMedication, yourSymtoms, yourTriggers, wheezeRate, peakFlow;

    LayoutParams params;
    LinearLayout next, prev;
    int viewWidth;
    GestureDetector gestureDetector = null;
    HorizontalScrollView horizontalScrollView;
    ArrayList<LinearLayout> layouts;
    int parentLeft, parentRight;
    int mWidth;
    int currPosition, prevPosition;

    private static final String APP_SHARED_PREFS = "login_preferences";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    private boolean isUserLoggedIn;
    int currentlyLoggedInUser;
    String currentlyLoggedInUserString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginCheck();
        setContentView(R.layout.activity_main);
        prev = (LinearLayout) findViewById(R.id.prev);
        next = (LinearLayout) findViewById(R.id.next);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv);
        gestureDetector = new GestureDetector(new MyGestureDetector());
        asthmaActionPlan = (LinearLayout) findViewById(R.id.asthma_action_plan);
        controlledMedication = (LinearLayout) findViewById(R.id.controlled_medication);
        asNeededMedication = (LinearLayout) findViewById(R.id.as_needed_medication);
        rescueMedication = (LinearLayout) findViewById(R.id.rescue_medication);
        yourSymtoms = (LinearLayout) findViewById(R.id.your_symptoms);
        yourTriggers = (LinearLayout) findViewById(R.id.your_triggers);
        wheezeRate = (LinearLayout) findViewById(R.id.wheeze_rate);
        peakFlow = (LinearLayout) findViewById(R.id.peak_flow);
        Display display = getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth(); // deprecated
        viewWidth = mWidth / 3;
        layouts = new ArrayList<LinearLayout>();
        params = new LayoutParams(viewWidth, LayoutParams.WRAP_CONTENT);
        asthmaActionPlan.setLayoutParams(params);
        controlledMedication.setLayoutParams(params);
        asNeededMedication.setLayoutParams(params);
        rescueMedication.setLayoutParams(params);
        yourSymtoms.setLayoutParams(params);
        yourTriggers.setLayoutParams(params);
        wheezeRate.setLayoutParams(params);
        peakFlow.setLayoutParams(params);
        layouts.add(asthmaActionPlan);
        layouts.add(controlledMedication);
        layouts.add(asNeededMedication);
        layouts.add(rescueMedication);
        layouts.add(yourSymtoms);
        layouts.add(yourTriggers);
        layouts.add(wheezeRate);
        layouts.add(peakFlow);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        horizontalScrollView.smoothScrollTo(horizontalScrollView.getScrollX() + viewWidth, horizontalScrollView.getScrollY());
                    }
                }, 100L);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        horizontalScrollView.smoothScrollTo(horizontalScrollView.getScrollX() - viewWidth, horizontalScrollView.getScrollY());
                    }
                }, 100L);
            }
        });
        horizontalScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
        WaterFall waterFall = (WaterFall) findViewById(R.id.waterfall);
        waterFall.setup();
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getX() < e2.getX()) {
                currPosition = getVisibleViews("left");
            } else {
                currPosition = getVisibleViews("right");
            }

            horizontalScrollView.smoothScrollTo(layouts.get(currPosition)
                    .getLeft(), 0);
            return true;
        }
    }

    protected void loginCheck(){
        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        currentlyLoggedInUser = sharedPrefs.getInt("currentLoggedInUserId", 0);
        currentlyLoggedInUserString = Integer.toString(currentlyLoggedInUser);
        if (!isUserLoggedIn) {
            Intent intent = new Intent(this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            Intent intent = new Intent(this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            Intent intent = new Intent(this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onRestart();
    }

    public int getVisibleViews(String direction) {
        Rect hitRect = new Rect();
        int position = 0;
        int rightCounter = 0;
        for (int i = 0; i < layouts.size(); i++) {
            if (layouts.get(i).getLocalVisibleRect(hitRect)) {
                if (direction.equals("left")) {
                    position = i;
                    break;
                } else if (direction.equals("right")) {
                    rightCounter++;
                    position = i;
                    if (rightCounter == 2)
                        break;
                }
            }
        }
        return position;
    }
}