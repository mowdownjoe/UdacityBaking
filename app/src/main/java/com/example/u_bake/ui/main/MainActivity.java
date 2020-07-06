package com.example.u_bake.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.ActivityMainBinding;
import com.example.u_bake.ui.recipe.steps.StepListActivity;
import com.example.u_bake.utils.LayoutUtils;
import com.example.u_bake.utils.MiscUtils;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeOnClickListener {

    ActivityMainBinding binding;
    MainViewModel viewModel;
    RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.rvRecipeList.setHasFixedSize(true);

        if (LayoutUtils.pxToDp(this, LayoutUtils.getScreenWidthPx(getWindowManager())) >= 900){
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            binding.rvRecipeList.setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rvRecipeList.setLayoutManager(layoutManager);
        }

        adapter = new RecipeAdapter(this);
        binding.rvRecipeList.setAdapter(adapter);

        viewModel.getLoadingStatus().observe(this, loadingStatus -> {
            switch (loadingStatus){
                case LOADING:
                    binding.pbLoadingSpinner.setVisibility(View.VISIBLE);
                    binding.tvErrorText.setVisibility(View.INVISIBLE);
                    binding.rvRecipeList.setVisibility(View.INVISIBLE);
                    break;
                case DONE:
                    binding.rvRecipeList.setVisibility(View.VISIBLE);
                    binding.pbLoadingSpinner.setVisibility(View.INVISIBLE);
                    binding.tvErrorText.setVisibility(View.INVISIBLE);
                    break;
                case ERROR:
                    binding.tvErrorText.setVisibility(View.VISIBLE);
                    binding.rvRecipeList.setVisibility(View.INVISIBLE);
                    binding.pbLoadingSpinner.setVisibility(View.INVISIBLE);
                    break;
            }
        });

        viewModel.getRecipeList().observe(this, recipes -> {
            if (recipes != null){
               adapter.setRecipeData(recipes);
            }
        });

        viewModel.initializeRecipes();
    }

    @Override
    public void onListItemClick(Recipe recipe) {
        startActivity(MiscUtils.buildRecipeIntent(recipe, this));
    }
}