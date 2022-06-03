package com.example.thriftawayui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainMenu extends AppCompatActivity {

    public ImageView btn_girl, btn_boy, btn_home, btn_kids, btn_others;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_girl = findViewById(R.id.btngirl);
        btn_boy = findViewById(R.id.btnboy);
        btn_home = findViewById(R.id.btnhome);
        btn_kids = findViewById(R.id.btnkids);
        btn_others = findViewById(R.id.btnothers);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navLister);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navLister =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.navhome:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navrequest:
                            selectedFragment = new RequestFragment();
                            break;
                        case R.id.navmystuff:
                            selectedFragment = new MyStuffFragment();
                            break;
                        case R.id.navmsg:
                            selectedFragment = new MessageFragment();
                            break;
                        case R.id.navaccount:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                return true;
                }

            };
}
