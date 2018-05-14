package com.example.aditi.contactapp2;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.aditi.contactapp2.Database.Contract;
import com.example.aditi.contactapp2.Pojo.Contact;
import com.example.aditi.contactapp2.Pojo.FavAdapter;

public class FavouriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mfavRecyclerView;
    private FavAdapter mFavAdapter;
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        mfavRecyclerView = findViewById(R.id.favrecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new
                LinearLayoutManager(this);
        mfavRecyclerView.setLayoutManager(mLayoutManager);
        mfavRecyclerView.setItemAnimator(new DefaultItemAnimator());


        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        mFavAdapter = new FavAdapter(this, new FavAdapter.RecyclerViewClickListenerFav() {
            @Override
            public void onClick(Contact contacts, int id) {
               /* Intent i = new Intent(FavouriteActivity.this
                        , DetailActivity.class);
                i.putExtra("parcel", contacts);
                startActivity(i);*/

                Uri uri = Contract.Fav.CONTENT_URI;
                String sid = String.valueOf(id);
                uri = uri.buildUpon().appendPath(sid).build();
                Log.i("111", sid);
                Intent i = new Intent(FavouriteActivity.this, DetailActivity.class);
                //i.putExtra(PrefrencesKeys.Parcelable_key, contacts);
                i.setData(uri);
                startActivity(i);


            }
        });

        mfavRecyclerView.setAdapter(mFavAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                String sid = String.valueOf(id);
                Uri uri = Contract.Fav.CONTENT_URI;
                uri = uri.buildUpon().appendPath(sid).build();
                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(LOADER_ID, null,
                        FavouriteActivity.this);

            }
        }).attachToRecyclerView(mfavRecyclerView);


    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                return getContentResolver()
                        .query(Contract.Fav.CONTENT_URI, null,
                                null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        mFavAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mFavAdapter.swapCursor(null);
    }
}
