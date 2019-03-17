package com.tony.builder.nqueens.view;

import com.tony.builder.nqueens.di.ActivityScoped;
import com.tony.builder.nqueens.utils.SoundManager;

import dagger.Module;
import dagger.Provides;

@Module
public class QueensActivityModule {
    @ActivityScoped
    @Provides
    public SoundManager providesSoundManager(QueensActivity context) {
        return new SoundManager(context);
    }
}
