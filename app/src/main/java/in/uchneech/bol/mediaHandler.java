package in.uchneech.bol;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

class mediaHandler implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String LOG_TAG = mediaHandler.class.getSimpleName();
    private static MediaPlayer nMediaPlayer = new MediaPlayer();
    private Thought chatMessage;
    private static String currentSource = null;
    private static ImageButton currentImageButton;
    private int position;
    private ImageButton imageButton;

    mediaHandler(Thought chatMessage, int position) {
        this.chatMessage = chatMessage;
        this.position = position;
    }
    @Override
    public void onClick(View view) {
        this.imageButton = (ImageButton) view;
        if (currentSource!=null && !chatMessage.downloadUri.contentEquals(currentSource)) {
            currentImageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        currentImageButton = (ImageButton) view;
        nMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        nMediaPlayer.setOnPreparedListener(this);
        nMediaPlayer.setOnErrorListener(this);
        Log.e(LOG_TAG, Boolean.toString(nMediaPlayer.isPlaying()));
        if (currentSource != null){Log.e(LOG_TAG, currentSource);}
        Log.e(LOG_TAG, chatMessage.downloadUri);
        if (!nMediaPlayer.isPlaying()) {
            if (currentSource!=null && chatMessage.downloadUri.contentEquals(currentSource)) {
                imageButton.setImageResource(R.drawable.ic_pause_black_24dp);
                nMediaPlayer.start();
            }
            else {
                nMediaPlayer.reset();
                try {
                    nMediaPlayer.setDataSource(chatMessage.downloadUri);
                    currentSource = chatMessage.downloadUri;
                    nMediaPlayer.prepareAsync();
                    imageButton.setImageResource(R.drawable.ic_pause_black_24dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            if (currentSource!=null && chatMessage.downloadUri.contentEquals(currentSource)) {
                imageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                nMediaPlayer.pause();
            }
            else {
                nMediaPlayer.reset();
                try {
                    nMediaPlayer.setDataSource(chatMessage.downloadUri);
                    currentSource = chatMessage.downloadUri;
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
        Log.e(LOG_TAG, "complete");
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
