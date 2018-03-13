package name.bagi.levente.pedometer;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * ������ ������ �ѷ��ִ� ���Ͼ� ���̾ƿ�
 * weather.xml�� inflater�ߴ�
 * 
 *@author Ans
 */
public class showWeather extends LinearLayout {

	TextView Tdate;
	TextView Ttime;
	TextView Ttemp;
	TextView Twind;
	TextView Thum;
	TextView Tweather;
	ImageView Iweather;
	
	
	public showWeather(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public showWeather(Context context) {
		super(context);
		init(context);
		
	}
	
	
	
	

	
	
	public void setDate(String data) {
		Tdate.setText(data+" ");			//��¥
		

	}
	public void setTime(String data) {
		Ttime.setText(data+"��    ");			//�ð�
		

	}
	public void setTemp(String data) {
		Ttemp.setText(data+"��    ");			//�µ�
		

	}
	//public void setWind(String data) {
	//	Twind.setText(data+"ǳ ");			//�ٶ�
		

	//}
/*	public void setHum(String data) {
		Thum.setText(data+"% ");			//����
		

	}*/
	public void setWeather(String data) {
		Tweather.setText(data);	
		
		}
	
	private void weatherIcon(String data,int weather){
		if(weather>=6&&weather<=18){
		//�̰��� ����������
			if(data.toString().equals("����")){
				Iweather.setImageResource(R.drawable.sun);
			}else if(data.toString().equals("�帲")){
				Iweather.setImageResource(R.drawable.cloud);
			}else if(data.toString().equals("���� ����")){
				Iweather.setImageResource(R.drawable.suncloud);
			}else if(data.toString().equals("���� ����")){
				Iweather.setImageResource(R.drawable.suncloud);
			}else if(data.toString().equals("��")){
				Iweather.setImageResource(R.drawable.snow);
			}else if(data.toString().equals("��")){
				Iweather.setImageResource(R.drawable.rain);
			}else if(data.toString().equals("��/��")){
				Iweather.setImageResource(R.drawable.snowrain);
			}else{
				Iweather.setImageResource(R.drawable.icon);
			}if(data.toString()==null){
				Iweather.setImageResource(R.drawable.icon);
			}
		}else{
			if(data.toString().equals("����")){
				Iweather.setImageResource(R.drawable.moon);
			}else if(data.toString().equals("�帲")){
				Iweather.setImageResource(R.drawable.cloud);
			}else if(data.toString().equals("���� ����")){
				Iweather.setImageResource(R.drawable.mooncloud);
			}else if(data.toString().equals("���� ����")){
				Iweather.setImageResource(R.drawable.mooncloud);
			}else if(data.toString().equals("��")){
				Iweather.setImageResource(R.drawable.snow);
			}else if(data.toString().equals("��")){
				Iweather.setImageResource(R.drawable.rain);
			}else if(data.toString().equals("��/��")){
				Iweather.setImageResource(R.drawable.snowrain);
			}else{
				Iweather.setImageResource(R.drawable.icon);
			}if(data.toString()==null){
				Iweather.setImageResource(R.drawable.icon);
			}
			
			
		}
		

	}public void init(Context context){
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.weather,this,true);
		
		 Tdate=(TextView)findViewById(R.id.Tdate);
		 Ttime=(TextView)findViewById(R.id.Ttime);
		 Ttemp=(TextView)findViewById(R.id.Ttemp);
	//	 Twind=(TextView)findViewById(R.id.Twind);
		// Thum=(TextView)findViewById(R.id.Thum);
		 Tweather=(TextView)findViewById(R.id.Tweather);
		 Iweather=(ImageView) findViewById(R.id.Iweather);
		
	}public void setWeather(String data,String time) {
		Tweather.setText(data+" ");				//����
		int iTime=Integer.parseInt(time);
		weatherIcon(data,iTime);
	}
	
	//@SuppressLint("NewApi")
	

}
