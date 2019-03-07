package com.tony.builder.nqueens.di;

import com.tony.builder.nqueens.view.QueensActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    //@ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    @ContributesAndroidInjector
    abstract QueensActivity contributeQueensActivity();
}
