package com.example.chenrui.easycook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecipes extends Fragment{
    private RecyclerView lstRecipes;
    private MyRecipesAdapter rvAdapter;
    private Button addRecipe;
    private ArrayList<Recipe> recipeList;
    private boolean pubFlag = false;
    private RecipeSaver recipeSaver;
    JSONArray idArr;

    public MyRecipes() {
        // Required empty public constructor
    }


    /***
     * setPubFlag
     *
     * @param pubFlag     boolean  Set the flag determining if this is a public or private recipe fragment
     */
    public void setPubFlag(boolean pubFlag) {
        this.pubFlag = pubFlag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        ProfileSaver profileSaver = new ProfileSaver();
        profileSaver.updateProfile(Utils.user,getContext().getFilesDir());

        // Check pubFlag for debugging
        if (pubFlag) {
            System.out.println("Starting public tab");

        } else {
            System.out.println("Starting private tab");

        }

        lstRecipes = view.findViewById(R.id.lstRecipes);
        recipeList= new ArrayList<>();
        recipeSaver = new RecipeSaver();
        /** do something to pass in the recipeList**/
        Toast.makeText(getContext(),"loading data, please wait", Toast.LENGTH_LONG).show();

        // Private recipe fragment
        if (!pubFlag) {

            // Add all private recipes to the list to be displayed
            idArr = Utils.user.getPrivateRecipes();
            for (int i = 0; i < idArr.length(); i++) {
                try {
                    Recipe temp = new Recipe();
                    temp.fromJSON(idArr.getJSONObject(i));
                    recipeList.add(temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Display private recipes
            rvAdapter = new MyRecipesAdapter(recipeList, getContext());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            lstRecipes.setLayoutManager(mLayoutManager);
            lstRecipes.setItemAnimator(new DefaultItemAnimator());
            lstRecipes.setAdapter(rvAdapter);
        }

        // Public recipe fragment
        else {

            // Add all public recipes to the list to be displayed
            idArr = Utils.user.getPublicRecipes();
            if (idArr == null) idArr = new JSONArray();
            System.out.format("Looking for recipes: %s%n",idArr);

            // Get public recipes from cloud storage
            recipeSaver.fetchRecipes(idArr, new RecipeCallback() {
                @Override
                public void onCallBack(JSONArray value) {
                    try{
                        for (int i = 0; i < value.length(); i++){
                            System.out.format("Got public recipe %s%n",value.get(i));
                            recipeList.add((Recipe)value.get(i));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    // Add recipes to list
                    rvAdapter = new MyRecipesAdapter(recipeList, getContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    lstRecipes.setLayoutManager(mLayoutManager);
                    lstRecipes.setItemAnimator(new DefaultItemAnimator());
                    lstRecipes.setAdapter(rvAdapter);
                }
            });
        }


        return view;
    }

}