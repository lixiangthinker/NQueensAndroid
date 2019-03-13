package com.tony.builder.nqueens.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import java.io.IOException;

import javax.inject.Inject;

public class SoundManager {
    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    private SparseIntArray mSoundPoolArray;
    private Context mContext;

    @Inject
    public SoundManager(Context context) {
        mContext = context;
        mSoundPool = new SoundPool.Builder().setMaxStreams(10).build();
        mSoundPoolArray = new SparseIntArray();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * interface for files in /res/raw/
     */
    public void addSound(int index, int soundID) {
        mSoundPoolArray.append(index, mSoundPool.load(mContext, soundID, 1));
    }
    /**
     * interface for files in /assets
     */
    public void addSound(int index, String assetFileName) throws IOException {
        AssetFileDescriptor musicAsset = mContext.getApplicationContext().getAssets().openFd(assetFileName);
        mSoundPoolArray.append(index, mSoundPool.load(musicAsset, 1));
    }

    public void playSound(int index) {
        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolArray.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }

    public void playLoopedSound(int index) {
        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSoundPool.play(mSoundPoolArray.get(index), streamVolume, streamVolume, 1, -1, 1f);
    }

    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }
}
