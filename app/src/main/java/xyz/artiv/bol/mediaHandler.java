package xyz.artiv.bol;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

class mediaHandler implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String LOG_TAG = mediaHandler.class.getSimpleName();
    private static MediaPlayer nMediaPlayer = new MediaPlayer();
    private final Thought chatMessage;
    private String downloadUri;
    private static String currentSource = null;
    private static ImageButton currentImageButton;
    private ImageButton imageButton;

    mediaHandler(Thought chatMessage) {
        this.downloadUri = chatMessage.getDownloadUri();
        this.chatMessage = chatMessage;
    }
    @Override
    public void onClick(View view) {
        Context currentActivity = view.getContext();
        this.imageButton = (ImageButton) view;
        if (currentSource!=null && !downloadUri.contentEquals(currentSource)) {
            currentImageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        currentImageButton = (ImageButton) view;
        nMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        nMediaPlayer.setOnPreparedListener(this);
        nMediaPlayer.setOnErrorListener(this);
        if (!nMediaPlayer.isPlaying()) {
            if (currentSource!=null && downloadUri.contentEquals(currentSource)) {
                imageButton.setImageResource(R.drawable.ic_pause_black_24dp);
                nMediaPlayer.start();
            }
            else {
                nMediaPlayer.reset();
                try {
                    nMediaPlayer.setDataSource(downloadUri);
                    currentSource = downloadUri;
                    nMediaPlayer.prepareAsync();
                    imageButton.setImageResource(R.drawable.ic_pause_black_24dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (chatMessage.getChildren() != null) {
                RecyclerView repliesRecyclerView = (RecyclerView) ((Activity) currentActivity).findViewById(R.id.replies_list);
                repliesRecyclerView.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (currentSource!=null && downloadUri.contentEquals(currentSource)) {
                imageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                nMediaPlayer.pause();
            }
            else {
                nMediaPlayer.reset();
                try {
                    nMediaPlayer.setDataSource(downloadUri);
                    currentSource = downloadUri;
                    nMediaPlayer.prepareAsync();
                    imageButton.setImageResource(R.drawable.ic_pause_black_24dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        imageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.e(LOG_TAG, "Async error");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
    }
}
