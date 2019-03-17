package com.tony.builder.nqueens.di;

import com.tony.builder.nqueens.view.QueensActivity;
import com.tony.builder.nqueens.view.QueensActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    @ActivityScoped
    @ContributesAndroidInjector(modules = QueensActivityModule.class)
    abstract QueensActivity contributeQueensActivity();
}
