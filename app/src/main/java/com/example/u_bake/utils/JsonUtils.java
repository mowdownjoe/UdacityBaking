package com.example.u_bake.utils;

import androidx.annotation.Nullable;

import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.IngredientJsonAdapter;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.InstructionJsonAdapter;
import com.example.u_bake.data.Recipe;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;

public class JsonUtils {

    @Nullable
    public static Recipe[] parseRecipeListJSON(String jsonString) throws IOException {
        Moshi base = new Moshi.Builder().build();
        Moshi moshi = base.newBuilder()
                .add(Instruction.class, new InstructionJsonAdapter(base))
                .add(Ingredient.class, new IngredientJsonAdapter(base))
                .build();
        JsonAdapter<Recipe[]> adapter = moshi.adapter(Types.arrayOf(Recipe.class));
        Recipe[] recipes = adapter.fromJson(jsonString);
        if (recipes != null && recipes.length != 0){
            return recipes;
        } else {
            return null;
        }
    }
}
