package name.bagi.levente.pedometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Intro extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
        Handler handler =new Handler();
		
		//postDelayed (실행할 동작, 실행시간) 메소드 사용
		handler.postDelayed(new Runnable ()
		{public void run(){
			Intent intent =new Intent(Intro.this,Pedometer.class);
			startActivity(intent);
			finish();
		}
			
		},1550);
	}
}
