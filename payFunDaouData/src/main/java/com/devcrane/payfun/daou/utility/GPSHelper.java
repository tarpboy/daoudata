package com.devcrane.payfun.daou.utility;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;

public class GPSHelper {

//	private static GeoPoint gpShop;
//	private static GeoPoint gpPhone;
	private static Location locationShop, locationPhone;
	private static LocationManager locationManager;
	private static Location currentLocation;
	private static boolean inHome = false;
	private static boolean isFollowMode = true; // default value is auto follow
	private static boolean isMapInCenter = false;
	private static String locationType = "";

	private static LocationListener gpsLocationListener;
	private static LocationListener networkLocationListener;
	private static final long minTime = 100; // ms
	private static final float minDistance = 5; // m

	public void startListening(Context ctx) {
		startGPSLocationListener(ctx);
	}

	public void stopListening() {
		finishGPSLocationListener(true);
	}

//	public GeoPoint get_GeoPoint_Shop(String lat, String lng) {
//		Double latPoint = Double.parseDouble(lat);
//		Double lngPoint = Double.parseDouble(lng);
//
//		GeoPoint gp = new GeoPoint((int) (latPoint * 1E6),
//				(int) (lngPoint * 1E6));
//		return gp;
//	}
//
//	public GeoPoint getGeoPoint_from_Address(String add, Context ctx) {
//		double lat = 0;// =Double.parseDouble("16.573022719182777");
//		double lng = 0;// = Double.parseDouble("107.20458984375");
//		GeoPoint gp = null;
//
//		try {
//			Geocoder gc = new Geocoder(ctx);
//			List<android.location.Address> foundAdresses = gc
//					.getFromLocationName(add, 5); // Search
//			for (int i = 0; i < foundAdresses.size(); ++i) {
//				// Save results as Longitude and Latitude
//				// @todo: if more than one result, then show a select-list
//				Address x = foundAdresses.get(i);
//				lat = x.getLatitude();
//				lng = x.getLongitude();
//			}
//			if (lat == 0 && lng == 0) {
//				return null;
//			}
//			gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
//		} catch (Exception ex) {
//			Log.i("lam", "getGeoPoint_from_Address(): " + ex.toString());
//			return null;
//
//		}
//		return gp;
//	}

	public Location get_Location_Shop(String lat, String lng) {
		Location location = new Location("LocationShop");
		try {
			if (lat == "" || lng == "")
				return null;

			Double latPoint = Double.parseDouble(lat);
			Double lngPoint = Double.parseDouble(lng);

			location.setLatitude(latPoint);
			location.setLongitude(lngPoint);
		} catch (Exception ex) {
			return null;
			
		}
		return location;
	}

	public Location get_Location_Phone() {

		return currentLocation;
	}

	public static void setCurrentLocation(Location currentLocation) {
		GPSHelper.currentLocation = currentLocation;
	}

//	public GeoPoint get_GeoPoint_Phone() {
//
//		return getGeoPointfromLocation(currentLocation);
//	}

	public float get_Distance_Shop_Phone(String lat_shop, String lng_shop) {

		locationShop = get_Location_Shop(lat_shop, lng_shop);
		locationPhone = get_Location_Phone();
		if (locationShop == null || locationPhone == null) {
			return 0;
		}
		float dis = locationShop.distanceTo(locationPhone) / 1000;

		return dis;
	}

	public String get_Distance_Shop_Phone_String(String lat_shop,
			String lng_shop) {
		String diss = "Distance :";
		locationShop = get_Location_Shop(lat_shop, lng_shop);
		locationPhone = get_Location_Phone();
		if (locationShop == null || locationPhone == null) {
			return diss += "Unknown";
		}
		float dis = locationShop.distanceTo(locationPhone) / 1000;
		return diss += dis + " km";
	}

//	private GeoPoint getGeoPointfromLocation(Location lct) {
//		if (lct == null)
//			return null;
//		GeoPoint gp = new GeoPoint((int) (lct.getLatitude() * 1E6), (int) (lct
//				.getLongitude() * 1E6));
//		return gp;
//	}

	public boolean check_GPS_Status(Context ctx) {
		boolean chk = false;
		locationManager = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true); // gps
		if(provider==null)
		{
			//Helper.showAlert(ctx, "GPS is invalid, please enable [GPS satellites] and [wireless networks] in Setting>Location & security");
			chk=false;
		}
		else
		{
			chk=true;
		}
		return chk;
	}

	private void startGPSLocationListener(Context ctx) {
		isFollowMode = true;
		inHome = false;
		try {
			locationManager = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setSpeedRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = locationManager.getBestProvider(criteria, true); // gps
			if (provider != null) {
				locationType = provider;
				if (!inHome) {
					Location location = locationManager
							.getLastKnownLocation(provider);
					if (location != null) {
						currentLocation = location;// vi tri cache;
					}
					inHome = true;
				}
			} else { // gps and network are both disabled
				// Toast.makeText(this, "gps and network are both disabled",
				// 3000).show();
			}

			/* GPS_PROVIDER */
			if (gpsLocationListener == null) {
				gpsLocationListener = new MyLocationListener("gps");
			}
			String gpsProvider = LocationManager.GPS_PROVIDER; // gps
			locationManager.requestLocationUpdates(gpsProvider, minTime,
					minDistance, gpsLocationListener);

			/* NETWORK_PROVIDER */
			if (networkLocationListener == null) {
				networkLocationListener = new GPSHelper.MyLocationListener("network");
			}
			String networkProvider = LocationManager.NETWORK_PROVIDER; // network
			locationManager.requestLocationUpdates(networkProvider, minTime,
					minDistance, networkLocationListener);
		} catch (Exception ex) {
			BHelper.db("startGPSLocationListener():" + ex.toString());
		}
	}

	private void finishGPSLocationListener(boolean isSetNull) {
		isFollowMode = false;
		// locationType = "";
		if (locationManager != null) {
			if (networkLocationListener != null)
				locationManager.removeUpdates(networkLocationListener);
			if (gpsLocationListener != null)
				locationManager.removeUpdates(gpsLocationListener);

			if (isSetNull) {
				if (networkLocationListener != null)
					networkLocationListener = null;
				if (gpsLocationListener != null)
					gpsLocationListener = null;
			}
		}

	}

	class MyLocationListener implements LocationListener {
		String locationType;

		MyLocationListener(String locationType) {
			this.locationType = locationType;
		}

		public void onLocationChanged(Location location) {
			if (location == null)
				return;
			currentLocation = location;
			inHome = true;
			// gpsLocationListener has higher priority than
			// networkLocationListener
			if (locationType.equals("gps")) {
				locationManager.removeUpdates(networkLocationListener);
			}
		}

		public void onProviderDisabled(String provider) {
			if (locationType.equals("gps")) {
				locationManager.requestLocationUpdates(provider, minTime,
						minDistance, networkLocationListener);
			}
		}

		public void onProviderEnabled(String provider) {
			if (locationType.equals("gps")) {
				locationManager.requestLocationUpdates(provider, minTime,
						minDistance, gpsLocationListener);
			} else {
				locationManager.requestLocationUpdates(provider, minTime,
						minDistance, networkLocationListener);
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (status == LocationProvider.OUT_OF_SERVICE) {
				// Helper.showToast(ShopOnMap3.this,provider +
				// " is OUT OF SERVICE");
			} else if (status == 1) {
				// Helper.showToast(ShopOnMap3.this,provider +
				// " is TEMPORARILY UNAVAILABLE");
			} else {
				// Helper.showToast(ShopOnMap3.this,provider + " is AVAILABLE");
			}
		}
	}
	
	
	
	
	public static void enableGPS(Context ctx) {
		// yeu cau permission android.permission.WRITE_SECURE_SETTINGS
		// Khong lam viec tren emulator
		String allowedProviders = LocationManager.GPS_PROVIDER + ","
				+ LocationManager.NETWORK_PROVIDER;
		Settings.Secure.putString(ctx.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED, allowedProviders);

	}
}
