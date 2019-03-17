package com.tony.builder.nqueens.di;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tony.builder.nqueens.GameApplication;
import com.tony.builder.nqueens.model.QueensModel;
import com.tony.builder.nqueens.model.QueensSingleArray;
import com.tony.builder.nqueens.utils.PixelConverter;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class AppModule {
    @Provides
    public SharedPreferences provideSharedPreferences(GameApplication context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    public QueensModel providesQueensModel() {
        return new QueensSingleArray();
    }

    @Provides
    public PixelConverter providesPixelConverter(GameApplication context) {
        return new PixelConverter(context);
    }
}
