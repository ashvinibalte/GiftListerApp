package edu.uncc.giftlistapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import edu.uncc.giftlistapp.databinding.FragmentTagsBinding;
import edu.uncc.giftlistapp.databinding.ListItemTagBinding;

public class TagsFragment extends Fragment {
    String[] mTags = {"Baby Shower", "Birthday", "Farewell", "Graduation","Housewarming", "Retirement", "Seasonal", "Special", "Wedding"};
    public TagsFragment() {
        // Required empty public constructor
    }

    HashSet<String> selectedTags = new HashSet<>();

    FragmentTagsBinding binding;
    TagsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Select Tags");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TagsAdapter();
        binding.recyclerView.setAdapter(adapter);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelTagSelection();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTags.size() == 0){
                    Toast.makeText(getActivity(), "Select some tags", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> tags = new ArrayList<>(selectedTags);
                    Collections.sort(tags);
                    mListener.onTagsSelected(tags);
                }
            }
        });

    }

    class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagsViewHolder> {
        @NonNull
        @Override
        public TagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemTagBinding itemBinding = ListItemTagBinding.inflate(getLayoutInflater(), parent, false);
            return new TagsViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull TagsViewHolder holder, int position) {
            holder.setupUI(mTags[position]);
        }

        @Override
        public int getItemCount() {
            return mTags.length;
        }

        class TagsViewHolder extends RecyclerView.ViewHolder {
            String mTag;
            ListItemTagBinding itemBinding;
            public TagsViewHolder(ListItemTagBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void setupUI(String tag){
                this.mTag = tag;
                itemBinding.textViewTag.setText(tag);
                if(selectedTags.contains(tag)){
                    itemBinding.imageViewSelected.setVisibility(View.VISIBLE);
                } else {
                    itemBinding.imageViewSelected.setVisibility(View.INVISIBLE);
                }

                itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectedTags.contains(mTag)){
                            selectedTags.remove(mTag);
                            itemBinding.imageViewSelected.setVisibility(View.INVISIBLE);
                        } else {
                            selectedTags.add(mTag);
                            itemBinding.imageViewSelected.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }

        }
    }

    TagsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TagsListener){
            mListener = (TagsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TagsListener");
        }
    }

    interface TagsListener{
        void onTagsSelected(ArrayList<String> selectedTags);
        void onCancelTagSelection();
    }

}