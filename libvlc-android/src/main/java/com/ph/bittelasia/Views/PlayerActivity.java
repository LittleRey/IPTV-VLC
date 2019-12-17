package com.ph.bittelasia.Views;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.videolan.R;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import com.ph.bittelasia.Model.OnChangeListener;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static org.videolan.libvlc.MediaPlayer.Event.Buffering;
import static org.videolan.libvlc.MediaPlayer.Event.ESAdded;
import static org.videolan.libvlc.MediaPlayer.Event.ESDeleted;
import static org.videolan.libvlc.MediaPlayer.Event.ESSelected;
import static org.videolan.libvlc.MediaPlayer.Event.EncounteredError;
import static org.videolan.libvlc.MediaPlayer.Event.EndReached;
import static org.videolan.libvlc.MediaPlayer.Event.LengthChanged;
import static org.videolan.libvlc.MediaPlayer.Event.MediaChanged;
import static org.videolan.libvlc.MediaPlayer.Event.Opening;
import static org.videolan.libvlc.MediaPlayer.Event.Paused;
import static org.videolan.libvlc.MediaPlayer.Event.Playing;
import static org.videolan.libvlc.MediaPlayer.Event.PositionChanged;
import static org.videolan.libvlc.MediaPlayer.Event.SeekableChanged;
import static org.videolan.libvlc.MediaPlayer.Event.Stopped;
import static org.videolan.libvlc.MediaPlayer.Event.TimeChanged;
import static org.videolan.libvlc.MediaPlayer.Event.Vout;

public abstract class PlayerActivity extends Activity implements MediaPlayer.EventListener, OnChangeListener {

    private static final String TAG = "VLC/PlayerActivity";

    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private VLCVideoLayout mVideoLayout = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    private ArrayList<Media> programs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surface_layout);
        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.setEventListener(this);

        mVideoLayout = findViewById(R.id.video_layout);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
        try {
            final Media media = new Media(mLibVLC, Uri.parse(getPath()));
            mMediaPlayer.setMedia(media);
            media.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.play();

    }

    @Override
    protected void onStop() {
        super.onStop();

        mMediaPlayer.stop();
        mMediaPlayer.detachViews();
    }


    //===================================Media.EventListener========================================
    //----------------------------------------------------------------------------------------------
    @Override
    public void onEvent(MediaPlayer.Event event) {
        switch (event.type)
        {
            case MediaChanged:
                Log.i(TAG,"@MediaChanged");
                Toast.makeText(this, "@MediaChanged", Toast.LENGTH_SHORT).show();
                break;
            case Opening:
                Log.i(TAG,"@Opening");
                break;
            case Buffering:
                onBufferChanged(Math.round(event.getBuffering()));
                break;
            case Playing:
                onLoadComplete();
                break;
            case Paused:
                Log.i(TAG,"@Paused");
                break;
            case Stopped:
                Log.i(TAG,"@Stopped");
                break;
            case EndReached:
                onEnd();
                break;
            case EncounteredError:
                Log.e(TAG,"@EncounteredError");
                onError("@EncounteredError");
                break;
            case TimeChanged:
                Log.i(TAG,"@TimeChanged: "+((new SimpleDateFormat("mm:ss:SSS", Locale.ENGLISH)).format(new Date(event.getTimeChanged()))));
                break;
            case PositionChanged:
                onChanging();
                break;
            case SeekableChanged:
                Log.i(TAG,"@SeekableChanged");
                break;
            case LengthChanged:
                Log.i(TAG,"@LengthChanged");
                break;
            case Vout:
                Log.i(TAG,"@Vout: "+event.getVoutCount());
                break;
            case ESAdded:
                Log.i(TAG,"@ESAdded");
                break;
            case ESDeleted:
                Log.i(TAG,"@ESDeleted");
                break;
            case ESSelected:
                Log.i(TAG,"@ESSelected");
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    //==============================================================================================

    public abstract String getPath();
}
