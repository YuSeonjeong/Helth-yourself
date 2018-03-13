/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.bagi.levente.pedometer;

import java.util.Calendar;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class Pedometer extends Activity {
	
	

	public enum LinearLayoutSingleAlarmItem {

	}

	private TextView todayText;


	private int tYear;
	private int tMonth;
	private int tDay;

	private long d;
	private long t;
	private long r;


	static final int DATE_DIALOG_ID = 0;
	
	
	
	
   
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    
    private TextView mStepValueView;
    private TextView mDistanceValueView;
    private TextView mCaloriesValueView;
    private int mStepValue;
    private float mDistanceValue;
    private int mCaloriesValue;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    
    private boolean mIsRunning;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mStepValue = 0;

        
        setContentView(R.layout.activity_main);
        
        startStepService();
        
        final RelativeLayout weather;
		RelativeLayout water, exercise, bmi;
        
        weather=(RelativeLayout)findViewById(R.id.weather);
       water=(RelativeLayout)findViewById(R.id.water);
        exercise=(RelativeLayout)findViewById(R.id.exercise);
        bmi=(RelativeLayout)findViewById(R.id.bmi);
        
        
        weather.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent =new Intent(getApplicationContext(),Weather.class);
				startActivity(intent);
			}
		});
        
      
        
        exercise.setOnClickListener(new View.OnClickListener() {
			
     			@Override
     			public void onClick(View v) {
     				// TODO Auto-generated method stub
     				
     				Intent intent =new Intent(getApplicationContext(),Exercise.class);
     				startActivity(intent);
     				
     			}
     		});
             
        water.setOnClickListener(new View.OnClickListener() {
			
     			@Override
     			public void onClick(View v) {
     				// TODO Auto-generated method stub
     				
     				Intent intent =new Intent(getApplicationContext(),Water.class);
     				startActivity(intent);
     			}
     		});
             
        bmi.setOnClickListener(new View.OnClickListener() {
			
     			@Override
     			public void onClick(View v) {
     				// TODO Auto-generated method stub
     				
     				Intent intent =new Intent(getApplicationContext(),Bmi.class);
     				startActivity(intent);
     			}
     		});
             
        
        
        
//날짜 표시
		todayText = (TextView) findViewById(R.id.today);

	

		Calendar calendar = Calendar.getInstance();
		tYear = calendar.get(Calendar.YEAR);
		tMonth = calendar.get(Calendar.MONTH);
		tDay = calendar.get(Calendar.DAY_OF_MONTH);

		Calendar dCalendar = Calendar.getInstance();
		updateDisplay();
//날짜 표시	
		
		
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);

    
        
        if (mIsRunning) {
            bindStepService();
        }
        
        mStepValueView     = (TextView) findViewById(R.id.step_value);
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        mCaloriesValueView = (TextView) findViewById(R.id.calories_value);
  
      /*  mIsMetric = mPedometerSettings.isMetric();
        ((TextView) findViewById(R.id.distance_units)).setText(getString(
                mIsMetric
                ? R.string.kilometers
                : R.string.miles
        ));*/

     
    }

    
    @Override
    protected void onPause() {
        if (mIsRunning) {
            unbindStepService();
        }
        super.onPause();
      
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
    
  


    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        mIsRunning = true;
        startService(new Intent(Pedometer.this,
                StepService.class));
    }
    
    private void bindStepService() {
        bindService(new Intent(Pedometer.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        mIsRunning = false;
        if (mService != null) {
            stopService(new Intent(Pedometer.this,
                  StepService.class));
        }
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            mDistanceValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);              
                stateEditor.putFloat("distance", 0);              
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
            .setIcon(android.R.drawable.ic_media_pause)
            .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
            .setIcon(android.R.drawable.ic_media_play)
            .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setShortcut('8', 's')
        .setIntent(new Intent(this, Settings.class));
        menu.add(0, MENU_QUIT, 0, R.string.quit)
        .setIcon(android.R.drawable.ic_lock_power_off)
        .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                resetValues(true);
                return true;
            case MENU_QUIT:
                resetValues(false);
                stopStepService();
                finish();
                return true;
        }
        return false;
    }
 
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
	
    };
    
    private static final int STEPS_MSG = 1;
    private static final int DISTANCE_MSG = 2;
    private static final int CALORIES_MSG = 3;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
          
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0"+"   km");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)+"   km"
                        );
                    }
                    break;
          
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0"+"   caloy");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue+"   calory");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
  
    //날짜표시
	private void updateDisplay() {

		todayText
				.setText(String.format("%d년 %d월 %d일", tYear, tMonth + 1, tDay));

	}
    //날짜표시
}