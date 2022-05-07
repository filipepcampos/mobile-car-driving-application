package pt.up.fe.mobilecardriving.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: IN THE FUTURE, THIS ACTIVITY COULD BE A MENU
        startActivity(new Intent(this, ObjectDetectionActivity.class));
    }
}