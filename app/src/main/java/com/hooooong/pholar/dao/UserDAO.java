package com.hooooong.pholar.dao;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hooooong.pholar.model.Post;
import com.hooooong.pholar.model.PostThumbnail;
import com.hooooong.pholar.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heepie on 2017. 11. 8..
 * User 객체와 Firebase를 연결하는 클래스
 */

public class UserDAO {
    private final String TAG = getClass().getSimpleName();
    private final int DEFAULT_GET_RECOMMEND_FRIEND = 15;

    // For Singleton Pattern
    private static UserDAO instance;

    private FirebaseDatabase database;
    private DatabaseReference userRef;

    public static UserDAO getInstance() {
        if (instance == null)
            instance = new UserDAO();
        return instance;
    }

    private UserDAO() {
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
    }

    public void create(Post info) {
        // insert the Data

    }

    public List<Post> readALL () {
        List<Post> tmp = new ArrayList<>();

        return tmp;
    }

    public void read(final UserDAO.ICallback callback) {
        final List<User> data = new ArrayList<>();

        Query getTopNewPost = userRef.limitToFirst(DEFAULT_GET_RECOMMEND_FRIEND);
        getTopNewPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User item = snapshot.getValue(User.class);

                    setInnerObject(dataSnapshot, item);

                    data.add(item);
                    Log.d(TAG, "read: " + item.toString());
                }

                callback.getUserFromFirebaseDB(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void readByUserId (final UserDAO.ICallback callback, String user_id) {

        Query getSinglePost = userRef.child(user_id);

        getSinglePost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User item = dataSnapshot.getValue(User.class);

                    Log.d(TAG, "readByUserId: " + item.toString());

                    setInnerObject(dataSnapshot, item);

                    callback.getSingleUserFromFirebaseDB(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void setInnerObject(DataSnapshot dataSnapshot, User item) {
        if (dataSnapshot.hasChild("post_thumbnail")) {
            DataSnapshot photoSnapshot = dataSnapshot.child("post_thumbnail");

            List<PostThumbnail> list = new ArrayList<>();

            for(DataSnapshot data : photoSnapshot.getChildren()){
                PostThumbnail postThumbnail = data.getValue(PostThumbnail.class);
                Log.e("heepie", "IN");
                list.add(postThumbnail);
            }

            item.setPost_thumbnail(list);
        }
    }

    // Firebase에서 Read한 결과를 리턴해주는 Interface
    public interface ICallback {
        void getUserFromFirebaseDB (List<User> data);
        void getSingleUserFromFirebaseDB (User item);
    }
}
