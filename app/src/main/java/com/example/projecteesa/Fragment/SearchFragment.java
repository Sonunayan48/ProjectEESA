package com.example.projecteesa.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.projecteesa.Adapters.ProfileItemClicked;
import com.example.projecteesa.Adapters.SearchAdapter;
import com.example.projecteesa.ProfileSection.Profile;
import com.example.projecteesa.ProfileSection.UserProfileActivity;
import com.example.projecteesa.R;
import com.example.projecteesa.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class SearchFragment extends Fragment implements ProfileItemClicked {
    SearchView searchView;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference reference;
    RecyclerView.LayoutManager manager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        searchView=view.findViewById(R.id.search_view);
        recyclerView=view.findViewById(R.id.profile_search_recycler);
        manager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        firestore=FirebaseFirestore.getInstance();
        reference=firestore.collection("Users");
        searchquery();
        return view;
    }

    private void searchquery() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchProfiles(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void fetchProfiles(String str) {
        if(str.length()>0) {
            Query query = reference.orderBy("name").startAt(str).endAt(str + "\uf88f");
            FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Profile>()
                    .setQuery(query, Profile.class)
                    .build();
            adapter = new SearchAdapter(options, getContext(), SearchFragment.this);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }


    @Override
    public void profileClicked(String uid) {
        Intent transfer=new Intent(getContext(), UserProfileActivity.class);
        transfer.putExtra(Constants.USER_UID_KEY,uid);
        startActivity(transfer);
    }
}