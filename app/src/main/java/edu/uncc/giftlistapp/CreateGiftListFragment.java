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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uncc.giftlistapp.databinding.FragmentCreateGiftListBinding;
import edu.uncc.giftlistapp.databinding.FragmentGiftListBinding;
import edu.uncc.giftlistapp.databinding.ListItemProductBinding;
import models.Product;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateGiftListFragment extends Fragment {
    public CreateGiftListFragment() {
        // Required empty public constructor
    }

    FragmentCreateGiftListBinding binding;
    ArrayList<Product> mProducts = new ArrayList<>();
    ProductsAdapter adapter;
    ArrayList<String> selectedTags = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateGiftListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Gift List");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ProductsAdapter();
        binding.recyclerView.setAdapter(adapter);


        if(selectedTags.size() == 0){
            binding.textViewSelectedTags.setText("Tags: N/A");
        } else {
            binding.textViewSelectedTags.setText("Tags: " + String.join(", ", selectedTags));
        }

        binding.buttonSelectTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoSelectTags();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelCreateGiftList();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editTextGiftListName.getText().toString().isEmpty() || selectedTags.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all required fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalAmount = 0; // Initialize total amount
                Map<String, Object> giftList = new HashMap<>();
                giftList.put("name", binding.editTextGiftListName.getText().toString());
                giftList.put("tags", selectedTags);
                giftList.put("creator", mAuth.getCurrentUser().getDisplayName());
                // Initialize numItems to 0
                giftList.put("numItems", 0);

                for (Product product : mProducts) {
                    if (product.isSelected()) {
                        totalAmount += product.getPrice(); // Sum up the prices of selected products
                    }
                }

                giftList.put("totalAmount", totalAmount); // Add total amount to the gift list document

                db.collection("giftlists").add(giftList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String giftListId = documentReference.getId();
                        AtomicInteger productCount = new AtomicInteger(0);

                        for (Product product : mProducts) {
                            if (product.isSelected()) {
                                Map<String, Object> productData = new HashMap<>();
                                productData.put("name", product.getName());
                                productData.put("price", product.getPrice());
                                productData.put("img_url", product.getImg_url());
                                productData.put("pledged", false);


                                db.collection("giftlists").document(giftListId).collection("giftlists-products")
                                        .add(productData)
                                        .addOnSuccessListener(docRef -> {
                                            db.collection("giftlists").document(giftListId)
                                                    .update("numItems", FieldValue.increment(1))
                                                    .addOnSuccessListener(aVoid -> {
                                                        int currentCount = productCount.incrementAndGet();
                                                        if (currentCount == mProducts.stream().filter(Product::isSelected).count()) {
                                                            mListener.doneCreateGiftList();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> Log.e("Error", "Failed to increment numItems", e));
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Error", "Failed to add product to the list", e);
                                        });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error adding document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
    private boolean isAnyProductSelected(){
        for (Product product:mProducts) {
            if(product.isSelected()){
                return true;
            }

        }
        return false;
    }



    private void calculateAndDisplayTotal() {
        // Calculate total cost
        double totalCost = 0;
        for (Product product : mProducts) {
            if (product.isSelected()) {
                totalCost += product.getPrice(); // Assume getPrice() returns the cost of the product
            }
        }

        // Update the TextView to display the total cost
        // Ensure you format the number to two decimal places for monetary display
        binding.textViewTotalCost.setText(String.format("$%.2f", totalCost));
    }

    public void updateSelectedTags(ArrayList<String> tags){
        selectedTags.clear();
        selectedTags.addAll(tags);
    }

    private final OkHttpClient client = new OkHttpClient();

    private void getProducts(){
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/products")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to get products. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        mProducts.clear();
                        JSONObject json = new JSONObject(body);
                        JSONArray courses = json.getJSONArray("products");
                        for (int i = 0; i < courses.length(); i++) {
                            Product product = new Product(courses.getJSONObject(i));
                            mProducts.add(product);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter != null){
                                    adapter.notifyDataSetChanged();
                                    calculateAndDisplayTotal();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Failed to get products", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }



    class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemProductBinding itemBinding = ListItemProductBinding.inflate(getLayoutInflater(), parent, false);
            return new ProductViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.setupUI(mProducts.get(position));
        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            ListItemProductBinding itemBinding;
            Product mProduct;
            public ProductViewHolder(ListItemProductBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void setupUI(Product product){
                mProduct = product;
                itemBinding.textViewName.setText(product.getName());
                itemBinding.textViewCostPerItem.setText(String.format("$%.2f", product.getPrice()));

                if(mProduct.isSelected()){
                    itemBinding.imageViewPlusOrMinus.setImageResource(R.drawable.ic_minus);
                } else {
                    itemBinding.imageViewPlusOrMinus.setImageResource(R.drawable.ic_plus);
                }

                itemBinding.imageViewPlusOrMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mProduct.isSelected()){
                            mProduct.setSelected(false);
                        } else {
                            mProduct.setSelected(true);
                        }
                        notifyDataSetChanged();
                        calculateAndDisplayTotal();
                    }
                });
                Picasso.get().load(product.getImg_url()).into(itemBinding.imageViewIcon);
            }
        }
    }

    CreateGiftListListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateGiftListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CreateGiftListListener");
        }
    }

    interface CreateGiftListListener{
        void cancelCreateGiftList();
        void doneCreateGiftList();
        void gotoSelectTags();
    }
}