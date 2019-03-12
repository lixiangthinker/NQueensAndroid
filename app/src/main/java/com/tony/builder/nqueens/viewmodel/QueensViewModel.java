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
    private MutableLiveData<ChessMoveEvent> mChessMoveEvent;
    private MutableLiveData<Boolean> isStarted;

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
            public void onStarted() {
                Log.d(TAG, "QueensModel.EventListener onStarted");
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isStarted != null) {
                            isStarted.setValue(true);
                        }
                    }
                });
            }

            @Override
            public void onMoveChess(final int oldX, final int newX) {
                Log.d(TAG, "onMoveChess, (" + oldX + "," + newX + ")");
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mChessMoveEvent != null) {
                            ChessMoveEvent event = new ChessMoveEvent();
                            event.oldX = oldX;
                            event.newX = newX;
                            event.currentLayer = mCurrentLayer.getValue();
                            mChessMoveEvent.setValue(event);
                        }
                    }
                });
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
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isStarted != null) {
                            isStarted.setValue(false);
                        }
                    }
                });
            }
        });
    }

    public void onStart(int dimension) {
        Log.d(TAG, "onStart start button clicked");
        queensModel.onStart(dimension);
    }

    public void onNext() {
        Log.d(TAG, "onNext next button clicked");
        queensModel.onNext();
    }

    public LiveData<Integer> getCurrentLayer() {
        if (mCurrentLayer == null) {
            mCurrentLayer = new MutableLiveData<>();
            mCurrentLayer.setValue(0);
        }
        return mCurrentLayer;
    }

    public LiveData<ChessMoveEvent> getChessMoveEvent() {
        if (mChessMoveEvent == null) {
            mChessMoveEvent = new MutableLiveData<>();
        }
        return mChessMoveEvent;
    }

    public LiveData<Boolean> getIsStarted() {
        if (isStarted == null) {
            isStarted = new MutableLiveData<>();
            isStarted.setValue(false);
        }
        return isStarted;
    }
}
