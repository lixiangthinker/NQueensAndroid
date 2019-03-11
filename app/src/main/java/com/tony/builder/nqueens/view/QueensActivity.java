package com.tony.builder.nqueens.view;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import com.tony.builder.nqueens.viewmodel.ChessMoveEvent;
import com.tony.builder.nqueens.viewmodel.QueensViewModel;

import javax.inject.Inject;

public class QueensActivity extends DaggerAppCompatActivity {
    private static final String TAG = "QueensActivity";
    private static final int BOARD_DIMENSION = 8;

    ConstraintLayout chessBoard;
    ImageView[] ivQueenCheeses;
    ImageButton btnPlay;
    ImageButton btnNext;
    Button btnStart;

//    @Inject
//    ViewModelProvider.Factory viewModelFactory;
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
        viewModel = ViewModelProviders.of(this).get(QueensViewModel.class);
        subscribeEvent(viewModel);
        initButtons();
    }

    private void subscribeEvent(QueensViewModel viewModel) {
        viewModel.getCurrentLayer().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentLayer) {
                Log.d(TAG, "current layer = " + currentLayer);
            }
        });

        viewModel.getChessMoveEvent().observe(this, new Observer<ChessMoveEvent>() {
            @Override
            public void onChanged(ChessMoveEvent event) {
                moveChess(event.currentLayer, event.newX, event.oldX);
            }
        });
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

    private void initButtons() {
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
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
