package com.example.aditi.contactapp2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditi.contactapp2.Database.Contract;
import com.example.aditi.contactapp2.Pojo.Contact;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    TextView txt_name, txt_number;
    CircleImageView img_dp;
    private String name, phone, image;
    private Uri mUri;
    private static final int LOADER_ID = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        txt_name = findViewById(R.id.txt_detail_name);
        txt_number = findViewById(R.id.txt_detail_number);
        img_dp = findViewById(R.id.img_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //catching intent from object
        Contact contacts = getIntent().getParcelableExtra("parcel");

        //catching intent from Uri
        mUri = getIntent().getData();


        //if intent from object is not null run the below code  else uri code
        if (contacts != null) {
            getSupportActionBar().setTitle("Data through object passing");
            name = contacts.getName();
            phone = contacts.getContact();
            image = contacts.getImage();
            txt_name.setText(name);
            txt_number.setText(phone);
            Picasso.with(this).load(image).into(img_dp);

        } else if (mUri != null) {
            getSupportActionBar().setTitle("Data through Uri passing");
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }


    }

    public void addToFav(View view) {

        if (name != null && phone != null && image != null) {
            ContentValues cv = new ContentValues();
            cv.put(Contract.Fav.COLUMN_NAME, name);
            cv.put(Contract.Fav.COLUMN_PHONE, phone);
            cv.put(Contract.Fav.COLUMN_IMAGE, image);
            Uri uri = getContentResolver().insert(Contract.Fav.CONTENT_URI, cv);
            Toast.makeText(this, String.valueOf(uri), Toast.LENGTH_SHORT).show();

        }
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

            @Override
            public Cursor loadInBackground() {
                return getContentResolver().query(mUri, null, null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            Toast.makeText(this, "Null or no rows !!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (data.moveToFirst()) {


            String name = data.getString(data.getColumnIndex(Contract.Fav.COLUMN_NAME));
            txt_name.setText(name);
            String phone = data.getString(data.getColumnIndex(Contract.Fav.COLUMN_PHONE));
            txt_number.setText(phone);
            String image = data.getString(data.getColumnIndex(Contract.Fav.COLUMN_IMAGE));
            Picasso.with(this).load(image).into(img_dp);

            int id = data.getInt(data.getColumnIndex(Contract.Fav._ID));

            Log.i("111", name);
            Log.i("111", phone);
            Log.i("111", image);
            Log.i("111", String.valueOf(id));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
