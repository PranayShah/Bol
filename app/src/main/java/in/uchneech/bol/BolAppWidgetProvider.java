package in.uchneech.bol;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BolAppWidgetProvider extends AppWidgetProvider {
    private static final String SHUFFLE_ACTION = "in.uchneech.bol.SHUFFLE_ACTION";
    private static final String PLAY_ACTION = "in.uchneech.bol.PLAY_ACTION";
    static RemoteViews views;
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {

            views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent playIntent = new Intent(context, mediaService.class);
            playIntent.setAction(PLAY_ACTION);
            PendingIntent playIntentPending = PendingIntent.getService(context, 0, playIntent, 0);
            Intent shuffleIntent = new Intent(context, mediaService.class);
            shuffleIntent.setAction(SHUFFLE_ACTION);
            PendingIntent shuffleIntentPending = PendingIntent.getService(context, 0, shuffleIntent, 0);
            views.setOnClickPendingIntent(R.id.play_button, playIntentPending);
            views.setOnClickPendingIntent(R.id.shuffle, shuffleIntentPending);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static class mediaService extends IntentService implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
        private static final String LOG_TAG = mediaService.class.getSimpleName();
        private static MediaPlayer nMediaPlayer = new MediaPlayer();
        DatabaseReference databaseReference;
        Map<String, Thought> thoughts = new HashMap<>();
        private static String currentSource = null;
        private AppWidgetManager appWidgetManager;
        private int[] appWidgetIds;
        private RemoteViews views;

        public mediaService() {
            super(LOG_TAG);
        }

        public mediaService(String name) {
            super(name);
        }

        @Override
        public void onCreate() {
            appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BolAppWidgetProvider.class));
            views = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
            nMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            nMediaPlayer.setOnPreparedListener(this);
            nMediaPlayer.setOnErrorListener(this);
            databaseReference = FirebaseDatabase.getInstance().getReference("thoughts");
            databaseReference.addChildEventListener(new ChildEventListener() {
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

            super.onCreate();
        }

        private String getRandom(Map map) {
            Random generator = new Random();
            Object[] values = map.values().toArray();
            return ((Thought) values[generator.nextInt(values.length)]).downloadUri;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent.getAction().equals(SHUFFLE_ACTION)) {
                currentSource = getRandom(thoughts);
                nMediaPlayer.reset();
                try {
                    nMediaPlayer.setDataSource(currentSource);
                    nMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(PLAY_ACTION)) {
                Log.d(LOG_TAG, String.valueOf(nMediaPlayer.isPlaying()));
                if (nMediaPlayer.isPlaying()) {
                    nMediaPlayer.pause();
                    changeButtonResourceToPlay(true);
                } else {
                    Log.d(LOG_TAG, currentSource == null ? "null" : currentSource);
                    if (currentSource == null) {
                        currentSource = getRandom(thoughts);
                        try {
                            nMediaPlayer.setDataSource(currentSource);
                            nMediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        nMediaPlayer.start();
                        changeButtonResourceToPlay(false);
                    }
                }
            }
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.d(LOG_TAG, "complete");
            changeButtonResourceToPlay(true);
        }

        private void changeButtonResourceToPlay(boolean b) {
            if (b) {
                for (int appWidgetId : appWidgetIds) {
                    views.setImageViewResource(R.id.play_button, R.drawable.ic_play_arrow_black_24dp);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
            else {
                for (int appWidgetId : appWidgetIds) {
                    views.setImageViewResource(R.id.play_button, R.drawable.ic_pause_black_24dp);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            Log.e(LOG_TAG, String.valueOf(i) + String.valueOf(i1));
            return true;
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
            changeButtonResourceToPlay(false);
        }
    }
}
