package name.bagi.levente.pedometer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;




/*public class Weather extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
	}
}*/


@SuppressLint("SdCardPath")
public class Weather extends Activity  implements OnClickListener,OnItemSelectedListener  {
	
	
	
	/** The view to show the ad. */
	
	public static int handler_timeout = 1001;
	
	String locationProvider=null; 
	String stringforshare=null;
	
	int backEvt=0;  
	
	static ProgressDialog progress;	
	MyLocationListener listener;
	LocationManager manager;
	listWeatherView listadapter;		//���������� �ѷ��ִ� ����Ʈ��� �����
	ArrayAdapter<String> sidoAdapter,gugunAdapter,dongAdapter;	//�õ�,����,���� ������ �ѷ��ִ� ���ǳʿ� �����
	Spinner sidoSpinner,gugunSpinner,dongSpinner;	//�õ����ǳ�,�������ǳ�,���齺�ǳ�
	Button getBtn,gpsBtn;		//���� �������� ��ư,gps ��ư
	TextView text;		//���������� ��ǥ�ð�����
	ListView listView1;	//���������� �ѷ��� ����Ʈ��
	
	
	//DB�� ����
	String [] sidonum,Nsidonum,sidoname;	//�õ� �ڵ�,���� table�� �õ� �ڵ�,�õ� �̸�
	String [] gugunnum,Ngugunnum,gugunname;	//���� �ڵ�,���� table�� ���� �ڵ�,���� �̸�
	String [] dongnum,dongname;	//�� �ڵ�,�� �̸�
	String [] gridx,gridy,id;	//x��ǥ,y��ǥ,id��
	String dbFile="weatherdb.db3";	
	String dbFolder="/data/data/name.bagi.levente.pedometer/datebases/";
	String numDong,numSido,numGugun;	//���������� ������ ���� �����ڵ�,�õ� �ڵ�,���� �ڵ尡 ����Ǵ� ����
	static SQLiteDatabase db;	//���
	
	//�ļ��� ����
	int data=0;			//�̰� �Ľ��ؼ� array�� ������ ����
	public static boolean updated;	//�̰� �������� �Ѹ������� �÷���
	String sCategory,sTm;	//����,��ǥ�ð�
	String [] sHour,sDay,sTemp,sWdKor,sReh,sWfKor;	//�����ð�,��¥,�µ�,ǳ��,����,����
	boolean bCategory,bTm,bHour,bDay,bTemp,bWdKor,bReh,bItem,bWfKor;	//���� ������ ���� �÷��׵�
	boolean tCategory,tTm,tItem;	//�̰� text�� �Ѹ������� �÷���
	boolean parserEnd;   //�����ڵ��� �ļ� �� flag
	static String sSido_name,sDong_name,sGugun_name;	//gps�� �����ڵ��� �ּҸ� �ļ��ؼ� ������ ����
	
	String tempDong;//="4215025000";	//�⺻dongcode
	static double latitude,longitutde;	//������ �浵�� ������ ����
	Handler handler,handler2;	//�������� �ڵ鷯,�����ڵ��ļ��� �ڵ鷯
	final int tableSido=1,tableGugun=2,tableDong=3; //�̰� switch case������ ������ ���� ����
	
	
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		checkDB();
        init();
        
        queryData(tableSido);	//�õ� DB ������ ����
        
     
         
	}
	
	@Override
	protected void onDestroy() {
		closeDatabase();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		openDatabase(dbFolder+dbFile);	//db�� ����� �������� db�� ������ �´�
		super.onResume();
	}

	

	/**
	 * DB�� �ִ��� üũ
	 * @author Ans
	 */
	private void checkDB() {
		 
        try {
        	boolean bResult = isCheckDB(getBaseContext());	// DB�� �ִ���?
        	
        	if(!bResult){	// DB�� ������
        		copyDB(getBaseContext());	//bd����
        		Toast.makeText(getApplicationContext(), "DB",Toast.LENGTH_SHORT).show();
        	}else{			//DB�� ������
        		//Toast.makeText(getApplicationContext(), "�̹� DB���־��",Toast.LENGTH_SHORT).show();
        		
        	}
        	
		} catch (Exception e) {	//���ܹ߻���

			Toast.makeText(getApplicationContext(), "����",Toast.LENGTH_SHORT).show();
		}
		
	}


	public void init() {
		handler=new Handler();	//������&�ڵ鷯ó��
		handler2=new Handler();	//������&�ڵ鷯ó��
		
		
		
		sidoSpinner=(Spinner)findViewById(R.id.sidospinner);	//�õ��� ���ǳ�
		gugunSpinner=(Spinner)findViewById(R.id.gugunspinner); 	//������ ���ǳ�
		dongSpinner=(Spinner)findViewById(R.id.dongspinner);	//����� ���ǳ�
		listView1=(ListView) findViewById(R.id.listView1);		//�������� ����Ʈ��
		
		bCategory=bTm=bHour=bTemp=bWdKor=bReh=bDay=bWfKor=tCategory=tTm=tItem=false;	//�ο����� false�� �ʱ�ȭ������
		listadapter=new listWeatherView(getApplicationContext());	//����Ʈ�並 ���������
		listView1.setAdapter(listadapter);					//����Ϳ� ����Ʈ�並 ����
		text=(TextView) findViewById(R.id.textView1);	//�ؽ�Ʈ ��ü����
		getBtn=(Button) findViewById(R.id.getBtn);		//��ư ��ü����
		gpsBtn=(Button) findViewById(R.id.gpsBtn);		//��ư ��ü����
		//mapBtn=(Button) findViewById(R.id.mapBtn);		
		
		sidoSpinner.setOnItemSelectedListener(this);
		gugunSpinner.setOnItemSelectedListener(this);
		dongSpinner.setOnItemSelectedListener(this);
		
		getBtn.setOnClickListener(this);
		gpsBtn.setOnClickListener(this);
		//mapBtn.setOnClickListener(this);
		
		
	
	}
	

	/**
	 * DB�� �������� �κ�
	 * �õ�, ����, ���� ��� ���̺��� ���ڵ尡 �ٸ��⶧���� case���� ��µ�
	 * �ڵ尡 �ʹ� �����;;
	 * 
	 * @author Ans
	 * @param table
	 */
	@SuppressWarnings("deprecation")
	private void queryData(final int table) {
		
		openDatabase(dbFolder+dbFile);	//db�� ����� �������� db�� ������ �´�
		String sql=null;				//sql��ɾ ������ ����
		Cursor cur = null;				//db������ Ŀ��
		int Count;						//db���� �� ����
		
		switch(table){
		
		case tableSido:
			sql="select sido_num, sido_name from t_sido";	//�õ� ���̺��� �õ��ڵ�� �õ��̸�
			cur=db.rawQuery(sql, null);						//Ŀ���� ����
			break;
		case tableGugun:									//���� ���̺��� �õ����� ���õ� �õ��� ����������
			sql="select sido_num, gugun_num, gugun_name from t_gugun where sido_num = "+numSido;	
			cur=db.rawQuery(sql, null);
			break;
		case tableDong:										//���� ���̺� ���õ� �����ڵ�� ���ؼ� 
			sql="select gugun_num, dong_num, dong_name, gridx, gridy, _id from t_dong where gugun_num = "+numGugun;
			cur=db.rawQuery(sql, null);
			break;
		default:
			break;
		}
		
		Count=cur.getCount();	//db�� ������ ����
		
		switch(table){
		
		case tableSido:
			
			sidoname=new String[Count];	//������ŭ �迭�� �����
			sidonum=new String[Count];
			
			if(cur!=null){	//�̺κ��� Ŀ���� �����͸� �о�ͼ� ������ �����ϴ� �κ�
				cur.moveToFirst();
				startManagingCursor(cur);
				for(int i=0;i<Count;i++){
					sidonum[i]=cur.getString(0);
					sidoname[i]=cur.getString(1);
					cur.moveToNext();
				}
				//������ ������ �Ǿ����� ���ǳʸ� ����� �ѷ�����
				sidoAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, sidoname);	//����͸� ���� ���ǳʿ� donglist �־���
	    		sidoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown����
			    sidoSpinner.setAdapter(sidoAdapter);	//���ǳ� ����
			    
			}
			break;
		case tableGugun:	//������ �����۾�
			
			Nsidonum=new String[Count];
			gugunnum=new String[Count];
			gugunname=new String[Count];
			if(cur!=null){
				cur.moveToFirst();
				startManagingCursor(cur);
				for(int i=0;i<Count;i++){
					Nsidonum[i]=cur.getString(0);
					gugunnum[i]=cur.getString(1);
					gugunname[i]=cur.getString(2);
					cur.moveToNext();
				}
				gugunAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, gugunname);	//����͸� ���� ���ǳʿ� donglist �־���
				gugunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown����
			    gugunSpinner.setAdapter(gugunAdapter);	
			    
			}
			break;
			
		case tableDong:		//���鵵 �����۾�
			
			Ngugunnum=new String[Count];
			dongnum=new String[Count];
			dongname=new String[Count];
			gridx=new String[Count];
			gridy=new String[Count];
			id=new String[Count];
			if(cur!=null){
				cur.moveToFirst();
				startManagingCursor(cur);
				for(int i=0;i<Count;i++){
					Ngugunnum[i]=cur.getString(0);
					dongnum[i]=cur.getString(1);
					dongname[i]=cur.getString(2);
					gridx[i]=cur.getString(3);
					gridy[i]=cur.getString(4);
					id[i]=cur.getString(5);
					cur.moveToNext();
				}
	//			cur.close();
				dongAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, dongname);	//����͸� ���� ���ǳʿ� donglist �־���
				dongAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown����
			    dongSpinner.setAdapter(dongAdapter);	
			}
			break;
			
		default:
			break;
		}	
		
	}
	
		/**
		 * �̺κ��� db�� �����ִ� �κ�
		 * 
		 * @author Ans
		 * @param databaseFile
		 */
		public static void openDatabase(String databaseFile) {
		    	
		    	try {
		    		db = SQLiteDatabase.openDatabase(	//������ ������ db�� �����ͼ� �а�,���� �����ϰ� �о�´�
		    	    				databaseFile, null, SQLiteDatabase.OPEN_READWRITE);
			
		    	} catch (SQLiteException ex) {
		    		
		    	}
		    }
		
		/**
		 * @author Ans
		 * �̺κ��� ��� �ݴ´�    
		 */
		public static void closeDatabase() {
				try {
					// close database
					db.close();
				} catch(Exception ext) {
					ext.printStackTrace();
					
				}
		    }
		
		
		
		/**
		 * @author Ans	
		 * ��� ���� �ִ��� üũ
		 */
		public boolean isCheckDB(Context mContext){
				
				
			String filePath = dbFolder+dbFile;
			File file = new File(filePath);
			  
			   if (file.exists()) {	//db������ ������ ������ true
				   return true;
			   }
			   
			   return false;		//�ƴ� default�� false�� ��ȯ
		}
			
			// DB�� �����ϱ�
			// assets�� /db/xxxx.db ������ ��ġ�� ���α׷��� ���� DB�������� �����ϱ�
		/**
		 * @author Ans
		 * ��� ������ ������
		 * 
		 */
		public void copyDB(Context mContext){	//���� db�� ���� ��� ���縦 �ؾߵȴ�.
				 
			  AssetManager manager = mContext.getAssets();	//asserts �������� ������ �б����� ���ܴ�.���� ��
			  String folderPath = dbFolder;	//db����			//�ϴ� DB�� �� ������ ������ �Ͽ����� ��߰���?
			  String filePath = dbFolder+dbFile; //db������ ���ϰ��
			  File folder = new File(folderPath);	
			  File file = new File(filePath);
			  
			  FileOutputStream fos = null;	
			  BufferedOutputStream bos = null;
			  try {
			   InputStream is = manager.open("db/" + "weather.db3");	//�ϴ� asserts������ db�������� db�� ��������
			   BufferedInputStream bis = new BufferedInputStream(is);

			   	   if (folder.exists()) {			//�츮�� �����Ϸ��� db������ �̹� ������ �Ѿ��
			   	   }else{						
					   folder.mkdirs();				//������� ������ ������
				   }
			   	   
			   
				   if (file.exists()) {				//������ �ִٸ� 
					   file.delete();				//�ϴ� �����
					   file.createNewFile();		//�� ������ ������
				   }
				   
				   fos = new FileOutputStream(file);	
				   bos = new BufferedOutputStream(fos);
				   int read = -1;
				   byte[] buffer = new byte[1024];	//buffer�� 1024byte�ϱ� 1k�� �������ְ�
				   while ((read = bis.read(buffer, 0, 1024)) != -1) {	//db������ �о buffer�� �ְ�
				    bos.write(buffer, 0, read);							//buffer���� ���� ���� ���Ͽ� ����
				   }													//�������ش� �Ǵµ� ��Ƴ�;;

				   bos.flush();
				   bos.close();
				   fos.close();
				   bis.close();
				   is.close();

			   } catch (IOException e) {
			   
			  }	
		}
			

	
	/**
	 * ���û�� �����Ͽ� �����ް� �ѷ��ִ� ������
	 * 
	 * @author Ans
	 *
	 */
		
	class GetWeatherThread extends Thread{	//���û ������ ���� ������
		/**
		 * ���û�� �����ϴ� ������
		 * �̰����� Ǯ�ļ��� �̿��Ͽ� ���û���� ������ �޾ƿ� ������ array������ �־���
		 * @author Ans
		 */
		public void run(){
			
			
			try{
				//timerDelayRemoveDialog(5000,progress);
				updated=false;
				sHour=new String[100];	//�����ð�(��� 15���ۿ� �ȵ������� �˳��ϰ� 20���� ��Ƴ���)
				sDay=new String[100];	//��¥
				sTemp=new String[100];	//����µ�
				sWdKor=new String[100];	//ǳ��
				sReh=new String[100];	//����
				sWfKor=new String[100];	//����
				data=0;
				XmlPullParserFactory factory=XmlPullParserFactory.newInstance();	//�̰��� Ǯ�ļ��� ����ϰ� �ϴ°�
				factory.setNamespaceAware(true);									//�̸��� ���鵵 �ν�
				XmlPullParser xpp=factory.newPullParser();							//Ǯ�ļ� xpp��� ��ü ����
				
				String weatherUrl="http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="+numDong;	//�̰��� ���ûURL
				URL url=new URL(weatherUrl);		//URL��ü����
				InputStream is=url.openStream();	//������ url�� inputstream�� �־� ������ �ϰԵȴ�.
				xpp.setInput(is,"UTF-8");			//�̷��� �ϸ� ������ �ȴ�. ���������� utf-8��
				
				int eventType=xpp.getEventType();	//Ǯ�ļ����� �±������� �����´�.
					
				while(eventType!=XmlPullParser.END_DOCUMENT){	//������ ���� �ƴҶ�
					
					switch(eventType){
					case XmlPullParser.START_TAG:	//'<'�����±׸� ��������
						
						if(xpp.getName().equals("category")){	//�±׾��� �̸��� ī�װ��ϋ� (�̰� �����̸��� ���´�)
							bCategory=true;	
							
						} if(xpp.getName().equals("pubDate")){		//��ǥ�ð�����
							bTm=true;
						
						} if(xpp.getName().equals("hour")){		//�����ð�
							bHour=true;
							
						} if(xpp.getName().equals("day")){		//������(���� ���� ��)
							bDay=true;
							
						} if(xpp.getName().equals("temp")){		//�����ð����� ����µ�
							bTemp=true;
							
						} if(xpp.getName().equals("wdKor")){	//ǳ������
							bWdKor=true;
							
						} if(xpp.getName().equals("reh")){		//��������
							bReh=true;
							
						} if(xpp.getName().equals("wfKor")){	//��������(����, ��������, ��������, �帲, ��, ��/��, ��)
							bWfKor=true;
							
						}
						
						break;
					
					case XmlPullParser.TEXT:	//�ؽ�Ʈ�� ��������
												//�ռ� �����±׿��� ���������� ������ �÷��׸� true�� �ߴµ� ���⼭ �÷��׸� ����
												//������ ������ �־��� �Ŀ� �÷��׸� false��~
						if(bCategory){				//�����̸�
							sCategory=xpp.getText();
							bCategory=false;
						} if(bTm){					//��ǥ�ð�
							sTm=xpp.getText();	
							bTm=false;	
						}  if(bHour){				//�����ð�			
							sHour[data]=xpp.getText();
							bHour=false;
						}  if(bDay){				//������¥
							sDay[data]=xpp.getText();
							bDay=false;
						}  if(bTemp){				//����µ�
							sTemp[data]=xpp.getText();
							bTemp=false;
						}  if(bWdKor){				//ǳ��
							sWdKor[data]=xpp.getText();
							bWdKor=false;
						}  if(bReh){				//����
							sReh[data]=xpp.getText();
							bReh=false;
						} if(bWfKor){				//����
							sWfKor[data]=xpp.getText();
							bWfKor=false;
							}
						break;
						
					case XmlPullParser.END_TAG:		//'</' �����±׸� ������ (�̺κ��� �߿�)
						
						if(xpp.getName().equals("item")){	//�±װ� ������ ������ �±��̸��� item�̸�(�̰� ���� ������ ��
							tItem=true;						//���� �̶� ��� ������ ȭ�鿡 �ѷ��ָ� �ȴ�.
							view_text();					//�ѷ��ִ� ��~
						} if(xpp.getName().equals("pubDate")){	//�̰� ��ǥ�ð������ϱ� 1���������Ƿ� �ٷ� �ѷ�����
							tTm=true;
							view_text();
						} if(xpp.getName().equals("category")){	//�̰͵� ���������� �ٷ� �ѷ��ָ� ��
							tCategory=true;
							view_text();
						} if(xpp.getName().equals("data")){	//data�±״� �����ð����� ���������� �ϳ����̴�.
							data++;							//�� data�±� == ���� ���� �׷��Ƿ� �̶� array�� ����������
						}
						break;
					}
					eventType=xpp.next();	//�̰� ���� �̺�Ʈ��~
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		/**
		 * �� �κ��� �ѷ��ִ°� 
		 * �Ѹ��°� �ڵ鷯��~
		 * @author Ans
		 */
		private void view_text(){
			
			handler.post(new Runnable() {	//�⺻ �ڵ鷯�ϱ� handler.post�ϸ��
				
				@Override
				public void run() {
					
					
					if(tCategory){	//�����̸� ���Դ�
						text.setText("����:   "+sCategory+"\n");
						tCategory=false;
					}if((tTm)&&(sTm.length()>11)){		//��ǥ�ð� ���Դ�
						text.setText(text.getText()+"��ǥ�ð�:   "+sTm+"\n");
						tTm=false;
					}if(tItem){		//������ �� �о���
						
						for(int i=0;i<data;i++){	//array�� �Ǿ������� for������
							if(sDay[i]!=null){		//�̰� null integer ���� ������ ����(String�� null�� ����������intger�� �ȵǴϱ�)
								if(sDay[i].equals("0")){	//��ǥ�ð��� 0�̸� ���� 
									sDay[i]="   ��¥: "+"���� ";
									
								}else if(sDay[i].equals("1")){	//1�̸� ����
									sDay[i]="   ��¥: "+"���� ";
								
								}else if(sDay[i].equals("2")){	//2�̸� ��
									sDay[i]="   ��¥: "+"�� ";
									
								}
							}
							
						}
						
						listadapter.setDay(sDay);	//���������� listview�� �ѷ�����
						listadapter.setTime(sHour);
					    listadapter.setTemp(sTemp);
					//	listadapter.setWind(sWdKor);
					//	listadapter.setHum(sReh);
					    listadapter.setWeather(sWfKor);
					    updated=true;					//������ ������� flag�� true��
						listadapter.notifyDataSetChanged();	//����Ʈ�� ������Ʈ
						tItem=false;
						data=0;		//������ ������ ���������� �Ǹ� ó������ �����ؾ߰���?
						stringforshare=sCategory+"�� ���� ������   "+sWfKor[0]+  "   �µ���"+sTemp[0]+"�� �Դϴ�.";
						progress.dismiss();	//���α׷��� ����
					}
					
					
				}
			});
		}
	}
	/**
	 * �̰����� GPS����� �����ʵ��� ������ �ش�
	 * @author Ans
	 */
	 final public void getMyLocation() {
		long minTime=60000;		//every 60sec
		float minDistance=1;	//if moved over 1miter
		
		manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);	//GPS ����
		listener=new MyLocationListener();	//������ ������
		locationProvider = LocationManager.NETWORK_PROVIDER;
		//locationProvider = LocationManager.GPS_PROVIDER;		//���Ŀ��� �̰ɷ�~
	    Handler mHandler=new Handler();
		timerDelayRemoveDialog(5000,progress,mHandler);
		manager.requestLocationUpdates(locationProvider,minTime ,minDistance, listener);//������
	 }
	 
	 public void timerDelayRemoveDialog(long time, final Dialog d,Handler mHandler){
		   mHandler.postDelayed(new Runnable() {
		        public void run() { 
		        	//Toast.makeText(getApplicationContext(), "��ġ�� ã�� �� �����ϴ�. ��Ʈ��ũ�� Ȯ���ϼ���", Toast.LENGTH_SHORT).show();
		         	d.dismiss();         
		        }
		    }, time); 
		} 
	
	
	/**
	 * �̺κ��� �����ڵ��ؼ� �ļ��ϴ� ��
	 * @author Administrator
	 *
	 */
	class GetAddressThread extends Thread{	//������ ��Ȯ�� �����ڵ��� ���� daum api�̿�
		
		String key="8b0153f352c2bdfbb1aacfb35f83585c77ceb38f";
		String url="http://apis.daum.net/local/geo/coord2addr?apikey=";
	    
		public void run(){
			
		try{
			XmlPullParserFactory fa = XmlPullParserFactory.newInstance();
			fa.setNamespaceAware(true);		
			XmlPullParser xp=fa.newPullParser();
			
			String geocode=url+key+"&longitude="+longitutde+"&latitude="+latitude+"&format=simple&output=xml&inputCoordSystem=WGS84";
			URL url = new URL(geocode);
				//URL��ü����
			InputStream is = url.openStream();
			
			xp.setInput(is,"UTF-8");
			int eventType=xp.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT){	//������ ���� �ƴҶ�
					
					switch(eventType){
					case XmlPullParser.START_TAG:	//'<'�����±׸� ��������
					
						String tag = xp.getName(); 
		                if (tag.compareTo("rcode") == 0) { //�ļ��� result �±׸� ������ x�� y�� �Ӽ� ���� ���� longitude,latitude�� ����.
		                	sSido_name=xp.getAttributeValue(null, "name1");
		                	sGugun_name=xp.getAttributeValue(null, "name2");
		                	sDong_name=xp.getAttributeValue(null, "name3");
		                	parserEnd=true;
		                }
						break;
						}
					eventType=xp.next();
					}
			}catch(Exception e){
				e.printStackTrace();
			}
		if(parserEnd){
			
			showtext();
			}
		}
		
		
		private void showtext(){
			handler2.post(new Runnable() {
				
				@Override
				public void run() {
					
					
					try{
						//�� ��������
						//Toast.makeText(getApplicationContext(),"���� ��ġ�� "+sSido_name+" "+sGugun_name+" "+sDong_name, Toast.LENGTH_SHORT).show();
						String sql="select gugun_num, dong_num, dong_name from t_dong where dong_name = "+"'"+sDong_name+"'";
						Cursor cur=db.rawQuery(sql, null);
						int cnt,cnt2;
						cnt=cur.getCount();
						String [] dongNumTemp=new String[cnt];
						String [] gugunNumTemp=new String[cnt];
						//�������� ������->���̸��� �ѹ� �ٲ㺸��
						if(cnt==0){
							int cut=sDong_name.length();
							String s1=sDong_name.substring(0, cut-2);
							String s2=sDong_name.substring(cut-2);
							sDong_name=s1+"��"+s2;
							//Toast.makeText(getApplicationContext(),"���� ��ġ�� "+sSido_name+" "+sGugun_name+" "+sDong_name, Toast.LENGTH_SHORT).show();
							sql="select gugun_num, dong_num, dong_name from t_dong where dong_name = "+"'"+sDong_name+"'";
							cur=db.rawQuery(sql, null);
							cnt=cur.getCount();
						}
						//�������� �ѹ��� ã����
						if(cnt==1){
							cur.moveToFirst();
							numDong=cur.getString(1);
						}else if(cnt==0){//������ ������
							Toast.makeText(getApplicationContext(),"�������� ã���� �����ϴ�.", Toast.LENGTH_SHORT).show();
						}else{//�������� ������
							cur.moveToFirst();
							for(int i=0;i<cnt;i++){
								gugunNumTemp[i]=cur.getString(0);
								dongNumTemp[i]=cur.getString(1);
								cur.moveToNext();
							}
							String sql2="select sido_num, gugun_num, gugun_name from t_gugun where gugun_name = "+"'"+sGugun_name+"'";
							Cursor cur2=db.rawQuery(sql2, null);
							cnt2=cur2.getCount();
							String [] gugunNum2Temp=new String[cnt2];
							String [] sidoNumTemp=new String[cnt2];
							
							if(cnt2==0){
								Toast.makeText(getApplicationContext(), "���� ������ ã���� �����ϴ�.", Toast.LENGTH_SHORT).show();
							}else if(cnt2==1){
								cur2.moveToFirst();
								for(int i=0;i<cnt2;i++){
									gugunNum2Temp[i]=cur2.getString(1);
									sidoNumTemp[i]=cur2.getString(0);
									for(int j=0;j<cnt;j++){
										if(gugunNumTemp[j].equals(gugunNum2Temp[i].toString())){
											numDong=dongNumTemp[j];
											cur2.close();
											break;
										}
									}									
									cur.moveToNext();
									
									}
								cur2.close();
							}
						}
						GetWeatherThread getweatherthread=new GetWeatherThread();		//���������(UI ��������� system ���´�)
						if(getweatherthread!=null&&getweatherthread.isAlive()){
							Toast.makeText(getApplicationContext(), "getweatherthread.isAlive", Toast.LENGTH_SHORT).show();
							getweatherthread.interrupt();
						}
						getweatherthread.start();	//������ ����
					
					
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), sSido_name+" "+sGugun_name+" "+sDong_name+"�� ã�� �� �����ϴ�.", Toast.LENGTH_SHORT).show();
					
				}
				
					
					
					
				}
			});
			
		}
		
		
	}

	/**
	 * �̰����� ������ ���ǿ��� ������ �޾ƿ´�
	 * @author Ans
	 *
	 */
	class MyLocationListener implements LocationListener{
	
		@Override
		public void onLocationChanged(Location location) {
			
			latitude=location.getLatitude();//get latitude
			longitutde=location.getLongitude();//get longitutde
			try{
				manager.removeUpdates(listener);	//���ǿ��� ���� ����(�̺κ��� �ּ�ó���ϸ� �����ѰŴ�� ��� �޾ƿ�)
			}catch(Exception e){
				Log.d("locationlistener", String.valueOf(e));
			}
			
			
			GetAddressThread getaddressthread=new GetAddressThread();		//�����ڵ��� ������
			if(getaddressthread!=null&&getaddressthread.isAlive()){
				Toast.makeText(getApplicationContext(), "getaddressthread.isAlive", Toast.LENGTH_SHORT).show();
				getaddressthread.interrupt();
			}
			getaddressthread.start();	//������ ����
			
		}
			
		@Override
		public void onProviderDisabled(String provider) {
			
		}
	
		@Override
		public void onProviderEnabled(String provider) {
			
			
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
	}
	
	/**
	 * ��Ʈ��ũ ��밡������ check!!
	 * @return
	 */
	public boolean checkNetwork(){
		//��Ʈ��ũ��
		ConnectivityManager connectivitymanager;
		NetworkInfo mobile;
		NetworkInfo wifi;
		connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile=connectivitymanager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi=connectivitymanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		try{
			if(mobile.isConnected()||wifi.isConnected())
				return true;
			else
				return false;
		}catch(Exception e){
			if(wifi.isConnected())
				return true;
			else
				return false;
		}
		
		
		 
	}
	
	
	/**
	 * ��ư�� ���� ó��
	 */
	@Override
	public void onClick(View v) {
		backEvt=0;
		
		if(checkNetwork()==true)
		{
			switch(v.getId()){
			
			case R.id.getBtn:
				progress = ProgressDialog.show(this, "��ø� ��ٸ�����", "���������� �������� �ֽ��ϴ�.");
				
				numDong=tempDong;
				GetWeatherThread getweatherthread=new GetWeatherThread();
				getweatherthread.start();	//������ ����
				
				break;
			case R.id.gpsBtn:
				progress = ProgressDialog.show(this, "��ø� ��ٸ�����", "���������� �������� �ֽ��ϴ�.");
				
				getMyLocation();	//GPS setting
				break;
			/*case R.id.mapBtn:
				Intent mapintent=new Intent(this,mapview.class);
				startActivity(mapintent);
				break;*/
				
			default:
				break;
			}
		}else{
			Toast.makeText(getApplicationContext(), "��Ʈ��ũ�� ����Ǿ� ���� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
		}
		
		
		
	}

	/**
	 * ���ǳʿ� ���� ó��
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		backEvt=0;
		
		
		switch(parent.getId()){
		
		case R.id.sidospinner:
			numSido=sidonum[position];	//�õ��� ���õǸ� �ش� �ڵ带 ������ �ִ´�
			queryData(tableGugun);		//���� DB������~
			break;
		case R.id.gugunspinner:
			numGugun=gugunnum[position];	//������ ���õǸ� �ش� �ڵ带 ������
			queryData(tableDong);			//���� DB������~
			break;
		case R.id.dongspinner:
			tempDong=dongnum[position];
			numDong=tempDong;	//���õ� �����ڵ带 ������ ����
			break;
		default:
			break;
				
		}
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	



	

	
}



