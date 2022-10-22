package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SetToDo extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton ib1, ib2;
    private EditText ed2;
    private Button btn;
    private TextView txt, txt2;
    private String description;
    private ProgressDialog loadingBar;

    private StorageReference taskReference;
    private DatabaseReference usersRef, taskRef;
    private FirebaseAuth mAuth;

    private String saveDate, saveTime, postName, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_to_do);

        ib1 = findViewById(R.id.todo_date_button);
        txt = findViewById(R.id.todo_display_date);
        ib2 = findViewById(R.id.todo_time_button);
        txt2 = findViewById(R.id.todo_display_time);
        ed2 = findViewById(R.id.todo_des);
        btn = findViewById(R.id.todo_send);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();
        taskReference = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks").child(current_user_id);

        loadingBar = new ProgressDialog(this);

        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showTimePicker(v); }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });

    }

    public static class MyDatePickerFragment extends DialogFragment {

        public MyDatePickerFragment(TextView txt2) {
            this.txt2 = txt2;
        }

        TextView txt2;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return datePickerDialog;
        }

        private void updateDisplay(String date){
            txt2.setText(date);
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        Calendar cal=Calendar.getInstance();
                        android.icu.text.SimpleDateFormat month_date = new android.icu.text.SimpleDateFormat("MMMM");
                        cal.set(Calendar.MONTH,month);

                        String selectedDate = day + "-" + month_date.format(cal.getTime()) + "-" + year;
                        updateDisplay(selectedDate);
                    }
                };
    }


    private void showTimePicker(View v) {
        DialogFragment newFragment1 = new TimePicker(txt2);
        newFragment1.show(getSupportFragmentManager(), "time picker");
    }


    public void showDatePicker(View v) {
        DialogFragment newFragment = new MyDatePickerFragment(txt);
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    private void ValidatePostInfo() {
        description = ed2.getText().toString();
        if (TextUtils.isEmpty(description)){
            Toast.makeText(SetToDo.this,"Please Write Description First!!", Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Task");
            loadingBar.setMessage("Task is adding");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            savingTask();
        }
    }

    private void savingTask() {

        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveDate = currentDate.format(calDate.getTime());

        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveTime = currentTime.format(calTime.getTime());

        postName = saveDate + saveTime;

        HashMap postMap = new HashMap();
        postMap.put("date", txt.getText().toString());
        postMap.put("time", txt2.getText().toString());
        postMap.put("task", ed2.getText().toString());
        postMap.put("status", "Pending");

        taskRef.child(postName).updateChildren(postMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(SetToDo.this, DashboardActivity.class);
                            startActivity(intent);
                            Toast.makeText(SetToDo.this,"Task added Successfully!!!", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                        else {
                            String msg = task.getException().getMessage();
                            Toast.makeText(SetToDo.this,"Error Occured: " + msg, Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SetToDo.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}