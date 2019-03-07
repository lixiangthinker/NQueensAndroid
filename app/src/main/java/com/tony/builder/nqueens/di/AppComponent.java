package com.tony.builder.nqueens.di;

import com.tony.builder.nqueens.GameApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityBuilder.class
})
public interface AppComponent extends AndroidInjector<GameApplication> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<GameApplication>{}
}
