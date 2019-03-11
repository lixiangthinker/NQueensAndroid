package com.tony.builder.nqueens.model;

public interface QueensModel {
    interface EventListener {
        void onStarted();
        void onMoveChess(int x, int y);
        void onMoveLayer(int oldLayer, int newLayer);
        void onSolution(int count, int[] position);
        void onFinished(int solutionCount);
    }
    void setEventListener(EventListener listener);
    void onStart(int dimension);
    void onNext();

    void print();
}
