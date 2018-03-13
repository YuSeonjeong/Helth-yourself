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
	listWeatherView listadapter;		//날씨정보를 뿌려주는 리스트뷰용 어댑터
	ArrayAdapter<String> sidoAdapter,gugunAdapter,dongAdapter;	//시도,구군,동면 정보를 뿌려주는 스피너용 어댑터
	Spinner sidoSpinner,gugunSpinner,dongSpinner;	//시도스피너,구군스피너,동면스피너
	Button getBtn,gpsBtn;		//날씨 가져오는 버튼,gps 버튼
	TextView text;		//날씨지역및 발표시각정보
	ListView listView1;	//날씨정보를 뿌려줄 리스트뷰
	
	
	//DB용 변수
	String [] sidonum,Nsidonum,sidoname;	//시도 코드,구군 table의 시도 코드,시도 이름
	String [] gugunnum,Ngugunnum,gugunname;	//구군 코드,동네 table의 구군 코드,구군 이름
	String [] dongnum,dongname;	//동 코드,동 이름
	String [] gridx,gridy,id;	//x좌표,y좌표,id값
	String dbFile="weatherdb.db3";	
	String dbFolder="/data/data/name.bagi.levente.pedometer/datebases/";
	String numDong,numSido,numGugun;	//최종적으로 가져다 붙일 동네코드,시도 코드,구군 코드가 저장되는 변수
	static SQLiteDatabase db;	//디비
	
	//파서용 변수
	int data=0;			//이건 파싱해서 array로 넣을때 번지
	public static boolean updated;	//이건 날씨정보 뿌리기위한 플래그
	String sCategory,sTm;	//동네,발표시각
	String [] sHour,sDay,sTemp,sWdKor,sReh,sWfKor;	//예보시간,날짜,온도,풍향,습도,날씨
	boolean bCategory,bTm,bHour,bDay,bTemp,bWdKor,bReh,bItem,bWfKor;	//여긴 저장을 위한 플래그들
	boolean tCategory,tTm,tItem;	//이건 text로 뿌리기위한 플래그
	boolean parserEnd;   //지오코딩용 파서 끝 flag
	static String sSido_name,sDong_name,sGugun_name;	//gps로 지오코딩후 주소를 파서해서 저장할 변수
	
	String tempDong;//="4215025000";	//기본dongcode
	static double latitude,longitutde;	//위도와 경도를 저장할 변수
	Handler handler,handler2;	//날씨저장 핸들러,지오코딩파서용 핸들러
	final int tableSido=1,tableGugun=2,tableDong=3; //이건 switch case문에서 쓸려고 만든 변수
	
	
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		checkDB();
        init();
        
        queryData(tableSido);	//시도 DB 가지고 오자
        
     
         
	}
	
	@Override
	protected void onDestroy() {
		closeDatabase();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		openDatabase(dbFolder+dbFile);	//db가 저장된 폴더에서 db를 가지고 온다
		super.onResume();
	}

	

	/**
	 * DB가 있는지 체크
	 * @author Ans
	 */
	private void checkDB() {
		 
        try {
        	boolean bResult = isCheckDB(getBaseContext());	// DB가 있는지?
        	
        	if(!bResult){	// DB가 없으면
        		copyDB(getBaseContext());	//bd복사
        		Toast.makeText(getApplicationContext(), "DB",Toast.LENGTH_SHORT).show();
        	}else{			//DB가 있으면
        		//Toast.makeText(getApplicationContext(), "이미 DB가있어요",Toast.LENGTH_SHORT).show();
        		
        	}
        	
		} catch (Exception e) {	//예외발생용

			Toast.makeText(getApplicationContext(), "예외",Toast.LENGTH_SHORT).show();
		}
		
	}


	public void init() {
		handler=new Handler();	//스레드&핸들러처리
		handler2=new Handler();	//스레드&핸들러처리
		
		
		
		sidoSpinner=(Spinner)findViewById(R.id.sidospinner);	//시도용 스피너
		gugunSpinner=(Spinner)findViewById(R.id.gugunspinner); 	//구군용 스피너
		dongSpinner=(Spinner)findViewById(R.id.dongspinner);	//동면용 스피너
		listView1=(ListView) findViewById(R.id.listView1);		//날씨정보 리스트뷰
		
		bCategory=bTm=bHour=bTemp=bWdKor=bReh=bDay=bWfKor=tCategory=tTm=tItem=false;	//부울상수는 false로 초기화해주자
		listadapter=new listWeatherView(getApplicationContext());	//리스트뷰를 만들어주자
		listView1.setAdapter(listadapter);					//어댑터와 리스트뷰를 연결
		text=(TextView) findViewById(R.id.textView1);	//텍스트 객체생성
		getBtn=(Button) findViewById(R.id.getBtn);		//버튼 객체생성
		gpsBtn=(Button) findViewById(R.id.gpsBtn);		//버튼 객체생성
		//mapBtn=(Button) findViewById(R.id.mapBtn);		
		
		sidoSpinner.setOnItemSelectedListener(this);
		gugunSpinner.setOnItemSelectedListener(this);
		dongSpinner.setOnItemSelectedListener(this);
		
		getBtn.setOnClickListener(this);
		gpsBtn.setOnClickListener(this);
		//mapBtn.setOnClickListener(this);
		
		
	
	}
	

	/**
	 * DB를 가져오는 부분
	 * 시도, 구군, 동면 모두 테이블명과 레코드가 다르기때문에 case문을 썼는데
	 * 코드가 너무 길어짐;;
	 * 
	 * @author Ans
	 * @param table
	 */
	@SuppressWarnings("deprecation")
	private void queryData(final int table) {
		
		openDatabase(dbFolder+dbFile);	//db가 저장된 폴더에서 db를 가지고 온다
		String sql=null;				//sql명령어를 저장할 변수
		Cursor cur = null;				//db가져올 커서
		int Count;						//db갯수 셀 변수
		
		switch(table){
		
		case tableSido:
			sql="select sido_num, sido_name from t_sido";	//시도 테이블에선 시도코드와 시도이름
			cur=db.rawQuery(sql, null);						//커서에 넣자
			break;
		case tableGugun:									//구군 테이블에선 시도에서 선택된 시도의 구군정보만
			sql="select sido_num, gugun_num, gugun_name from t_gugun where sido_num = "+numSido;	
			cur=db.rawQuery(sql, null);
			break;
		case tableDong:										//동면 테이블도 선택된 구군코드와 비교해서 
			sql="select gugun_num, dong_num, dong_name, gridx, gridy, _id from t_dong where gugun_num = "+numGugun;
			cur=db.rawQuery(sql, null);
			break;
		default:
			break;
		}
		
		Count=cur.getCount();	//db의 갯수를 세고
		
		switch(table){
		
		case tableSido:
			
			sidoname=new String[Count];	//갯수만큼 배열을 만든다
			sidonum=new String[Count];
			
			if(cur!=null){	//이부분이 커서로 데이터를 읽어와서 변수에 저장하는 부분
				cur.moveToFirst();
				startManagingCursor(cur);
				for(int i=0;i<Count;i++){
					sidonum[i]=cur.getString(0);
					sidoname[i]=cur.getString(1);
					cur.moveToNext();
				}
				//변수에 저장이 되었으니 스피너를 만들어 뿌려주자
				sidoAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, sidoname);	//어댑터를 통해 스피너에 donglist 넣어줌
	    		sidoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown형식
			    sidoSpinner.setAdapter(sidoAdapter);	//스피너 생성
			    
			}
			break;
		case tableGugun:	//구군도 같은작업
			
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
				gugunAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, gugunname);	//어댑터를 통해 스피너에 donglist 넣어줌
				gugunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown형식
			    gugunSpinner.setAdapter(gugunAdapter);	
			    
			}
			break;
			
		case tableDong:		//동면도 같은작업
			
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
				dongAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, dongname);	//어댑터를 통해 스피너에 donglist 넣어줌
				dongAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown형식
			    dongSpinner.setAdapter(dongAdapter);	
			}
			break;
			
		default:
			break;
		}	
		
	}
	
		/**
		 * 이부분이 db를 열어주는 부분
		 * 
		 * @author Ans
		 * @param databaseFile
		 */
		public static void openDatabase(String databaseFile) {
		    	
		    	try {
		    		db = SQLiteDatabase.openDatabase(	//선택한 폴더의 db를 가져와서 읽고,쓰기 가능하게 읽어온다
		    	    				databaseFile, null, SQLiteDatabase.OPEN_READWRITE);
			
		    	} catch (SQLiteException ex) {
		    		
		    	}
		    }
		
		/**
		 * @author Ans
		 * 이부분은 디비를 닫는다    
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
		 * 디비 파일 있는지 체크
		 */
		public boolean isCheckDB(Context mContext){
				
				
			String filePath = dbFolder+dbFile;
			File file = new File(filePath);
			  
			   if (file.exists()) {	//db폴더에 파일이 있으면 true
				   return true;
			   }
			   
			   return false;		//아님 default로 false를 반환
		}
			
			// DB를 복사하기
			// assets의 /db/xxxx.db 파일을 설치된 프로그램의 내부 DB공간으로 복사하기
		/**
		 * @author Ans
		 * 디비가 없을때 복사함
		 * 
		 */
		public void copyDB(Context mContext){	//만약 db가 없는 경우 복사를 해야된다.
				 
			  AssetManager manager = mContext.getAssets();	//asserts 폴더에서 파일을 읽기위해 쓴단다.아직 잘
			  String folderPath = dbFolder;	//db폴더			//일단 DB를 이 폴더에 저장을 하였으니 써야겠지?
			  String filePath = dbFolder+dbFile; //db폴더와 파일경로
			  File folder = new File(folderPath);	
			  File file = new File(filePath);
			  
			  FileOutputStream fos = null;	
			  BufferedOutputStream bos = null;
			  try {
			   InputStream is = manager.open("db/" + "weather.db3");	//일던 asserts폴더밑 db폴더에서 db를 가져오자
			   BufferedInputStream bis = new BufferedInputStream(is);

			   	   if (folder.exists()) {			//우리가 복사하려는 db폴더가 이미 있으면 넘어가고
			   	   }else{						
					   folder.mkdirs();				//없을경우 폴더를 만들자
				   }
			   	   
			   
				   if (file.exists()) {				//파일이 있다면 
					   file.delete();				//일단 지우고
					   file.createNewFile();		//새 파일을 만들자
				   }
				   
				   fos = new FileOutputStream(file);	
				   bos = new BufferedOutputStream(fos);
				   int read = -1;
				   byte[] buffer = new byte[1024];	//buffer는 1024byte니깐 1k로 지정해주고
				   while ((read = bis.read(buffer, 0, 1024)) != -1) {	//db파일을 읽어서 buffer에 넣고
				    bos.write(buffer, 0, read);							//buffer에서 새로 만든 파일에 쓴다
				   }													//대충이해는 되는데 어렵네;;

				   bos.flush();
				   bos.close();
				   fos.close();
				   bis.close();
				   is.close();

			   } catch (IOException e) {
			   
			  }	
		}
			

	
	/**
	 * 기상청을 연결하여 정보받고 뿌려주는 스레드
	 * 
	 * @author Ans
	 *
	 */
		
	class GetWeatherThread extends Thread{	//기상청 연결을 위한 스레드
		/**
		 * 기상청을 연결하는 스레드
		 * 이곳에서 풀파서를 이용하여 기상청에서 정보를 받아와 각각의 array변수에 넣어줌
		 * @author Ans
		 */
		public void run(){
			
			
			try{
				//timerDelayRemoveDialog(5000,progress);
				updated=false;
				sHour=new String[100];	//예보시간(사실 15개밖에 안들어오지만 넉넉하게 20개로 잡아놓음)
				sDay=new String[100];	//날짜
				sTemp=new String[100];	//현재온도
				sWdKor=new String[100];	//풍향
				sReh=new String[100];	//습도
				sWfKor=new String[100];	//날씨
				data=0;
				XmlPullParserFactory factory=XmlPullParserFactory.newInstance();	//이곳이 풀파서를 사용하게 하는곳
				factory.setNamespaceAware(true);									//이름에 공백도 인식
				XmlPullParser xpp=factory.newPullParser();							//풀파서 xpp라는 객체 생성
				
				String weatherUrl="http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="+numDong;	//이곳이 기상청URL
				URL url=new URL(weatherUrl);		//URL객체생성
				InputStream is=url.openStream();	//연결할 url을 inputstream에 넣어 연결을 하게된다.
				xpp.setInput(is,"UTF-8");			//이렇게 하면 연결이 된다. 포맷형식은 utf-8로
				
				int eventType=xpp.getEventType();	//풀파서에서 태그정보를 가져온다.
					
				while(eventType!=XmlPullParser.END_DOCUMENT){	//문서의 끝이 아닐때
					
					switch(eventType){
					case XmlPullParser.START_TAG:	//'<'시작태그를 만났을때
						
						if(xpp.getName().equals("category")){	//태그안의 이름이 카테고리일떄 (이건 동네이름이 나온다)
							bCategory=true;	
							
						} if(xpp.getName().equals("pubDate")){		//발표시각정보
							bTm=true;
						
						} if(xpp.getName().equals("hour")){		//예보시간
							bHour=true;
							
						} if(xpp.getName().equals("day")){		//예보날(오늘 내일 모레)
							bDay=true;
							
						} if(xpp.getName().equals("temp")){		//예보시간기준 현재온도
							bTemp=true;
							
						} if(xpp.getName().equals("wdKor")){	//풍향정보
							bWdKor=true;
							
						} if(xpp.getName().equals("reh")){		//습도정보
							bReh=true;
							
						} if(xpp.getName().equals("wfKor")){	//날씨정보(맑음, 구름조금, 구름많음, 흐림, 비, 눈/비, 눈)
							bWfKor=true;
							
						}
						
						break;
					
					case XmlPullParser.TEXT:	//텍스트를 만났을때
												//앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
												//변수에 정보를 넣어준 후엔 플래그를 false로~
						if(bCategory){				//동네이름
							sCategory=xpp.getText();
							bCategory=false;
						} if(bTm){					//발표시각
							sTm=xpp.getText();	
							bTm=false;	
						}  if(bHour){				//예보시각			
							sHour[data]=xpp.getText();
							bHour=false;
						}  if(bDay){				//예보날짜
							sDay[data]=xpp.getText();
							bDay=false;
						}  if(bTemp){				//현재온도
							sTemp[data]=xpp.getText();
							bTemp=false;
						}  if(bWdKor){				//풍향
							sWdKor[data]=xpp.getText();
							bWdKor=false;
						}  if(bReh){				//습도
							sReh[data]=xpp.getText();
							bReh=false;
						} if(bWfKor){				//날씨
							sWfKor[data]=xpp.getText();
							bWfKor=false;
							}
						break;
						
					case XmlPullParser.END_TAG:		//'</' 엔드태그를 만나면 (이부분이 중요)
						
						if(xpp.getName().equals("item")){	//태그가 끝나느 시점의 태그이름이 item이면(이건 거의 문서의 끝
							tItem=true;						//따라서 이때 모든 정보를 화면에 뿌려주면 된다.
							view_text();					//뿌려주는 곳~
						} if(xpp.getName().equals("pubDate")){	//이건 발표시각정보니까 1번만나오므로 바로 뿌려주자
							tTm=true;
							view_text();
						} if(xpp.getName().equals("category")){	//이것도 동네정보라 바로 뿌려주면 됨
							tCategory=true;
							view_text();
						} if(xpp.getName().equals("data")){	//data태그는 예보시각기준 예보정보가 하나씩이다.
							data++;							//즉 data태그 == 예보 개수 그러므로 이때 array를 증가해주자
						}
						break;
					}
					eventType=xpp.next();	//이건 다음 이벤트로~
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		/**
		 * 이 부분이 뿌려주는곳 
		 * 뿌리는건 핸들러가~
		 * @author Ans
		 */
		private void view_text(){
			
			handler.post(new Runnable() {	//기본 핸들러니깐 handler.post하면됨
				
				@Override
				public void run() {
					
					
					if(tCategory){	//동네이름 들어왔다
						text.setText("지역:   "+sCategory+"\n");
						tCategory=false;
					}if((tTm)&&(sTm.length()>11)){		//발표시각 들어왔다
						text.setText(text.getText()+"발표시각:   "+sTm+"\n");
						tTm=false;
					}if(tItem){		//문서를 다 읽었다
						
						for(int i=0;i<data;i++){	//array로 되어있으니 for문으로
							if(sDay[i]!=null){		//이건 null integer 에러 예방을 위해(String은 null이 가능하지만intger는 안되니깐)
								if(sDay[i].equals("0")){	//발표시각이 0이면 오늘 
									sDay[i]="   날짜: "+"오늘 ";
									
								}else if(sDay[i].equals("1")){	//1이면 내일
									sDay[i]="   날짜: "+"내일 ";
								
								}else if(sDay[i].equals("2")){	//2이면 모레
									sDay[i]="   날짜: "+"모레 ";
									
								}
							}
							
						}
						
						listadapter.setDay(sDay);	//날씨정보를 listview로 뿌려보자
						listadapter.setTime(sHour);
					    listadapter.setTemp(sTemp);
					//	listadapter.setWind(sWdKor);
					//	listadapter.setHum(sReh);
					    listadapter.setWeather(sWfKor);
					    updated=true;					//정보가 담겼으니 flag를 true로
						listadapter.notifyDataSetChanged();	//리스트뷰 업데이트
						tItem=false;
						data=0;		//다음에 날씨를 더가져오게 되면 처음부터 저장해야겠지?
						stringforshare=sCategory+"의 현재 날씨는   "+sWfKor[0]+  "   온도는"+sTemp[0]+"℃ 입니다.";
						progress.dismiss();	//프로그레스 종료
					}
					
					
				}
			});
		}
	}
	/**
	 * 이곳에서 GPS방법과 리스너등을 세팅해 준다
	 * @author Ans
	 */
	 final public void getMyLocation() {
		long minTime=60000;		//every 60sec
		float minDistance=1;	//if moved over 1miter
		
		manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);	//GPS 서비스
		listener=new MyLocationListener();	//리스너 만들자
		locationProvider = LocationManager.NETWORK_PROVIDER;
		//locationProvider = LocationManager.GPS_PROVIDER;		//에뮬에선 이걸로~
	    Handler mHandler=new Handler();
		timerDelayRemoveDialog(5000,progress,mHandler);
		manager.requestLocationUpdates(locationProvider,minTime ,minDistance, listener);//기지국
	 }
	 
	 public void timerDelayRemoveDialog(long time, final Dialog d,Handler mHandler){
		   mHandler.postDelayed(new Runnable() {
		        public void run() { 
		        	//Toast.makeText(getApplicationContext(), "위치를 찾을 수 없습니다. 네트워크를 확인하세요", Toast.LENGTH_SHORT).show();
		         	d.dismiss();         
		        }
		    }, time); 
		} 
	
	
	/**
	 * 이부분은 지오코딩해서 파서하는 곳
	 * @author Administrator
	 *
	 */
	class GetAddressThread extends Thread{	//국내라 정확한 지오코딩을 위해 daum api이용
		
		String key="8b0153f352c2bdfbb1aacfb35f83585c77ceb38f";
		String url="http://apis.daum.net/local/geo/coord2addr?apikey=";
	    
		public void run(){
			
		try{
			XmlPullParserFactory fa = XmlPullParserFactory.newInstance();
			fa.setNamespaceAware(true);		
			XmlPullParser xp=fa.newPullParser();
			
			String geocode=url+key+"&longitude="+longitutde+"&latitude="+latitude+"&format=simple&output=xml&inputCoordSystem=WGS84";
			URL url = new URL(geocode);
				//URL객체생성
			InputStream is = url.openStream();
			
			xp.setInput(is,"UTF-8");
			int eventType=xp.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT){	//문서의 끝이 아닐때
					
					switch(eventType){
					case XmlPullParser.START_TAG:	//'<'시작태그를 만났을때
					
						String tag = xp.getName(); 
		                if (tag.compareTo("rcode") == 0) { //파서가 result 태그를 만나면 x의 y의 속성 값을 각각 longitude,latitude에 넣음.
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
						//동 쿼리시작
						//Toast.makeText(getApplicationContext(),"현재 위치는 "+sSido_name+" "+sGugun_name+" "+sDong_name, Toast.LENGTH_SHORT).show();
						String sql="select gugun_num, dong_num, dong_name from t_dong where dong_name = "+"'"+sDong_name+"'";
						Cursor cur=db.rawQuery(sql, null);
						int cnt,cnt2;
						cnt=cur.getCount();
						String [] dongNumTemp=new String[cnt];
						String [] gugunNumTemp=new String[cnt];
						//동정보가 없을때->동이름을 한번 바꿔보자
						if(cnt==0){
							int cut=sDong_name.length();
							String s1=sDong_name.substring(0, cut-2);
							String s2=sDong_name.substring(cut-2);
							sDong_name=s1+"제"+s2;
							//Toast.makeText(getApplicationContext(),"현재 위치는 "+sSido_name+" "+sGugun_name+" "+sDong_name, Toast.LENGTH_SHORT).show();
							sql="select gugun_num, dong_num, dong_name from t_dong where dong_name = "+"'"+sDong_name+"'";
							cur=db.rawQuery(sql, null);
							cnt=cur.getCount();
						}
						//동정보를 한번에 찾으면
						if(cnt==1){
							cur.moveToFirst();
							numDong=cur.getString(1);
						}else if(cnt==0){//동정보 없을때
							Toast.makeText(getApplicationContext(),"동정보를 찾을수 없습니다.", Toast.LENGTH_SHORT).show();
						}else{//동정보가 여러개
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
								Toast.makeText(getApplicationContext(), "구군 정보를 찾을수 없습니다.", Toast.LENGTH_SHORT).show();
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
						GetWeatherThread getweatherthread=new GetWeatherThread();		//스레드생성(UI 스레드사용시 system 뻗는다)
						if(getweatherthread!=null&&getweatherthread.isAlive()){
							Toast.makeText(getApplicationContext(), "getweatherthread.isAlive", Toast.LENGTH_SHORT).show();
							getweatherthread.interrupt();
						}
						getweatherthread.start();	//스레드 시작
					
					
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), sSido_name+" "+sGugun_name+" "+sDong_name+"를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
					
				}
				
					
					
					
				}
			});
			
		}
		
		
	}

	/**
	 * 이곳에서 실제로 지피에스 정보를 받아온다
	 * @author Ans
	 *
	 */
	class MyLocationListener implements LocationListener{
	
		@Override
		public void onLocationChanged(Location location) {
			
			latitude=location.getLatitude();//get latitude
			longitutde=location.getLongitude();//get longitutde
			try{
				manager.removeUpdates(listener);	//지피에스 서비스 종료(이부분을 주석처리하면 설정한거대로 계속 받아옴)
			}catch(Exception e){
				Log.d("locationlistener", String.valueOf(e));
			}
			
			
			GetAddressThread getaddressthread=new GetAddressThread();		//지오코딩할 스레드
			if(getaddressthread!=null&&getaddressthread.isAlive()){
				Toast.makeText(getApplicationContext(), "getaddressthread.isAlive", Toast.LENGTH_SHORT).show();
				getaddressthread.interrupt();
			}
			getaddressthread.start();	//스레드 시작
			
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
	 * 네트워크 사용가능한지 check!!
	 * @return
	 */
	public boolean checkNetwork(){
		//네트워크용
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
	 * 버튼에 대한 처리
	 */
	@Override
	public void onClick(View v) {
		backEvt=0;
		
		if(checkNetwork()==true)
		{
			switch(v.getId()){
			
			case R.id.getBtn:
				progress = ProgressDialog.show(this, "잠시만 기다리세요", "날씨정보를 가져오고 있습니다.");
				
				numDong=tempDong;
				GetWeatherThread getweatherthread=new GetWeatherThread();
				getweatherthread.start();	//스레드 시작
				
				break;
			case R.id.gpsBtn:
				progress = ProgressDialog.show(this, "잠시만 기다리세요", "날씨정보를 가져오고 있습니다.");
				
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
			Toast.makeText(getApplicationContext(), "네트워크가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
		}
		
		
		
	}

	/**
	 * 스피너에 대한 처리
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		backEvt=0;
		
		
		switch(parent.getId()){
		
		case R.id.sidospinner:
			numSido=sidonum[position];	//시도가 선택되면 해당 코드를 변수에 넣는다
			queryData(tableGugun);		//구군 DB가지러~
			break;
		case R.id.gugunspinner:
			numGugun=gugunnum[position];	//구군이 선택되면 해당 코드를 변수에
			queryData(tableDong);			//동면 DB가지러~
			break;
		case R.id.dongspinner:
			tempDong=dongnum[position];
			numDong=tempDong;	//선택된 동면코드를 변수에 넣자
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



