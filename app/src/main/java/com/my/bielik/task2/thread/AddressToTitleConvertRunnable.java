package com.my.bielik.task2.thread;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.my.bielik.task2.main.MainActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import static com.my.bielik.task2.user.LoginActivity.TAG;

public class AddressToTitleConvertRunnable implements Runnable {

    private double latitude;
    private double longitude;
    private WeakReference<MainActivity> mainActivityWeakReference;

    private OnConvertingFinishedCallback convertingFinishedCallback;

    public AddressToTitleConvertRunnable(MainActivity activity, double latitude, double longitude, OnConvertingFinishedCallback callback) {
        this.mainActivityWeakReference = new WeakReference<>(activity);
        this.latitude = latitude;
        this.longitude = longitude;
        this.convertingFinishedCallback = callback;
    }

    @Override
    public void run() {
        String text;
        MainActivity activity = mainActivityWeakReference.get();
        if (activity != null && !activity.isFinishing()) {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ex) {
                Log.e(TAG, "Service not available", ex);
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "invalid latitude or longitude", ex);
            }

            if (addresses != null && addresses.size() != 0) {
                Address address = addresses.get(0);
                StringBuilder result = new StringBuilder();
                if (address.getLocality() != null) {
                    result.append(address.getLocality());
                }
                if (address.getCountryName() != null) {
                    if (result.length() != 0) {
                        result.append(", ");
                    }
                    result.append(address.getCountryName());
                }
                text = result.toString();
                convertingFinishedCallback.onConvertingFinished(text);
            }
        }
    }

    public interface OnConvertingFinishedCallback {
        void onConvertingFinished(String text);
    }
}
