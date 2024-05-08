package edu.uncc.giftlistapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.giftlistapp.databinding.FragmentGiftListsBinding;
import edu.uncc.giftlistapp.databinding.ListItemGiftlistBinding;
import models.GiftList;


public class GiftListsFragment extends Fragment {
    public GiftListsFragment() {
        // Required empty public constructor
    }

    FragmentGiftListsBinding binding;
    ArrayList<GiftList> mGiftLists = new ArrayList<>();
    GiftlistsAdapter adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration listenerRegistration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGiftListsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Gift Lists");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GiftlistsAdapter();
        binding.recyclerView.setAdapter(adapter);
        //setup Firebase Snapshot Listener
        String currentUserId= mAuth.getCurrentUser().getUid();
        listenerRegistration = db.collection("giftlists")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("FirestoreError", "listen:error", e);
                            return;
                        }


                        mGiftLists.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            if (doc.exists()) {
                                GiftList giftList = doc.toObject(GiftList.class);
                                giftList.setDocId(doc.getId());
                                mGiftLists.add(giftList);
                                Log.d("DataFetch", "Document data: " + doc.getData());
                            } else {
                                Log.d("DataMissing", "No such document");
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });




    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    class GiftlistsAdapter extends RecyclerView.Adapter<GiftlistsAdapter.GiftListViewHolder>{
        @NonNull
        @Override
        public GiftListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemGiftlistBinding itemBinding = ListItemGiftlistBinding.inflate(getLayoutInflater(), parent, false);
            return new GiftListViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull GiftListViewHolder holder, int position) {
            holder.setupUI(mGiftLists.get(position));
        }

        @Override
        public int getItemCount() {
            return mGiftLists.size();
        }

        class GiftListViewHolder extends RecyclerView.ViewHolder{
            ListItemGiftlistBinding itemBinding;
            GiftList mGiftList;
            public GiftListViewHolder(ListItemGiftlistBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void setupUI(GiftList giftList) {
                this.mGiftList = giftList;
                itemBinding.textViewName.setText(giftList.getName());
                itemBinding.textViewCreatedBy.setText("Created by: " + giftList.getCreator());

                if (giftList.getTags() != null && !giftList.getTags().isEmpty()) {
                    String tags = TextUtils.join(", ", giftList.getTags());
                    itemBinding.textViewSelectedTags.setText(tags);
                } else {
                    itemBinding.textViewSelectedTags.setText("No tags");
                }

                itemBinding.textViewNumItems.setText("Items: " + giftList.getNumItems());

                // Calculate and display the pledged amount vs. total amount
                String pledgedVsTotal = String.format("Pledged: $%.2f of $%.2f", giftList.getTotalPledgedAmount(), giftList.getTotalAmount());
                double percentage = 0;
                if (giftList.getTotalAmount() > 0) {  // Avoid division by zero
                    percentage = (giftList.getTotalPledgedAmount() / giftList.getTotalAmount()) * 100;
                }
                // Combine pledged vs total and percentage in one string for display
                String progressText = String.format("%s - %.0f%% Pledged", pledgedVsTotal, percentage);
                itemBinding.textViewProgress.setText(progressText);
                itemBinding.progressBar.setProgress((int) percentage);
                itemBinding.getRoot().setOnClickListener(v -> {
                    if (giftList.getDocId() != null && !giftList.getDocId().isEmpty()) {
                        Log.e("GiftListDetails", "Document ID is "+giftList.getDocId());
                        mListener.gotoGiftListDetails(giftList.getDocId());
                    } else {
                        Log.e("GiftListDetails", "Document ID is null or empty at UI setup.");
                    }
                });
            }

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            mListener.performLogout();
            return true;
        } else if (item.getItemId() == R.id.action_add){
            mListener.gotoAddNewGiftList();
            return true;
        } else if (item.getItemId() == R.id.action_filter){
            mListener.gotoFilterGiftlists();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    GiftListsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GiftListsListener){
            mListener = (GiftListsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GiftListsListener");
        }
    }

    interface GiftListsListener{
        void gotoAddNewGiftList();
        void performLogout();
        void gotoGiftListDetails(String giftListDocId);
        void gotoFilterGiftlists();
    }
}