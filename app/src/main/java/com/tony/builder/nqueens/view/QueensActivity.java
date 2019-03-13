package com.tony.builder.nqueens.view;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tony.builder.nqueens.R;
import com.tony.builder.nqueens.utils.PixlConverter;
import com.tony.builder.nqueens.viewmodel.ChessBoardConstant;
import com.tony.builder.nqueens.viewmodel.ChessMoveEvent;
import com.tony.builder.nqueens.viewmodel.QueensViewModel;
import com.tony.builder.nqueens.viewmodel.SolutionEvent;

import java.io.IOException;

import javax.inject.Inject;

public class QueensActivity extends DaggerAppCompatActivity {
    private static final String TAG = "QueensActivity";
    private static final int BOARD_DIMENSION = 8;

    ConstraintLayout chessBoard;
    ImageView[] ivQueenCheeses;
    ImageView ivCurrentLayer;
    ImageButton btnPlayOrPause;
    ImageButton btnNext;
    ImageButton btnReset;
    TextView tvStepCounter;
    TextView tvSolutionCounts;
    Context mContext;

    PlayPauseButtonOnClickListener listener;

    SoundPool mSoundPool = null;
    private int streamIdStart = -1;
    private int streamIdChessMove = -1;

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
        mContext = this;
        chessBoard = findViewById(R.id.clChessBoard);
        initChessImageView();
        ivCurrentLayer = findViewById(R.id.ivCurrentLayer);
        tvStepCounter = findViewById(R.id.tvStepCounter);
        tvSolutionCounts = findViewById(R.id.tvCounts);
        tvSolutionCounts.setText(getString(R.string.solution_counts, 0));
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(QueensViewModel.class);
        subscribeEvent(viewModel);
        listener = new PlayPauseButtonOnClickListener();
        initButtons(listener);
        initSoundEffects();
    }

    private void initSoundEffects() {
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .build();
        try {
            streamIdStart = mSoundPool.load(getApplicationContext().getAssets().openFd("start.mp3"), 1);
            streamIdChessMove =  mSoundPool.load(getApplicationContext().getAssets().openFd("chess_move.mp3"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeEvent(QueensViewModel viewModel) {
        viewModel.getIsStarted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isStarted) {
                if (isStarted) {
                    btnNext.setEnabled(true);
                    resetChess();
                    resetLayerArrow();
                } else {
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
                moveChess(event.currentLayer, event.newX);
                mSoundPool.play(streamIdChessMove, 10, 10, 1, 0, 1.0f);
            }
        });

        viewModel.getStepCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                tvStepCounter.setText(getString(R.string.current_step, integer));
            }
        });

        viewModel.getSolutionEvent().observe(this, new Observer<SolutionEvent>() {
            @Override
            public void onChanged(SolutionEvent solutionEvent) {
                Log.d(TAG, "getSolutionEvent " + solutionEvent.solutionCount);
                tvSolutionCounts.setText(getString(R.string.solution_counts, solutionEvent.solutionCount));
                resetPlayButton();
            }
        });
    }

    private void resetPlayButton() {
        listener.setPlayOrPause(PlayPauseButtonOnClickListener.PLAY_VALUE);
        btnPlayOrPause.setImageDrawable(mContext.getDrawable(R.drawable.ic_play_arrow_black_24dp));
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
            chess.setVisibility(View.INVISIBLE);
        }
    }

    private void moveChess(int currentLayer, int newX) {
        ImageView chess = ivQueenCheeses[currentLayer];
        if (chess == null) {
            Log.e(TAG, "could not get chess layer = " + currentLayer);
            return;
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chess.getLayoutParams();
        // back track, reset chess
        if (newX != -1 && newX != 0) {
            int marginStartPixel = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.rowStart[newX], this);
            int marginTopPixel = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.columnStart[currentLayer], this);
            layoutParams.leftMargin = marginStartPixel;
            layoutParams.topMargin = marginTopPixel;
            chess.setLayoutParams(layoutParams);
        } else if (newX == 0) {
            chess.setVisibility(View.VISIBLE);
            int marginStartPixel = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.rowStart[newX], this);
            int marginTopPixel = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.columnStart[currentLayer], this);
            layoutParams.leftMargin = marginStartPixel;
            layoutParams.topMargin = marginTopPixel;
            chess.setLayoutParams(layoutParams);
        } else {
            chess.setVisibility(View.INVISIBLE);
        }
    }

    private void moveArrow(Integer currentLayer) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivCurrentLayer.getLayoutParams();
        int offset = (int) PixlConverter.convertDpToPixel(ChessBoardConstant.columnStart[currentLayer], this);
        Log.d(TAG, "moveArrow, offset = " + offset);
        layoutParams.setMargins(layoutParams.leftMargin, offset, 0, 0);
    }

    class PlayPauseButtonOnClickListener implements View.OnClickListener {
        final static int PLAY_VALUE = 1;
        final static int PAUSE_VALUE = 2;
        private int playOrPause = PLAY_VALUE;

        public void setPlayOrPause(int value) {
            playOrPause = value;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "button play onClick");
            if (playOrPause == PLAY_VALUE) {
                Boolean isStarted;
                if (viewModel != null) {
                    isStarted = viewModel.getIsStarted().getValue();
                    if (isStarted != null && !isStarted) {
                        viewModel.onStart(BOARD_DIMENSION);
                    }
                    viewModel.onPlay();
                }
                playOrPause = PAUSE_VALUE;
                btnPlayOrPause.setImageDrawable(mContext.getDrawable(R.drawable.ic_pause_black_24dp));
                mSoundPool.play(streamIdStart, 10, 10, 1, 0, 1.0f);
            } else {
                if (viewModel != null) {
                    viewModel.onPause();
                }
                playOrPause = PLAY_VALUE;
                btnPlayOrPause.setImageDrawable(mContext.getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
        }
    }

    private void initButtons(PlayPauseButtonOnClickListener listener) {
        btnPlayOrPause = findViewById(R.id.btnPlay);
        btnPlayOrPause.setOnClickListener(listener);

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

        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button reset onClick");
                if (viewModel != null) {
                    viewModel.onReset(BOARD_DIMENSION);
                }
                tvStepCounter.setText(getString(R.string.current_step, 0));
                tvSolutionCounts.setText(getString(R.string.solution_counts, 0));
                resetChess();
                resetLayerArrow();
                resetPlayButton();
                btnNext.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }
}
