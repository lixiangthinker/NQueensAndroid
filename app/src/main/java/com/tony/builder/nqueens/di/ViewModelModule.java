package com.tony.builder.nqueens.di;

import com.tony.builder.nqueens.viewmodel.QueensViewModelFactory;
import com.tony.builder.nqueens.viewmodel.QueensViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract public class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(QueensViewModel.class)
    abstract ViewModel bindQueensViewModel(QueensViewModel queensViewmodel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(QueensViewModelFactory factory);
}
