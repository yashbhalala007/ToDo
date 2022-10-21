package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ViewPager viewPager;
    private ImageButton logoutButton, profileButton;

    private DatabaseReference userRef;

    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        profileButton = findViewById(R.id.todo_profile);
        logoutButton = findViewById(R.id.todo_logout);

        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Fragment todopendingRequest = new ToDoPending();
                    Fragment todocompletedRequest = new ToDoCompleted();
                    Fragment todoallRequest = new ToDoAll();

                    viewPager = (ViewPager) findViewById(R.id.view_pager);
                    setupViewPager(viewPager, todocompletedRequest, todopendingRequest, todoallRequest);
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setToDoIntent = new Intent(DashboardActivity.this, SetToDo.class);
                startActivity(setToDoIntent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(DashboardActivity.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            }
        });

    }

    private void setupViewPager(ViewPager viewPager, Fragment todoConfirmRequest, Fragment todoPendingRequest, Fragment todoAllRequest) {

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(todoAllRequest, "All");
        adapter.addFragment(todoConfirmRequest, "Done");
        adapter.addFragment(todoPendingRequest, "Pending");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("WARNING");
        alertDialogBuilder
                .setMessage("Do you really want to exit?")
                .setCancelable(false)
                .setPositiveButton("EXIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // what to do if YES is tapped
                                finishAffinity();
                                System.exit(0);
                            }
                        })
                .setNegativeButton("CANCLE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // code to do on NO tapped
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}