package com.reobotetechnology.reobotegame.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.reobotetechnology.reobotegame.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class HomeActivity extends AppCompatActivity {


    private BottomNavigationViewEx navView;
    //private AnimatedBottomBar navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navView = findViewById(R.id.nav_view);
        //navView.setVisibility(View.GONE);
        navView.enableAnimation(false);
        navView.enableItemShiftingMode(false);
        navView.enableShiftingMode(false);


        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_feed, R.id.navigation_graphics, R.id.navigation_ranking, R.id.navigation_profile)
                .build();*/

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //NavigationUI.setupActionBarWithNavController(this, navController);
        NavigationUI.setupWithNavController(navView, navController);

    }

}
