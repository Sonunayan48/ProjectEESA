package com.example.projecteesa.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projecteesa.Adapters.ProfilePostAdapter;
import com.example.projecteesa.LoginActivity;
import com.example.projecteesa.Posts.CreatePostActivity;
import com.example.projecteesa.Posts.Post;
import com.example.projecteesa.ProfileSection.EditProfile;
import com.example.projecteesa.ProfileSection.Profile;
import com.example.projecteesa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    ImageButton fab;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference db = firestore.collection("Users");
    TextView name, email;
    String img = "";
    ImageView imageView;
    Button createPost;
    CardView b1;
    Profile profilex;
    static Profile profileData;
    RecyclerView myPosts;
    RecyclerView.LayoutManager layoutManager;
    ProfilePostAdapter profilePostAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        fab = view.findViewById(R.id.edit_profile_fab);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        createPost=view.findViewById(R.id.add_post);
        b1 = view.findViewById(R.id.finish);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        imageView = view.findViewById(R.id.profile_image);

        myPosts=view.findViewById(R.id.myPosts);
        layoutManager=new GridLayoutManager(getContext(),2);
        myPosts.setLayoutManager(layoutManager);

//        getPosts();
        Log.i("Hello:", "Profile fragment");
        fetchData();
        createPost.setOnClickListener(v->{
            startActivity(new Intent(getContext(), CreatePostActivity.class));
        });
        fab.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), EditProfile.class);
            intent.putExtra("profile", profilex);
            startActivity(intent);
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

    void fetchData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        DocumentReference doc = db.document("" + firebaseUser.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    name.setText(profile.getName());
                    email.setText(profile.getBio());
                    img = profile.getImage();
                    Glide.with(getContext()).load(img).into(imageView);
                    profilex = profile;
                    profileData=profilex;
                }
            }
        });
        progressDialog.dismiss();
        fetchMyPosts();

    }
    public static Profile getProfileData()
    {

        return profileData;
    }
    void fetchMyPosts(){
        ArrayList<Post> myPostList=new ArrayList<>();
        db.document(firebaseUser.getUid()).collection("MyPosts").
                orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    Post post=documentSnapshot.toObject(Post.class);
                    myPostList.add(post);
                }
                Log.e("abc",myPostList.size()+"");
                profilePostAdapter=new ProfilePostAdapter(myPostList,getContext());
                myPosts.setAdapter(profilePostAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "oh yeah I fucked up :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

}