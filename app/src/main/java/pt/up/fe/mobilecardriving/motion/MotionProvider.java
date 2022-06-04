package pt.up.fe.mobilecardriving.motion;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

public class MotionProvider implements LocationListener {
    private MotionProviderListener motionProviderListener;
    private final LocationManager locationManager;
    private Location previousLocation;
    private float speed;

    public MotionProvider(LocationManager locationManager) {
        this.motionProviderListener = null;
        this.locationManager = locationManager;
        this.speed = 0;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean isProviderEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void setProviderListener(MotionProviderListener listener) {
        this.motionProviderListener = listener;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (this.previousLocation != null) {
                float elapsedSeconds = (float) (location.getTime() - this.previousLocation.getTime()) / 1000f;
                float elapsedMeters = this.previousLocation.distanceTo(location);
                this.speed = elapsedMeters / elapsedSeconds * 3.6f;  // Convert to km/h
            }
            this.previousLocation = location;
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if (MotionProvider.this.motionProviderListener != null)
            MotionProvider.this.motionProviderListener.onProviderEnabled();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if (MotionProvider.this.motionProviderListener != null)
            MotionProvider.this.motionProviderListener.onProviderDisabled();
    }

    @SuppressLint("MissingPermission")
    public void resume() {
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void pause() {
        this.locationManager.removeUpdates(this);
    }
}
