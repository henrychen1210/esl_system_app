package no.nordicsemi.android.nrfmesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class SelectmodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectmode);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("FJUEE");

        Button building = (Button) findViewById(R.id.setting);
        building.setOnClickListener(buildingListener);

        Button manage = (Button) findViewById(R.id.manage);
        manage.setOnClickListener(manageListener);

    }

    private Button.OnClickListener buildingListener =
            new Button.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences.Editor edit = getSharedPreferences("build", MODE_PRIVATE).edit();
                    Intent i = new Intent();
                    i.setClass(SelectmodeActivity.this, MainActivity.class);
                    edit.clear();
                    edit.putBoolean("building", true);
                    edit.commit();
                    startActivity(i);
                }
            };


    private Button.OnClickListener manageListener =
            new Button.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences.Editor edit = getSharedPreferences("build", MODE_PRIVATE).edit();
                    Intent i = new Intent();
                    i.setClass(SelectmodeActivity.this, DashboardActivity.class);
                    edit.clear();
                    edit.putBoolean("building", false);
                    edit.commit();
                    startActivity(i);
                }
            };
}