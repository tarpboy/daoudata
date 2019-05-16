package com.devcrane.payfun.daou;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.Item;
import com.devcrane.payfun.daou.entity.OnFinishSearchListener;
import com.devcrane.payfun.daou.entity.Searcher;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.GPSHelper;
import com.devcrane.payfun.daou.utility.LocationAddress;

public class MapActivity extends Activity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
	private MapView mMapView;
	private static MapPoint MAP_POINT_POI1;
	GPSHelper gps = new GPSHelper();
	private volatile Thread th_refresh_gps;
	protected String lat;
	protected String lng;
	private EditText mEditTextQuery;
	private Button mButtonSearch;
	private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);

		MapLayout mapLayout = new MapLayout(this);
		mMapView = mapLayout.getMapView();
		mMapView.setDaumMapApiKey(StaticData.DAUM_MAPS_ANDROID_APP_API_KEY);
		mMapView.setMapViewEventListener(this);
		mMapView.setPOIItemEventListener(this);
		mMapView.setMapType(MapView.MapType.Standard);
		mMapView.zoomIn(true);
		mMapView.zoomOut(true);
		mMapView.setClickable(true);
		ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
		mapViewContainer.addView(mapLayout);
		mEditTextQuery = (EditText) findViewById(R.id.editTextQuery); // 검색창
	    mButtonSearch = (Button) findViewById(R.id.buttonSearch); // 검색버튼

	}

	@Override
	protected void onStart() {
		super.onStart();
		gps.startListening(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		gps.stopListening();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh_gps();
		
	        mButtonSearch.setOnClickListener(new OnClickListener() { // 검색버튼 클릭 이벤트 리스너
				@Override
				public void onClick(View v) {
			        String query = mEditTextQuery.getText().toString();
			        if (query == null || query.length() == 0) {
			        	showToast("검색어를 입력하세요.");
			        	return;
			        }
			        hideSoftKeyboard(); // 키보드 숨김
			        GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
			        double latitude = geoCoordinate.latitude; // 위도
			        double longitude = geoCoordinate.longitude; // 경도
			        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
			        int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
			        String apikey = StaticData.DAUM_MAPS_ANDROID_APP_API_KEY;
			        
			        Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
			        searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
						@Override
						public void onSuccess(List<Item> itemList) {
							mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
							showResult(itemList); // 검색 결과 보여줌 
						}
						
						@Override
						public void onFail() {
							showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
						}
					});
				}
			});
	}

	
	
	
	
	private void showResult(List<Item> itemList) {
		MapPointBounds mapPointBounds = new MapPointBounds();
		
		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);

			MapPOIItem poiItem = new MapPOIItem();
			poiItem.setItemName(item.title);
			poiItem.setTag(i);
			MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
			poiItem.setMapPoint(mapPoint);
			mapPointBounds.add(mapPoint);
			poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
			poiItem.setCustomImageAutoscale(false);
			poiItem.setCustomImageAnchor(0.5f, 1.0f);
			
			mMapView.addPOIItem(poiItem);
			mTagItemMap.put(poiItem.getTag(), item);
		}
		
		mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
		
		MapPOIItem[] poiItems = mMapView.getPOIItems();
		if (poiItems.length > 0) {
			mMapView.selectPOIItem(poiItems[0], false);
		}
	}
	
	
	
	
	private class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String locationAddress;
			switch (message.what) {
			case 1:
				Bundle bundle = message.getData();
				locationAddress = bundle.getString("address");
				break;
			default:
				locationAddress = null;
			}
			showAddress(locationAddress);

		}
	}

	private void showAddress(final String locationAddress) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Address:");
		alertDialog.setMessage(locationAddress);
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra(StaticData.ADDRESS_RESULT, locationAddress);
				setResult(RESULT_OK, returnIntent);
				finish();

			}
		});
		alertDialog.setNegativeButton("Cancel", null);
		alertDialog.show();
	}

	@Override
	public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewDragEnded(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewDragStarted(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewInitialized(MapView arg0) {
		// TODO Auto-generated method stub
		BHelper.db("onMapViewInitialized");
		

	}

	@Override
	public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
		MapPoint.GeoCoordinate mapPointGeo = arg1.getMapPointGeoCoord();
		getLocation(mapPointGeo);

	}

	@Override
	public void onMapViewMoveFinished(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private void refresh_gps() {

		gps.startListening(this);

		th_refresh_gps = new Thread() {

			@Override
			public void run() {
				BHelper.db("gps refresh is running!");
				if (th_refresh_gps == null) {
					return;
				} // stopped before started.
				int counter = 0;
				Looper.prepare();
				while (counter < 30) {
					try {
						counter += 1;
						Thread.yield();
						Location loc = gps.get_Location_Phone();
						Thread.sleep(2000);
						if (loc != null) {
							lat = String.valueOf(loc.getLatitude());
							lng = String.valueOf(loc.getLongitude());
							BHelper.db("gps value:" + lat + "," + lng);
							if (lat != null && lng != null) {
								MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(loc.getLatitude(), loc.getLongitude());
//								MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(37.551094, 127.019470);
								
								MapPOIItem poiItem1 = new MapPOIItem();
								poiItem1.setItemName("위치");
								poiItem1.setMapPoint(MAP_POINT_POI1);
								poiItem1.setMarkerType(MapPOIItem.MarkerType.BluePin);
								mMapView.addPOIItem(poiItem1);
								
								mMapView.setMapCenterPointAndZoomLevel(MAP_POINT_POI1, 2, true);
								
								gps.stopListening();
								Looper.loop();
								this.interrupt();
							} else {
								BHelper.db("can not get gps 1");
							}
						}else{
							BHelper.db("can not get gps 0");
						}
						
					} catch (Exception ex) {
						BHelper.db("get location error: " + ex.getMessage());
						// Looper.loop();
						this.interrupt();
					}
				}
				Looper.loop();
				gps.stopListening();
			}

		};
		th_refresh_gps.start();
	}
	private void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MapActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		});
    }

	@Override
	@Deprecated
	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
		// TODO Auto-generated method stub
		BHelper.db("onCalloutBalloonOfPOIItemTouched2");

	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1, MapPOIItem.CalloutBalloonButtonType arg2) {
		// TODO Auto-generated method stub
		try {
			Item item = mTagItemMap.get(arg1.getTag());
			String address = item.address;
			if(address!=null)
			showAddress(address);
		} catch (Exception e) {
			getLocation(MAP_POINT_POI1.getMapPointGeoCoord());
			e.printStackTrace();
		}
		

	}

	@Override
	public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1, MapPoint arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
		// TODO Auto-generated method stub

	}

	class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

		private final View mCalloutBalloon;

		public CustomCalloutBalloonAdapter() {
			mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
		}

		@Override
		public View getCalloutBalloon(MapPOIItem poiItem) {
			if (poiItem == null)
				return null;
			Item item = mTagItemMap.get(poiItem.getTag());
			if (item == null)
				return null;
			ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
			TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
			textViewTitle.setText(item.title);
			TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
			textViewDesc.setText(item.address);
			imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
			return mCalloutBalloon;
		}

		@Override
		public View getPressedCalloutBalloon(MapPOIItem poiItem) {
			return null;
		}

	}
	private Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
	}
	private Drawable createDrawableFromUrl(String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	private void getLocation(MapPoint.GeoCoordinate mapPointGeo){
		LocationAddress locationAddress = new LocationAddress();
		locationAddress.getAddressFromLocation(mapPointGeo.latitude, mapPointGeo.longitude, this, new GeocoderHandler());
	}

}
