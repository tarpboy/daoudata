package com.devcrane.payfun.daou.utility;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LocationAddress {
    private static final String TAG = "LocationAddress";
    public static void getAddressFromLocation2(final double latitude, final double longitude,
            final Context context, final Handler handler) {
    	Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.KOREAN);
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        result = address.getAddressLine(0);
                    }
                } catch (IOException e) {
                	BHelper.db("Unable connect to Geocoder: "+ e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    	
    }
    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.KOREAN);
                String result = null;
                try {
                    //MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(loc.getLatitude(), loc.getLongitude());
//                	MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(37.551094, 127.019470);
                	MapPoint MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                	MapPOIItem poiItem1 = new MapPOIItem();
                	poiItem1.setItemName("my point");
                	poiItem1.setMapPoint(MAP_POINT_POI1);
                	poiItem1.setMarkerType(MapPOIItem.MarkerType.BluePin);
//                	mMapView.addPOIItem(poiItem1);

                	
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        result = address.getAddressLine(0);
                    }
                } catch (IOException e) {
                	BHelper.db( "Unable connect to Geocoder:"+ e.getMessage());
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
