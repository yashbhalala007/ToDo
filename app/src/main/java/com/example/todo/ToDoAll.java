package com.example.todo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ToDoAll extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference usersRef, taskRef;
    private FirebaseAuth mAuth;
    private Button deleteAll;
    private EditText search;

    private String current_user_id;

    private FirebaseRecyclerAdapter<AllRequest, AllRequestsViewHolder> firebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_all, container, false);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        deleteAll = view.findViewById(R.id.delete_all_task);
        search = view.findViewById(R.id.all_task_search);

        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks").child(current_user_id);
        recyclerView = view.findViewById(R.id.todo_all_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String key = dataSnapshot.getKey();
                            taskRef.child(key).removeValue();
                        }
                        displayAllRequest(taskRef);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String st = search.getText().toString().trim();
                if (TextUtils.isEmpty(st)){
                    displayAllRequest(taskRef);
                }else {
                    Query query = taskRef.orderByChild("task")
                            .startAt(st).endAt(st + "\uf8ff");
                    displayAllRequest(query);
                }
            }
        });

        return view;


    }

    @Override
    public void onStart() {
        super.onStart();
        displayAllRequest(taskRef);
    }

    private void displayAllRequest( Query query) {

                    FirebaseRecyclerOptions<AllRequest> options =
                            new FirebaseRecyclerOptions.Builder<AllRequest>()
                                    .setQuery(query, AllRequest.class)
                                    .build();

                    firebaseRecyclerAdapter =
                            new FirebaseRecyclerAdapter<AllRequest, AllRequestsViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull AllRequestsViewHolder holder, int position, @NonNull AllRequest model) {
                                    holder.task.setText(model.getTask());
                                    holder.date.setText(model.getDate());
                                    holder.time.setText(model.getTime());
                                }

                                @NonNull
                                @Override
                                public AllRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_task_layout, parent, false);
                                    AllRequestsViewHolder viewHolder = new AllRequestsViewHolder(view);
                                    return viewHolder;
                                }
                            };

                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                    firebaseRecyclerAdapter.startListening();

                }
    }

    class AllRequestsViewHolder extends RecyclerView.ViewHolder {

        TextView task, date, time;

        public AllRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            task = itemView.findViewById(R.id.status_all_task);
            date = itemView.findViewById(R.id.status_all_task_date);
            time = itemView.findViewById(R.id.status_all_task_time);
        }
    }
