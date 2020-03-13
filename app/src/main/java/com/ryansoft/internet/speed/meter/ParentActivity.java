package com.ryansoft.internet.speed.meter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

public class ParentActivity extends AppCompatActivity {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment fragment = new HomeFragment();

                    ft.replace(R.id.container, fragment);
                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new OptionFragment())
                            .commit();
                    return true;
                case R.id.navigation_notifications:
                    AboutFragment aboutFragment = new AboutFragment();
                    ft.replace(R.id.container, aboutFragment);
                    ft.commit();
                    return true;
//                case R.id.navigation_vpn:
//                    VpnFragment vpnFragment = new VpnFragment();
//                    ft.replace(R.id.container, vpnFragment);
//                    ft.commit();
//                    return true;
            }
            return false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        if (!Data.sflag) {
            Intent intent1 = new Intent(this, SpeedMeter.class);
            try {
                startService(intent1);
            }catch ( Exception e1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(intent1);
                }else {
                    this.startService(intent1);
                }
            }
        }


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeFragment fragment = new HomeFragment();

        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        Data.flag = false;
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        Data.flag = true;
        super.onResume();
    }
    @Override
    protected void onStop() {
        Data.flag = false;
        super.onStop();
    }

}
