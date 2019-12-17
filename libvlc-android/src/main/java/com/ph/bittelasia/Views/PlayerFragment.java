package com.ph.bittelasia.Views;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



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

public abstract class PlayerFragment extends Fragment implements MediaPlayer.EventListener, OnChangeListener {

    //=========================================Variable=============================================
    //-----------------------------------------Constant---------------------------------------------
    public static PlayerFragment fragment;
    private static final String TAG = "VLC/PlayerActivity";
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    //----------------------------------------------------------------------------------------------
    //-----------------------------------------Instance---------------------------------------------
    private VLCVideoLayout mVideoLayout = null;
    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    //----------------------------------------------------------------------------------------------
    //==============================================================================================



    //========================================LifeCycle=============================================
    //----------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.surface_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            fragment = this;
            final ArrayList<String> args = new ArrayList<>();
            args.add("-vvv");

            mLibVLC = new LibVLC(view.getContext(), args);
            mMediaPlayer = new MediaPlayer(mLibVLC);
            mMediaPlayer.setEventListener(this);

            mVideoLayout = view.findViewById(R.id.video_layout);


        }catch (Exception e)
        {
            onError(e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mMediaPlayer.release();
            mLibVLC.release();
        }catch (Exception e)
        {
            onError(e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
            try {
                final Media media = new Media(mLibVLC, Uri.parse(getPath()));
                mMediaPlayer.setMedia(media);
                media.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaPlayer.play();
        }catch (Exception e)
        {
            onError(e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mMediaPlayer.stop();
            mMediaPlayer.detachViews();
        }catch (Exception e)
        {
            onError(e.getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------
    //==============================================================================================

    //===================================Media.EventListener========================================
    //----------------------------------------------------------------------------------------------
    @Override
    public void onEvent(MediaPlayer.Event event) {
        try {
            switch (event.type) {
                case MediaChanged:
                    Log.i(TAG, "@MediaChanged");
                    break;
                case Opening:
                    Log.i(TAG, "@Opening");
                    break;
                case Buffering:
                    onBufferChanged(Math.round(event.getBuffering()));
                    break;
                case Playing:
                    onLoadComplete();
                    break;
                case Paused:
                    Log.i(TAG, "@Paused");
                    break;
                case Stopped:
                    Log.i(TAG, "@Stopped");
                    break;
                case EndReached:
                    onEnd();
                    break;
                case EncounteredError:
                    Log.e(TAG, "@EncounteredError");
                    onError("@EncounteredError");
                    break;
                case TimeChanged:
                    Log.i(TAG, "@TimeChanged: " + ((new SimpleDateFormat("mm:ss:SSS", Locale.ENGLISH)).format(new Date(event.getTimeChanged()))));
                    break;
                case PositionChanged:
                    onChanging();
                    break;
                case SeekableChanged:
                    Log.i(TAG, "@SeekableChanged");
                    break;
                case LengthChanged:
                    Log.i(TAG, "@LengthChanged");
                    break;
                case Vout:
                    Log.i(TAG, "@Vout: " + event.getVoutCount());
                    break;
                case ESAdded:
                    Log.i(TAG, "@ESAdded");
                    break;
                case ESDeleted:
                    Log.i(TAG, "@ESDeleted");
                    break;
                case ESSelected:
                    Log.i(TAG, "@ESSelected");
                    break;
            }
            if (mMediaPlayer != null) {
                mMediaPlayer.updateVideoSurfaces();
                mMediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_FILL);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------------------------------------------------
    //==============================================================================================


    public abstract String getPath();
}
