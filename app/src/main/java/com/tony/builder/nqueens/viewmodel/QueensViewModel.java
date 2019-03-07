package com.tony.builder.nqueens.viewmodel;

import android.content.SharedPreferences;
import android.util.Log;

import com.tony.builder.nqueens.model.QueensModel;
import com.tony.builder.nqueens.utils.AppExecutors;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QueensViewModel extends ViewModel {
    private static final String TAG = "QueensViewModel";
    private MutableLiveData<Integer> mCurrentLayer;
    private MutableLiveData<Integer> mCurrentXPosition;

    private AppExecutors executors;
    private SharedPreferences sharedPreferences;
    private QueensModel queensModel;

    @Inject
    public QueensViewModel(AppExecutors executors, QueensModel queensModel, SharedPreferences sharedPreferences) {
        this.executors = executors;
        this.sharedPreferences = sharedPreferences;
        setQueensModel(queensModel);
    }

    private void setQueensModel(QueensModel queensModel) {
        this.queensModel = queensModel;
        queensModel.setEventListener(new QueensModel.EventListener() {

            @Override
            public void onMoveChess(int oldX, int newX) {
                Log.d(TAG, "onMoveChess, (" + oldX + "," + newX + ")");
            }

            @Override
            public void onMoveLayer(int oldLayer, final int newLayer) {
                Log.d(TAG, "onMoveLayer, (" + oldLayer + "," + newLayer + ")");
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentLayer != null) {
                            mCurrentLayer.setValue(newLayer);
                        }
                    }
                });
            }

            @Override
            public void onSolution(int count, int[] position) {
                Log.d(TAG, "onSolution, (" + count + ")");
            }

            @Override
            public void onFinished(int solutionCount) {
                Log.d(TAG, "onFinished, (" + solutionCount + ")");
            }
        });
    }

    public void onStart(int dimension) {
        Log.d(TAG, "onStart start button clicked");
        queensModel.onStart(dimension);
    }

    public void onNext() {
        Log.d(TAG, "onStart start button clicked");
        queensModel.onNext();
    }

    public LiveData<Integer> getCurrentLayer() {
        if (mCurrentLayer == null) {
            mCurrentLayer = new MutableLiveData<>();
        }
        return mCurrentLayer;
    }

    public LiveData<Integer> getCurrentXPosition() {
        if (mCurrentXPosition == null) {
            mCurrentXPosition = new MutableLiveData<>();
        }
        return mCurrentXPosition;
    }
}