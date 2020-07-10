package com.example.u_bake.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.u_bake.R;
import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.CardRecipeItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    @Nullable
    private List<Recipe> recipeList;
    private RecipeOnClickListener onClickListener;

    public interface RecipeOnClickListener{
        void onListItemClick(Recipe recipe);
    }

    public RecipeAdapter(RecipeOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setRecipeData(List<Recipe> recipes){
        recipeList = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        if (recipeList != null){
            holder.bind(recipeList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (recipeList != null) {
            return recipeList.size();
        } else {
            return 0;
        }
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardRecipeItemBinding binding;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardRecipeItemBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        void bind(Recipe recipe){
            binding.tvRecipeName.setText(recipe.getName());
            String imageUrl = recipe.getImage();
            if (!imageUrl.isEmpty()){
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.loading_plate)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .into(binding.ivRecipeImage);
            } else {
                binding.ivRecipeImage.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if (recipeList != null){
                onClickListener.onListItemClick(recipeList.get(getAdapterPosition()));
            }
        }
    }
}
