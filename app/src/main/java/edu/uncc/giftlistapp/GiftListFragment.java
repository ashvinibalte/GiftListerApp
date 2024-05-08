package edu.uncc.giftlistapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uncc.giftlistapp.databinding.FragmentGiftListBinding;
import edu.uncc.giftlistapp.databinding.ListItemGiftlistProductBinding;
import models.GiftList;
import models.GiftListProduct;

public class GiftListFragment extends Fragment {

    private static final String ARG_PARAM_GIFTLIST_DOCID = "ARG_PARAM_GIFTLIST_DOCID";

    private String mGiftListDocId;
    private ListenerRegistration giftListListenerRegistration;
    private ListenerRegistration productListListenerRegistration;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ;
    public static GiftListFragment newInstance(String giftListDocId) {
        GiftListFragment fragment = new GiftListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_GIFTLIST_DOCID, giftListDocId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mGiftListDocId = getArguments().getString(ARG_PARAM_GIFTLIST_DOCID);
            if (mGiftListDocId != null) {
                Log.d("GiftListFragment", "Received Gift List Document ID: " + mGiftListDocId);
            } else {
                Log.e("GiftListFragment", "No Gift List Document ID found in arguments");
            }
        } else {
            Log.e("GiftListFragment", "No arguments set for Gift List Fragment");
        }
    }


    public GiftListFragment() {
        // Required empty public constructor
    }

    FragmentGiftListBinding binding;
    ArrayList<GiftListProduct> mGiftListProducts = new ArrayList<>();
    GiftlistProductsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGiftListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Gift List");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GiftlistProductsAdapter();
        binding.recyclerView.setAdapter(adapter);

        setupSnapshotListeners();


    }
    private void setupSnapshotListeners() {
        if (mGiftListDocId == null || mGiftListDocId.isEmpty()) {
            Log.e("GiftListFragment", "Document ID is null or empty.");
            return;
        }

        // Listener for the gift list document
        DocumentReference giftListRef = FirebaseFirestore.getInstance().collection("giftlists").document(mGiftListDocId);
        giftListListenerRegistration = giftListRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("FirestoreError", "listen:error", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Assuming you have a method to handle the data object
                GiftList giftList = documentSnapshot.toObject(GiftList.class);
                // Update your UI here based on the details of giftList
                updateGiftListUI(giftList);
            } else {
                Log.e("DataMissing", "No such gift list document");
            }
        });

        // Listener for the products in the gift list
        productListListenerRegistration = giftListRef.collection("giftlists-products").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirestoreError", "Product listen:error", e);
                return;
            }

            if (querySnapshot != null) {
                ArrayList<GiftListProduct> products = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    GiftListProduct product = doc.toObject(GiftListProduct.class);
                    product.setGiftListId(mGiftListDocId);  // Ensure this is correctly set
                    product.setDocId(doc.getId());  // Ensure this is correctly set
                    products.add(product);
                }
                mGiftListProducts.clear();
                mGiftListProducts.addAll(products);
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (giftListListenerRegistration != null) {
            giftListListenerRegistration.remove();
        }
        if (productListListenerRegistration != null) {
            productListListenerRegistration.remove();
        }
    }
    private void updateGiftListUI(GiftList giftList) {
        binding.textViewGiftListName.setText(giftList.getName());
        binding.textViewCreatedBy.setText("Created by: " + giftList.getCreator());

        // Calculate and display the pledged amount vs. total amount
        String pledgedVsTotal = String.format("Pledged: $%.2f of $%.2f", giftList.getTotalPledgedAmount(), giftList.getTotalAmount());
        double percentage = 0;
        if (giftList.getTotalAmount() > 0) {  // Avoid division by zero
            percentage = (giftList.getTotalPledgedAmount() / giftList.getTotalAmount()) * 100;
        }
        // Combine pledged vs total and percentage in one string for display
        String progressText = String.format("%s - %.0f%% Pledged", pledgedVsTotal, percentage);
        binding.textViewProgress.setText(progressText);

        // Update progress bar
        binding.progressBar.setProgress((int) percentage);

        // Handling click events on the Gift List
        binding.getRoot().setOnClickListener(v -> {
            if (giftList.getDocId() != null && !giftList.getDocId().isEmpty()) {
                Log.d("GiftListDetails", "Navigating to details of Document ID: " + giftList.getDocId());
               // mListener.gotoGiftListDetails(giftList.getDocId());
            } else {
                Log.e("GiftListDetails", "Document ID is null or empty at UI setup.");
            }
        });
    }


    class GiftlistProductsAdapter extends RecyclerView.Adapter<GiftlistProductsAdapter.GiftListProductViewHolder>{
        @NonNull
        @Override
        public GiftListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemGiftlistProductBinding itemBinding = ListItemGiftlistProductBinding.inflate(getLayoutInflater(), parent, false);
            return new GiftListProductViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull GiftListProductViewHolder holder, int position) {
            holder.setupUI(mGiftListProducts.get(position));
        }

        @Override
        public int getItemCount() {
            return mGiftListProducts.size();
        }

        class GiftListProductViewHolder extends RecyclerView.ViewHolder{
            GiftListProduct mGiftListProduct;
            ListItemGiftlistProductBinding itemBinding;
            public GiftListProductViewHolder(ListItemGiftlistProductBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void setupUI(GiftListProduct giftListProduct) {
                itemBinding.textViewName.setText(giftListProduct.getName());
                itemBinding.textViewCostPerItem.setText(String.format("$%.2f", giftListProduct.getPrice()));
                Picasso.get().load(giftListProduct.getImg_url()).into(itemBinding.imageViewIcon);
                // Update UI based on pledge status
                if (giftListProduct.isPledged()) {
                    itemBinding.imageViewPlegeOrNot.setImageResource(R.drawable.ic_check_fill);
                    itemBinding.textViewPledgedBy.setText("Pledged by: " + giftListProduct.getPledgedBy());
                } else {
                    itemBinding.imageViewPlegeOrNot.setImageResource(R.drawable.ic_check_not_fill);
                    itemBinding.textViewPledgedBy.setText("Not pledged");
                }



                // Click listener for the pledge icon
                itemBinding.imageViewPlegeOrNot.setOnClickListener(v -> {
                    if (!giftListProduct.isCreatedByCurrentUser()) {
                        // Toggle pledge status
                        giftListProduct.setPledged(!giftListProduct.isPledged());
                        giftListProduct.setPledgedBy(giftListProduct.isPledged() ? FirebaseAuth.getInstance().getCurrentUser().getDisplayName() : "");
                        giftListProduct.setPledgedAmount(giftListProduct.isPledged() ? giftListProduct.getPrice() : 0); // Update pledged amount
                        updateProductInFirestore(giftListProduct); // Update the product in Firestore
                    } else {
                        Toast.makeText(getContext(), "You cannot pledge/unpledge your own products.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void updateGiftListTotalPledged(String giftListId, double pledgedAmountChange) {
                DocumentReference giftListRef = FirebaseFirestore.getInstance().collection("giftlists").document(giftListId);
                giftListRef.update("totalPledgedAmount", FieldValue.increment(pledgedAmountChange))
                        .addOnSuccessListener(aVoid -> Log.d("UpdateTotalPledged", "Total pledged amount updated successfully"))
                        .addOnFailureListener(e -> Log.e("UpdateTotalPledged", "Error updating total pledged amount", e));
            }


            private void updatePledgeStatus(GiftListProduct giftListProduct) {
                if (giftListProduct.isPledged()) {
                    itemBinding.imageViewPlegeOrNot.setImageResource(R.drawable.ic_check_fill);
                    itemBinding.textViewPledgedBy.setText("Pledged by: " + giftListProduct.getPledgedBy());
                } else {
                    itemBinding.imageViewPlegeOrNot.setImageResource(R.drawable.ic_check_not_fill);
                    itemBinding.textViewPledgedBy.setText("Not pledged");
                }
            }

            private void updateProductInFirestore(GiftListProduct product) {
                DocumentReference productRef = FirebaseFirestore.getInstance().collection("giftlists")
                        .document(product.getGiftListId()).collection("giftlists-products").document(product.getDocId());

                // Calculate the pledged amount change based on whether the product is pledged or un-pledged
                double pledgedAmountChange = product.isPledged() ? product.getPrice() : -product.getPrice();

                Map<String, Object> updates = new HashMap<>();
                updates.put("pledged", product.isPledged());
                updates.put("pledgedBy", product.getPledgedBy());
                updates.put("pledgedAmount", product.getPledgedAmount()); // Update pledged amount in Firestore

                // Update the product in Firestore
                productRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirestoreUpdate", "Product updated successfully");
                            // Update the total pledged amount of the gift list
                            updateGiftListTotalPledged(product.getGiftListId(), pledgedAmountChange);
                        })
                        .addOnFailureListener(e -> Log.e("FirestoreUpdate", "Error updating product", e));
            }



        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cancel_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cancel){
            mListener.cancelGiftListDetail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    GiftListListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GiftListListener){
            mListener = (GiftListListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GiftListListener");
        }
    }

    interface GiftListListener{
        void cancelGiftListDetail();
    }
}