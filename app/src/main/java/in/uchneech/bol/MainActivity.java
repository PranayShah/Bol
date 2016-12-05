package in.uchneech.bol;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import in.uchneech.bol.database.FeedReaderContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 10;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    HashMap<String, Thought> thoughts = new HashMap<>();
    private LoaderManager.LoaderCallbacks currentActivity;
    private final ArrayList<String> keys = new ArrayList<>();
    private final int REQUEST_RECORD_AUDIO_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Transition ts = new Fade();
        ts.setDuration(1000);
        getWindow().setEnterTransition(ts);
        getWindow().setExitTransition(ts);
        currentActivity = this;
        setContentView(R.layout.activity_main);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = firebaseDatabase.getReference().child("thoughts");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                thoughts.put(dataSnapshot.getKey(), dataSnapshot.getValue(Thought.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                thoughts.put(dataSnapshot.getKey(), dataSnapshot.getValue(Thought.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                thoughts.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAdapter = new FirebaseRecyclerAdapter<Thought, ThoughtHolder>(Thought.class, R.layout.feed_item, ThoughtHolder.class, mRef) {
            @Override
            public void populateViewHolder(final ThoughtHolder chatMessageViewHolder, final Thought chatMessage, int position) {
                final String key = chatMessage.getKey();
                setImage(chatMessageViewHolder, key);
                chatMessageViewHolder.mView.findViewById(R.id.play_button).setOnClickListener(new mediaHandler(chatMessage));
                chatMessageViewHolder.mView.findViewById(R.id.favourites_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (keys.contains(key) ) {
                            String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_KEY + " = ?";
                            String[] selectionArgs = { key };
                            getContentResolver().delete(FeedReaderContract.FeedEntry.CONTENT_URI, selection, selectionArgs);
                        }
                        else {
                            ContentValues values = new ContentValues();
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY, key);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DOWNLOAD_URI, chatMessage.getDownloadUri());
                            getContentResolver().insert(Uri.withAppendedPath(FeedReaderContract.FeedEntry.CONTENT_URI, key), values);
                        }
                        getLoaderManager().restartLoader(0, null, currentActivity);
                    }
                });
            }
            private void setImage(ThoughtHolder chatMessageViewHolder, String key) {
                if (keys.contains(key) ) {
                    ((ImageButton)chatMessageViewHolder.mView.findViewById(R.id.favourites_button)).setImageResource(R.drawable.ic_favorite_black_24dp);
                }
                else {
                    ((ImageButton)chatMessageViewHolder.mView.findViewById(R.id.favourites_button)).setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }
    public void postStory (View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startRecordingActivity();
        }
        else if (permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, R.string.record_audio_rationale, Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecordingActivity();
                } else {
                    Toast.makeText(this, R.string.record_audio_denied_permission, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startRecordingActivity () {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, RecordingActivity.class);
            startActivity(intent,
                     ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, RecordingActivity.class));
                finish();
            } else {
                Log.w (LOG_TAG, "Not signed in");
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mRecyclerView.setAdapter(null);
        currentActivity = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                mySwipeRefreshLayout.setRefreshing(true);
                refresh();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void refresh () {
        getLoaderManager().restartLoader(0, null, this);
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, FeedReaderContract.FeedEntry.CONTENT_URI,null, null,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        keys.clear();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            keys.add(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_KEY)));
            cursor.moveToNext();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
