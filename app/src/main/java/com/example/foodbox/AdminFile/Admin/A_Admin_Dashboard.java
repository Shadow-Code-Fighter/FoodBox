package com.example.foodbox.AdminFile.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.foodbox.AdminFile.Fragment.A_AccountFragment;
import com.example.foodbox.AdminFile.Fragment.A_HomeFragment;
import com.example.foodbox.AdminFile.Fragment.A_NotificationsFragment;
import com.example.foodbox.AdminFile.Fragment.A_OrdersFragment;
import com.example.foodbox.AdminFile.A_Gallery;
import com.example.foodbox.R;
import com.example.foodbox.A_Rating_dialog;
import com.example.foodbox.A_about;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class A_Admin_Dashboard extends AppCompatActivity {

    DrawerLayout drawerlayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    BottomNavigationView bottomNavigationView;



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_admin_dashboard);


        //side navigation
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerlayout,R.string.open,R.string.close);
        drawerlayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.gallery:
                    {
                        Intent i = new Intent(getApplicationContext(), A_Gallery.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.share:
                    {
                        ShareApp(A_Admin_Dashboard.this);
                        Toast.makeText(A_Admin_Dashboard.this, "Share Application", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case R.id.about:
                    {
                        Intent i = new Intent(getApplicationContext(), A_about.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.contact:
                    {
                        startActivity(new Intent(A_Admin_Dashboard.this, A_contact.class));
                        break;
                    }
                    case R.id.rate_us:
                    {
                        Toast.makeText(A_Admin_Dashboard.this, "Rate Selected", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), A_Rating_dialog.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.logout_ac:
                    {
                        logoutMenu(A_Admin_Dashboard.this);
                        break;
                    }
                }
                return false;
            }
        });

        //buttom Navigation
        bottomNavigationView = findViewById(R.id.buttonFragment);

        A_HomeFragment courseMeal = new A_HomeFragment();
        A_OrdersFragment ordersFragment = new A_OrdersFragment();
        A_NotificationsFragment notificationsFragment = new A_NotificationsFragment();
        A_AccountFragment accountfragment = new A_AccountFragment();

//        //Notification number set
//        BadgeDrawable badgeDrawable =bottomNavigationView.getOrCreateBadge(R.id.notifications);
//        badgeDrawable.setVisible(true);
//        badgeDrawable.setNumber(10);

        //orders number
//        BadgeDrawable badgeDrawable1 =bottomNavigationView.getOrCreateBadge(R.id.orders);
//        badgeDrawable.setVisible(true);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, courseMeal).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, courseMeal).commit();
                        return true;
                    case R.id.orders:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,ordersFragment).commit();
                        return true;
                    case R.id.notifications:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,notificationsFragment).commit();
                        return true;
                    case R.id.me:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,accountfragment).commit();
                        return true;
                }
                return false;

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerlayout.isDrawerOpen(GravityCompat.START))
        {
            drawerlayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
    //Share App code here ==
    public void ShareApp(Context c){
        final String appPakageName=c.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Download Now : https://play.google.com/store/apps/details?id="+appPakageName);
        sendIntent.setType("text/plain");
        c.startActivity(sendIntent);
    }
    //Logout pop-up "yes" or "no" code
    private void logoutMenu(A_Admin_Dashboard admin_dashboard){
        AlertDialog.Builder builder= new AlertDialog.Builder(admin_dashboard);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), A_Admin_Login.class);
                startActivity(i);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}