package com.shady_selim.azkar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shady_selim.azkar.firebaseDB.AzkarClass;
import com.shady_selim.azkar.firebaseDB.AzkarListClass;
import com.shady_selim.azkar.settings.SettingsActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindBool;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private List<AzkarClass> azkarList;
    private SharedPreferences preferences;
    private boolean mTwoPane;
    private boolean isFavorite = false;
    @BindBool(R.bool.isTablet) boolean isTablet;
    @BindView(R.id.pb_loading_indicator) ProgressBar pb;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    @BindView(R.id.zikr_list) RecyclerView mRecyclerView;
    @BindString(R.string.app_name) String title;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    isFavorite = false;
                    setupRecyclerView(mRecyclerView);
                    return true;
                case R.id.navigation_favorite:
                    isFavorite = true;
                    setupRecyclerView(mRecyclerView);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(title);

        preferences = getSharedPreferences("zikr", 0);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        pb.setVisibility(View.VISIBLE);

        if (findViewById(R.id.zikr_detail_container) != null) {
            mTwoPane = true;
        }

        if (savedInstanceState == null){
            new FirebaseAsyncTask().execute("arabic");

        }else {
            azkarList = Parcels.unwrap(savedInstanceState.getParcelable("azkarList"));
            isFavorite = savedInstanceState.getBoolean("isFavorite");
            setupRecyclerView(mRecyclerView);
        }

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(azkarList));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("azkarList", Parcels.wrap(azkarList));
        outState.putBoolean("isFavorite", isFavorite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult((new Intent(getBaseContext(), SettingsActivity.class)),0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final List<AzkarClass> mValues;

        public SimpleItemRecyclerViewAdapter(List<AzkarClass> azkarList) {
            mValues = azkarList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zikr_list_content, parent, false);
            pb.setVisibility(View.GONE);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {
            int exist = -1;
            if (preferences.contains("favorite")) {
                List<String> favorite = new Gson().fromJson(preferences.getString("favorite", null),new TypeToken<List<String>>() {}.getType());
                exist= favorite.indexOf(position+"");
            }
            if (exist == -1 && isFavorite) {
                holder.mList.setVisibility(View.GONE);
            }else{
                changeButton(exist ,holder.mFavorite, mValues.get(position).name);
                holder.mItem = mValues.get(position);
                holder.mContentView.setText(mValues.get(position).name);
                holder.mFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {

                                List<String> favorite = new Gson().fromJson(preferences.getString("favorite", null),new TypeToken<List<String>>() {}.getType());
                                if (favorite == null){
                                    favorite = new ArrayList<>();
                                }
                                if (favorite.indexOf(position+"") == -1){
                                    favorite.add(position+"");
                                    changeButton(position,holder.mFavorite, mValues.get(position).name);
                                }else{
                                    favorite.remove(position+"");
                                    changeButton(-1,holder.mFavorite, mValues.get(position).name);
                                    if(isFavorite){
                                        holder.mList.setVisibility(View.GONE);
                                    }
                                }
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("favorite", new Gson().toJson(favorite));
                                editor.apply();
                            }catch (Exception e){
                                Log.e("List Click Error", e.getMessage());
                            }
                        }
                    }
                );
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString("name", holder.mItem.name);
                        arguments.putParcelable("azkar",Parcels.wrap(holder.mItem.azkar));
                        ZikrDetailFragment fragment = new ZikrDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction().replace(R.id.zikr_detail_container, fragment).commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ZikrDetailActivity.class);
                        intent.putExtra("name", holder.mItem.name);
                        intent.putExtra("azkar", Parcels.wrap(holder.mItem.azkar));
                        context.startActivity(intent);
                    }
                }
            });
        }

        void changeButton(int exist, ImageButton v, String s){
            if (exist != -1){
                v.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                v.setContentDescription(s + getString(R.string.remove_from_fav));
            }else {
                v.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                v.setContentDescription(s + getString(R.string.add_to_fav));
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public final View mView;
            public AzkarClass mItem;
            @BindView(R.id.content) TextView mContentView;
            @BindView(R.id.favorite_button) ImageButton mFavorite;
            @BindView(R.id.list_content) RelativeLayout mList;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }
    }

    private class FirebaseAsyncTask extends AsyncTask<String,Void,DatabaseReference> {
        @Override
        protected DatabaseReference doInBackground(String... strings) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            DatabaseReference myRef = database.getReference(strings[0]);
            return myRef;
        }

        @Override
        protected void onPostExecute(DatabaseReference result) {
            super.onPostExecute(result);
            result.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("test", "Count is: " + dataSnapshot.getChildrenCount());
                    azkarList = new ArrayList<>();
                    for(DataSnapshot templateSnapshot : dataSnapshot.getChildren()){
                        azkarList.add(templateSnapshot.getValue(AzkarClass.class)) ;
                    }

                    List<AzkarListClass> azkaListClassList = new ArrayList<>();
                    for (int i = 0; i < azkarList.size(); i++){
                        azkaListClassList.addAll(azkarList.get(i).azkar);
                    }


                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("azkar", new Gson().toJson(azkaListClassList));
                    editor.apply();

                    setupRecyclerView(mRecyclerView);

                    Log.e("test", "azkaListClassList is: " + azkaListClassList.size());
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("test", "Failed to read value.", error.toException());
                }
            });
        }
    }
}