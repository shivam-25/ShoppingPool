package com.example.android.shoppingpool.activities;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.fragments.SocialFragment;
import com.example.android.shoppingpool.models.Comments;
import com.example.android.shoppingpool.models.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private String Post_Key, current_user_id;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("Post Key").toString();
        CommentsList = (RecyclerView) findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        mAuth=FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_btn);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String username = dataSnapshot.child("username").getValue().toString();
                            ValidateComment(username);
                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts")
                .child(Post_Key)
                .child("Comments");

        System.out.println(query);

        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(query, new SnapshotParser<Comments>() {
                            @NonNull
                            @Override
                            public Comments parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Comments(
                                        snapshot.child("comment").getValue().toString(),
                                        snapshot.child("date").getValue().toString(),
                                        snapshot.child("time").getValue().toString(),
                                        snapshot.child("username").getValue().toString());
                            }
                        })
                        .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {


                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_comments_layout, parent, false);

                        return new CommentsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {
                        holder.setUsername(model.getUsername());
                        holder.setComment(model.getComment());
                        holder.setDate(model.getDate());
                        holder.setTime(model.getTime());
                    }




                };

        CommentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setUsername (String username){
            TextView mUserName = (TextView) mView.findViewById(R.id.comment_user_name);
            mUserName.setText(" "+username+" ");
        }

        public void setComment (String comment) {
            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }

        public void setDate(String date){
            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText(" Date: "+date);
        }

        public void setTime(String time){
            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText(" Time: "+time);
        }

    }

    private void ValidateComment(String userName) {
        String commentText = CommentInputText.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "Please write text to comment... ", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate= new SimpleDateFormat("dd-MMMM-YYYY");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap=new HashMap();
            commentsMap.put("uid", current_user_id);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            PostRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(CommentsActivity.this, "You have commented Successfully.. ", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(CommentsActivity.this, "Error in updating comment...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
