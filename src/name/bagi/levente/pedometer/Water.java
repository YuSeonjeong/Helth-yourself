package name.bagi.levente.pedometer;



import java.util.Calendar;

import com.android.kyscholbus.schoolbus_out;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Water extends Activity {
	
	
	public static final String KEY_MY_PREFERENCE = "my_preference";
	
	RelativeLayout pictureLayout;
	MyGraphicView view;
	static float scaleX = 1, scaleY = 1;

	Button alarm;
	ImageView pluse, minuse;
	ImageView imageView2;
	//imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9;
	TextView today;
	String filName;
	
	private TextView watertext;
	static Integer num1 = 0;
	private TextView todayText;


	private int tYear;
	private int tMonth;
	private int tDay;

	private long d;
	private long t;
	private long r;


	static final int DATE_DIALOG_ID = 0;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_water);
		
		
		view = (MyGraphicView) new MyGraphicView(this);
		pictureLayout = (RelativeLayout) findViewById(R.id.pictureLayout);
		pictureLayout.addView(view);

		
		
		alarm = (Button) findViewById(alarm);
		pluse = (ImageView) findViewById(R.id.pluse);
		minuse = (ImageView) findViewById(R.id.minuse);
		watertext = (TextView) findViewById(R.id.watertext);
		
		
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		/*imageView3 = (ImageView) findViewById(R.id.imageView3);
		imageView4 = (ImageView) findViewById(R.id.imageView4);
		imageView5 = (ImageView) findViewById(R.id.imageView5);
		imageView6 = (ImageView) findViewById(R.id.imageView6);
		imageView7 = (ImageView) findViewById(R.id.imageView7);
		imageView8 = (ImageView) findViewById(R.id.imageView8);
		imageView9 = (ImageView) findViewById(R.id.imageView9);*/


		today=(TextView)findViewById(R.id.today);
		
		
		
		alarm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),alarm.class);
				startActivity(intent);
			}
		});
		
		
		today.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				num1=0;
				watertext.setText(num1.toString() + "/8");
				
					scaleX = 1f;
				
					scaleY = 1f;
				view.invalidate();
				
			}
		});
		
		
		SharedPreferences settings = getSharedPreferences("WW", 0);

		if (settings.contains("key1")) {
			String save = settings.getString("key1", "");
			watertext.setText(save);
		  
			
			
			

			
		
		
		
		pluse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

	
				num1++;

				scaleX = scaleX + 1.3f;
				scaleY = scaleY + 1.3f;
				if (scaleX >= 11.4)
					scaleX = 11.4f;
				if (scaleY >= 11.4)
					scaleY = 11.4f;
				view.invalidate();
				
				/*if(num1==1){
					imageView2.setVisibility(android.view.View.VISIBLE);
					
				}
				if(num1==2){
					imageView2.setVisibility(android.view.View.VISIBLE);
					imageView3.setVisibility(android.view.View.VISIBLE);	
				}
				

				if(num1==3){
					imageView3.setVisibility(android.view.View.VISIBLE);	
					imageView4.setVisibility(android.view.View.VISIBLE);
					
				}
				if(num1==4){
					imageView4.setVisibility(android.view.View.VISIBLE);
					imageView5.setVisibility(android.view.View.VISIBLE);	
				}
				

				if(num1==5){
					imageView5.setVisibility(android.view.View.VISIBLE);	
					imageView6.setVisibility(android.view.View.VISIBLE);
					
				}
				if(num1==6){
					imageView6.setVisibility(android.view.View.VISIBLE);
					imageView7.setVisibility(android.view.View.VISIBLE);	
				}

				if(num1==7){
					imageView7.setVisibility(android.view.View.VISIBLE);	
					imageView8.setVisibility(android.view.View.VISIBLE);
					
				}
				if(num1==8){
					imageView8.setVisibility(android.view.View.VISIBLE);
					imageView9.setVisibility(android.view.View.VISIBLE);	
				}*/
				
				watertext.setText(num1.toString() + "/8");

				String str = watertext.getText().toString();
				
				SharedPreferences settings = getSharedPreferences("WW", 0);
				SharedPreferences.Editor prefEditor = settings.edit();
				prefEditor.putString("key1", str);
				// prefEditor.putInt("cnt_1",1);
				prefEditor.commit();
				

			
				
				}
					
				});
		
				
				
				
	
		minuse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(num1==0){
					imageView2.setVisibility(android.view.View.INVISIBLE);
					
				}
			if (num1 > 0) {
					num1--;
					scaleX = scaleX - 1.3f;
					scaleY = scaleY - 1.3f;
					if (scaleX <= 1)
						scaleX = 1f;
					if (scaleY <= 1)
						scaleY = 1f;
					view.invalidate();
					
					
					/*if(num1==1){
						//imageView2.setVisibility(android.view.View.INVISIBLE);
						imageView3.setVisibility(android.view.View.INVISIBLE);	
					}
					if(num1==2){
						//imageView3.setVisibility(android.view.View.INVISIBLE);
						imageView4.setVisibility(android.view.View.INVISIBLE);
					}
					

					if(num1==3){
						//imageView4.setVisibility(android.view.View.INVISIBLE);
						imageView5.setVisibility(android.view.View.INVISIBLE);	
					}
					if(num1==4){
						//imageView5.setVisibility(android.view.View.INVISIBLE);	
						imageView6.setVisibility(android.view.View.INVISIBLE);
					}
					

					if(num1==5){
						//imageView6.setVisibility(android.view.View.INVISIBLE);
						imageView7.setVisibility(android.view.View.INVISIBLE);	
					}
					if(num1==6){
						//imageView7.setVisibility(android.view.View.INVISIBLE);	
						imageView8.setVisibility(android.view.View.INVISIBLE);
					}

					if(num1==7){
						//imageView8.setVisibility(android.view.View.INVISIBLE);
						imageView9.setVisibility(android.view.View.INVISIBLE);	
						
					}
					if(num1==8){
						imageView9.setVisibility(android.view.View.INVISIBLE);	
					}
					*/
					
					watertext.setText(num1.toString() + "/8");
					
						String str = watertext.getText().toString();

						SharedPreferences settings = getSharedPreferences("WW", 0);
						SharedPreferences.Editor prefEditor = settings.edit();
						prefEditor.putString("key1", str);
						// prefEditor.putInt("cnt_1",1);
						prefEditor.commit();
						return;
					}
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
	}}
	
	 private Button findViewById(Button alarm2) {
		// TODO Auto-generated method stub
		return null;
	}

		//날짜표시
		private void updateDisplay() {

			todayText
					.setText(String.format("%d년 %d월 %d일", tYear, tMonth + 1, tDay));

		}
	    //날짜표시
		
		
		private static class MyGraphicView extends View {
			public MyGraphicView(Context context) {
				super(context);
			}

			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);

				int cenX = this.getWidth();
				int cenY = this.getHeight();
				canvas.scale(scaleX, scaleY, cenX, cenY);

				Bitmap picture = BitmapFactory.decodeResource(getResources(),
						R.drawable.cupwater);

				int picX = (this.getWidth() - picture.getWidth());
				int picY = (this.getHeight() - picture.getHeight()/2);
				canvas.drawBitmap(picture, picX, picY, null);

				picture.recycle();

			}
		}

		
	
	}
	
	
	
