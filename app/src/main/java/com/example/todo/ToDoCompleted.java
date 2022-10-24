package com.example.todo;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ToDoCompleted extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference taskRef;
    private FirebaseAuth mAuth;

    private Button deleteAll;

    private String current_user_id;

    private FirebaseRecyclerAdapter<AllRequest, CompleteTaskViewHolder> firebaseRecyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_completed, container, false);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();

        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks").child(current_user_id);

        deleteAll = view.findViewById(R.id.delete_completed_task);

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String status = dataSnapshot.child("status").getValue().toString();
                            if(status.equals("Completed")) {
                                String key = dataSnapshot.getKey();
                                taskRef.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        recyclerView = view.findViewById(R.id.todo_completed_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayCompletedMeeting();
    }

    private void displayCompletedMeeting() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Query query = taskRef.orderByChild("status")
                            .startAt("Completed").endAt("Completed" + "\uf8ff");

                    FirebaseRecyclerOptions<AllRequest> options =
                            new FirebaseRecyclerOptions.Builder<AllRequest>()
                                    .setQuery(query, AllRequest.class)
                                    .build();

                    firebaseRecyclerAdapter =
                            new FirebaseRecyclerAdapter<AllRequest, CompleteTaskViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull CompleteTaskViewHolder holder, int position, @NonNull AllRequest model) {
                                    final String postKey = getRef(position).getKey();

                                    holder.task.setText(model.getTask());
                                    holder.date.setText(model.getDate());
                                    holder.time.setText(model.getTime());
                                    holder.btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteTaskDialog(postKey);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public CompleteTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_task_layout, parent, false);
                                    CompleteTaskViewHolder viewHolder = new CompleteTaskViewHolder(view);
                                    return viewHolder;
                                }
                            };

                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                    firebaseRecyclerAdapter.startListening();
                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void deleteTaskDialog(final String postKey) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder
                .setMessage("Do you really want to delete Task?")
                .setCancelable(false)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // what to do if YES is tapped
                                taskRef.child(postKey).removeValue();
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


    public static class CompleteTaskViewHolder extends RecyclerView.ViewHolder{

        private TextView task, date, time;
        private Button btn;

        public CompleteTaskViewHolder(@NonNull View itemView) {
            super(itemView);

            task = itemView.findViewById(R.id.complete_task_detail);
            date = itemView.findViewById(R.id.complete_task_date);
            time = itemView.findViewById(R.id.complete_task_time);
            btn = itemView.findViewById(R.id.complete_task_delete_button);

        }
    }
}