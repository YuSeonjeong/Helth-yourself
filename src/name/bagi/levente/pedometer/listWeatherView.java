package name.bagi.levente.pedometer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * �̺κ��� ������ ����Ʈ�信 �ѷ��ִ� �����
 * @author Ans
 *
 */
class listWeatherView extends BaseAdapter{
	
	Weather main;
	String[] day,time,temp,wind,hum,weather;
	Context mContext;
	String temp_data[]=new String [15];	//�ӽ÷� ������� nullpointexception ����
	
	public listWeatherView(Context context){
		mContext = context;
	}
	
	public void setDay(String[] data){
		day=data;
	}
	public void setTime(String[] data){
		time=data;
	}
	public void setTemp(String[] data){	
		temp=data;
	}
	//public void setWind(String[] data){
	//	wind=data;
	//}
	//public void setHum(String[] data){
	//	hum=data;
	//}
	public void setWeather(String[] data){
		weather=data;
	}
	
	@Override
	public int getCount() {
		return temp_data.length;		//����Ʈ���� ����
	}

	@Override
	public Object getItem(int position) {
		return temp_data[position];		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parents) {
		
		
		showWeather layout=null;	
		
		
		if(convertView!=null){	//��ũ�ѷ� �Ѿ �並 ������ �ʰ� ����
			layout=(showWeather)convertView;
		}
		else{
			layout=new showWeather(mContext.getApplicationContext());	//���̾ƿ� ����
			
		}
		
	if(Weather.updated){	//���������� �޾ƿ�����
		try{
				layout.setDate(day[position]);	//���̾ƿ����� �ѷ���
				layout.setTime(time[position]);
				layout.setTemp("  �µ�"+temp[position]);
			//	layout.setWind(wind[position]);
			//	layout.setHum("����"+hum[position]);
				layout.setWeather(weather[position],   time[position]);
		}catch(Exception e){
			Log.d("getview", String.valueOf(e));
			}
		}
		
		return layout;
	}

}
