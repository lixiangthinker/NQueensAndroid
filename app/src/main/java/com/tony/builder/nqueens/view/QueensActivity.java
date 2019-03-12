package com.tony.builder.nqueens.view;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.tony.builder.nqueens.R;
import com.tony.builder.nqueens.utils.PixlConverter;
import com.tony.builder.nqueens.viewmodel.ChessBoardConstant;
import com.tony.builder.nqueens.viewmodel.ChessMoveEvent;
import com.tony.builder.nqueens.viewmodel.QueensViewModel;

import javax.inject.Inject;

public class QueensActivity extends DaggerAppCompatActivity {
    private static final String TAG = "QueensActivity";
    private static final int BOARD_DIMENSION = 8;

    ConstraintLayout chessBoard;
    ImageView[] ivQueenCheeses;
    ImageView ivCurrentLayer;
    ImageButton btnPlay;
    ImageButton btnNext;
    Button btnStart;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    QueensViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_queens);
        chessBoard = findViewById(R.id.clChessBoard);
        initChessImageView();
        ivCurrentLayer = findViewById(R.id.ivCurrentLayer);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(QueensViewModel.class);
        subscribeEvent(viewModel);
        initButtons();
    }

    private void subscribeEvent(QueensViewModel viewModel) {
        viewModel.getIsStarted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isStarted) {
                if (isStarted) {
                    btnPlay.setEnabled(true);
                    btnNext.setEnabled(true);
                    resetChess();
                    resetLayerArrow();
                } else {
                    btnPlay.setEnabled(false);
                    btnNext.setEnabled(false);
                }
            }
        });

        viewModel.getCurrentLayer().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentLayer) {
                Log.d(TAG, "current layer = " + currentLayer);
                moveArrow(currentLayer);
            }
        });

        viewModel.getChessMoveEvent().observe(this, new Observer<ChessMoveEvent>() {
            @Override
            public void onChanged(ChessMoveEvent event) {
                moveChess(event.currentLayer, event.newX, event.oldX);
            }
        });
    }

    private void resetLayerArrow() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivCurrentLayer.getLayoutParams();
        int marginTop = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.columnStart[0], this);
        int marginStart = (int) PixlConverter.convertDpToPixel(0, this);
        layoutParams.topMargin = marginTop;
        layoutParams.leftMargin = marginStart;
        ivCurrentLayer.setLayoutParams(layoutParams);
    }

    private void resetChess() {
        for (int index = 0; index < ivQueenCheeses.length; index++) {
            ImageView chess = ivQueenCheeses[index];
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chess.getLayoutParams();
            int marginTopPixel = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.columnStart[index], this);
            int marginLeftPixel = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.leftMargin, this);
            layoutParams.topMargin = marginTopPixel;
            layoutParams.leftMargin = marginLeftPixel;
            chess.setLayoutParams(layoutParams);
        }
    }

    private void moveChess(int currentLayer, int newX, int oldX) {
        ImageView chess = ivQueenCheeses[currentLayer];
        if (chess == null) {
            Log.e(TAG, "could not get chess layer = " + currentLayer);
            return;
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chess.getLayoutParams();
        int currentMargin = layoutParams.getMarginStart();
        int offset = (int) PixlConverter.convertDpToPixel((newX - oldX) * 39, this);
        Log.d(TAG, "currentMargin = " + currentMargin + " offset = " + offset + " old = " + oldX + " new = " + newX);
        layoutParams.setMarginStart(currentMargin + offset);
        chess.setLayoutParams(layoutParams);
    }

    private void moveArrow(Integer currentLayer) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivCurrentLayer.getLayoutParams();
        int offset = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.columnStart[currentLayer], this);
        Log.d(TAG, "moveArrow, offset = " + offset);
        layoutParams.setMargins(layoutParams.leftMargin, offset, 0, 0);
    }

    private void initButtons() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setEnabled(false);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button next onClick");
                if (viewModel != null) {
                    viewModel.onNext();
                }
            }
        });
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button start onClick");
                if (viewModel != null) {
                    viewModel.onStart(8);
                }
            }
        });
    }

    private void initChessImageView() {
        int[] chessIds = getChessId();
        ivQueenCheeses = new ImageView[BOARD_DIMENSION];
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            ivQueenCheeses[i] = findViewById(chessIds[i]);
        }
    }

    private int[] getChessId() {
        int[] result = new int[BOARD_DIMENSION];
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            String strChessID = "ivChessQueen" + i;
            result[i] = getId(strChessID);
        }
        return result;
    }

    private int getId(String idName) {
        Resources resources = getResources();
        return resources.getIdentifier(idName, "id", getPackageName());
    }
}
