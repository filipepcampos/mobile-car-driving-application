package pt.up.fe.mobilecardriving.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MotionTracker implements LocationListener {
    private ProviderListener providerListener;
    private final LocationManager locationManager;
    private Location previousLocation;
    private float speed;

    public MotionTracker(LocationManager locationManager) {
        this.providerListener = null;
        this.locationManager = locationManager;
        this.speed = 0;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean isProviderEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void setProviderListener(ProviderListener listener) {
        this.providerListener = listener;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (this.previousLocation != null) {
                float elapsedSeconds = (float) (location.getTime() - this.previousLocation.getTime()) / 1000f;
                float elapsedMeters = this.previousLocation.distanceTo(location);
                this.speed = elapsedMeters / elapsedSeconds * 3.6f;
            }
            this.previousLocation = location;
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if (MotionTracker.this.providerListener != null)
            MotionTracker.this.providerListener.onProviderEnabled();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if (MotionTracker.this.providerListener != null)
            MotionTracker.this.providerListener.onProviderDisabled();
    }

    @SuppressLint("MissingPermission")
    public void resume() {
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void pause() {
        this.locationManager.removeUpdates(this);
    }
}
