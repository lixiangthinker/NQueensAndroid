package com.tony.builder.nqueens.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tony.builder.nqueens.GameApplication;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class AppModule {
    @Provides
    public SharedPreferences provideSharedPreferences(GameApplication context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
