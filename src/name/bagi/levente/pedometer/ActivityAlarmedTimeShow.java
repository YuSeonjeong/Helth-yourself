package name.bagi.levente.pedometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

public class ActivityAlarmedTimeShow extends Activity {
	TextView textViewAlarmedTime;
	Vibrator vide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmed_time_show_layout);
		vide = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		Vibrator_pattern(textViewAlarmedTime);
		
		textViewAlarmedTime = (TextView)findViewById(R.id.textViewAlarmedTime);
		Intent intent = getIntent();
		String time = intent.getStringExtra("time");
		String data = intent.getStringExtra("data");
		int reqCode = intent.getIntExtra("reqCode", 0);
		textViewAlarmedTime.setText(time+"\n"+data+"\n"+reqCode);

	}

	
	public void Vibrator_pattern(View v){

		long[] pattern = { 1000, 2000, 1000, 3000, 1000, 5000, 1000,2000,1000 };

		vide.vibrate(pattern, -1);
	}

}
