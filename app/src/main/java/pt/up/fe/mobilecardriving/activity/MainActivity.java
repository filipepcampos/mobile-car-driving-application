package pt.up.fe.mobilecardriving.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import pt.up.fe.mobilecardriving.R;
import pt.up.fe.mobilecardriving.view.PermissionIcon;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_CODE = 200;

    private PermissionIcon cameraIcon, gpsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initializeIcons();
        this.initializeStartButton();
    }

    private void initializeIcons() {
        this.cameraIcon = findViewById(R.id.camera_icon);
        this.gpsIcon = findViewById(R.id.gps_icon);

        this.cameraIcon.changeIconState(checkPermission(PERMISSIONS[0]));
        this.gpsIcon.changeIconState(checkPermission(PERMISSIONS[1]));
    }

    private void initializeStartButton() {
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasAllPermissions()) {
                    LinkedList<String> permissions = new LinkedList<>();
                    for (String permission : PERMISSIONS) {
                        if (!checkPermission(permission)) {
                            permissions.add(permission);
                        }
                    }
                    String[] permissionsArray = new String[permissions.size()];
                    permissions.toArray(permissionsArray);
                    requestAppPermissions(permissionsArray, REQUEST_CODE);
                } else if (!new MotionTracker((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled()) {
                    Toast.makeText(MainActivity.this, "Enable GPS to start detecting...", Toast.LENGTH_LONG).show();
                } else {
                    changeToObjectDetection();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            PermissionIcon icon;
            for (int i = 0; i < permissions.length; ++i) {
                if (permissions[i].equals(PERMISSIONS[0]))
                    icon = this.cameraIcon;
                else if (permissions[i].equals(PERMISSIONS[1]))
                    icon = this.gpsIcon;
                else
                    continue;
                icon.changeIconState(grantResults[i] == PackageManager.PERMISSION_GRANTED);

            }
            if (this.hasAllPermissions() && new MotionTracker((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled())
                changeToObjectDetection();
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : PERMISSIONS)
            if (!this.checkPermission(permission))
                return false;
        return true;
    }

    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAppPermissions(String[] permissions, int code) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    private void changeToObjectDetection() {
        startActivity(new Intent(MainActivity.this, ObjectDetectionActivity.class));
    }
}