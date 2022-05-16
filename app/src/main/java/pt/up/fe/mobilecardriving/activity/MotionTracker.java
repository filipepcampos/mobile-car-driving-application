package pt.up.fe.mobilecardriving.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

public class MotionTracker implements LocationListener {
    private final LocationManager locationManager;
    private Location previousLocation;
    private float speed;

    public MotionTracker(LocationManager locationManager) {
        this.locationManager = locationManager;
        this.speed = 0;
    }

    public float getSpeed() {
        return this.speed;
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
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @SuppressLint("MissingPermission")
    public void resume(Activity activity) {
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void pause() {
        this.locationManager.removeUpdates(this);
    }
}
