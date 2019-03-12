package com.tony.builder.nqueens.model;

import javax.inject.Inject;

public class QueensSingleArray implements QueensModel{
    private int DIMENSION;
    private int[] position;
    private final int POSITION_NOT_SET = -1;

    @Inject
    public QueensSingleArray() {}

    private int solutionCount = 0;
    public int getSolutionCount(int currentLayer) {
        if (currentLayer == DIMENSION) {
            solutionCount++;
        } else {
            for (int x = 0; x < DIMENSION; x++) {
                position[currentLayer] = x;
                if (isAvailable(currentLayer)) {
                    getSolutionCount(currentLayer + 1);
                }
            }
        }
        return solutionCount;
    }

    public int getSolutionCountIter() {
        position[0] = POSITION_NOT_SET;
        int currentLayer = 0;

        while (currentLayer >= 0) {
            position[currentLayer]++;
            // find a valid position in current layer
            while (position[currentLayer] < DIMENSION && !isAvailable(currentLayer)) {
                position[currentLayer]++;
            }
            // successfully find a position in current layer
            if (position[currentLayer] < DIMENSION) {
                if (currentLayer == DIMENSION - 1) {
                    // get a solution.
                    solutionCount++;
                } else {
                    // continue to check next layer.
                    currentLayer++;
                }
            } else {
                // failed to find a position in current layer
                // move to upper layer.
                position[currentLayer] = POSITION_NOT_SET;
                currentLayer--;
            }
        }

        return solutionCount;
    }

    public boolean isAvailable(int y) {
        for (int i = 0; i < y; i++) {
            // delta y = delta x; not same column;
            if (Math.abs(y - i) == Math.abs(position[y] - position[i]) || position[y] == position[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void print() {
        for (int i = 0; i < position.length; i++) {
            System.out.print(position[i] + "\t");
        }
        System.out.println();
        System.out.println();

        int[][] chessBoardArrayTwoDimension = new int[DIMENSION][DIMENSION];
        for (int y = 0; y < DIMENSION; y++) {
            for (int x = 0; x < DIMENSION; x++) {
                chessBoardArrayTwoDimension[y][x] = 0;
            }
            if (position[y] != POSITION_NOT_SET) {
                chessBoardArrayTwoDimension[y][position[y]] = 1;
            }
        }

        for (int y = 0; y < DIMENSION; y++) {
            for (int x = 0; x < DIMENSION; x++) {
                System.out.print(chessBoardArrayTwoDimension[y][x] + "\t");
            }
            System.out.println();
        }
    }

    private EventListener listener;
    @Override
    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }
    private enum IterState{
        INIT,
        QUEEN_MOVED,
        LAYER_MOVED,
        FINISHED,
    }
    private IterState state = IterState.INIT;
    private int mCurrentLayer = 0;

    @Override
    public void onStart(int dimension) {
        DIMENSION = dimension;
        position = new int[dimension];
        for (int i = 0; i < DIMENSION; i++) {
            position[i] = POSITION_NOT_SET;
        }
        state = IterState.QUEEN_MOVED;
        position[mCurrentLayer] = POSITION_NOT_SET;
        solutionCount = 0;
        notifyStarted();
    }

    @Override
    public void onNext() {
        switch (state) {
            case INIT:
                state = IterState.QUEEN_MOVED;
                break;
            case QUEEN_MOVED:
                if (mCurrentLayer >= 0) {
                    position[mCurrentLayer]++;

                    if (position[mCurrentLayer] < DIMENSION && !isAvailable(mCurrentLayer)){
                        // invalid position wait to click();
                        notifyQueenMoved(position[mCurrentLayer], mCurrentLayer);
                    } else {
                        //state = IterState.LAYER_MOVED;
                        if (position[mCurrentLayer] < DIMENSION) {
                            notifyQueenMoved(position[mCurrentLayer], mCurrentLayer);
                            if (mCurrentLayer == DIMENSION - 1) {
                                // get a solution.
                                solutionCount++;
                                notifySolution(solutionCount, position);
                            } else {
                                // continue to check next layer.
                                mCurrentLayer++;
                                notifyLayerMoved(mCurrentLayer-1, mCurrentLayer);
                            }
                        } else {
                            position[mCurrentLayer] = POSITION_NOT_SET;
                            notifyQueenMoved(position[mCurrentLayer], mCurrentLayer);
                            mCurrentLayer--;
                            notifyLayerMoved(mCurrentLayer+1, mCurrentLayer);
                        }
                    }
                } else {
                    state = IterState.FINISHED;
                }
                break;
            case FINISHED:
                notifyFinished(solutionCount);
                break;
            default:
                break;
        }
    }

    private void notifyQueenMoved(int x, int y) {
        if (listener != null) {
            listener.onMoveChess(x, y);
        }
    }

    private void notifyStarted() {
        if (listener != null) {
            listener.onStarted();
        }
    }

    private void notifySolution(int solutionCount, int[] position) {
        if (listener != null) {
            listener.onSolution(solutionCount, position);
        }
    }

    private void notifyLayerMoved(int from, int to) {
        if (listener != null) {
            listener.onMoveLayer(from, to);
        }
    }

    private void notifyFinished(int solutionCount) {
        if (listener != null) {
            listener.onFinished(solutionCount);
        }
    }
}
