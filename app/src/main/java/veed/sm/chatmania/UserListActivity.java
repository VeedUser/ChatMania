package veed.sm.chatmania;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private String userName;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;


    private ArrayList<User> usersArrayList;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        if(intent != null){
            userName = intent.getStringExtra(userName);

        }


        auth = FirebaseAuth.getInstance();

        usersArrayList = new ArrayList<>();

        attachUserDatabaseReferenceListener();
        buildRecyclerView();


    }

    private void attachUserDatabaseReferenceListener() {
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (usersChildEventListener == null){
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot,String previousChildName) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getId().equals( auth.getCurrentUser().getUid())) {
                        user.setAvatarMockUpResource(R.drawable.ic_baseline_person_24);
                        usersArrayList.add(user);
                        userAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot snapshot,String previousChildName) {

                }

                @Override
                public void onChildRemoved(DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot snapshot,String previousChildName) {

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            };

            usersDatabaseReference.addChildEventListener(usersChildEventListener);

        }



    }

    private void buildRecyclerView() {

        userRecyclerView = findViewById(R.id.userListRecyclerView);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(userRecyclerView.getContext(),DividerItemDecoration.VERTICAL));
        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(usersArrayList);

        userRecyclerView.setLayoutManager(userLayoutManager);
        userRecyclerView.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                goToChat(position);
            }
        });

    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this,
                ChatActivity.class);
        intent.putExtra("recipientUserId",
                usersArrayList.get(position).getId());
        intent.putExtra("recipientUserName",
                usersArrayList.get(position).getName());
        intent.putExtra("userName",userName);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserListActivity.this,SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}