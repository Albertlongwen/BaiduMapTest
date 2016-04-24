package com.example.baidumaptest;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private MapView mapView;
	private BaiduMap baiduMap;//地图管理器，对地图的各种操作都是调用该类的方法
	private LocationManager locationManager;//位置管理器，获取当前位置
	private String provider;//位置提供器的名字
	private boolean isFirstLocate=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());//获取主程序的context,而不是单个activity的context,
		setContentView(R.layout.activity_main);
		mapView=(MapView)findViewById(R.id.map_view);
		baiduMap=mapView.getMap();//获取Baidumap的实例
		
		baiduMap.setMyLocationEnabled(true);//设置当前位置可显示
		locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);//获取系统的位置管理器，定位服务
		List<String> providerList=locationManager.getProviders(true);//获取可用的位置提供器
		if(providerList.contains(LocationManager.GPS_PROVIDER))
		{
			provider=LocationManager.GPS_PROVIDER;
		}
		else if(providerList.contains(LocationManager.NETWORK_PROVIDER))
		{
			provider=LocationManager.NETWORK_PROVIDER;
		}
		else
		{
			Toast.makeText(this, "没有位置服务在用", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Location location=locationManager.getLastKnownLocation(provider);//通过可用的提供器获取当前位置信息
		if(location!=null)
		{
			navigateTo(location);//把位置信息传入baiduMap对象里面去，并在地图上显示当前位置
		}
		locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
	}
	private void navigateTo(Location location)//获取当前的位置对象，随时更新
	{
		if(isFirstLocate)//第一次定位
		{
			LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
			MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);//传入当前位置的对象
			baiduMap.animateMapStatus(update);
			update=MapStatusUpdateFactory.zoomTo(12.5f);//定义缩放级别
			baiduMap.animateMapStatus(update);
			isFirstLocate=false;
		}
		MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());//封装当前的位置信息
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData=locationBuilder.build();//生成一个光标实例
		baiduMap.setMyLocationData(locationData);//让当前位置显示在地图上
	}
	LocationListener locationListener=new LocationListener()//位置监听器，监听状态的改变
	{
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if(location!=null)
				navigateTo(location);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		baiduMap.setMyLocationEnabled(false);//消除位置标注信息
		mapView.onDestroy();
		if(locationManager!=null)
			locationManager.removeUpdates(locationListener);//移除监听器
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}

}
