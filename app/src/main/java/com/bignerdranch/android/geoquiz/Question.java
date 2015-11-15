package com.bignerdranch.android.geoquiz;

/**
 * Created by Owner on 11/1/2015.
 */
//package com.bignerdranch.android.geoquiz;

public class Question {
    private int mQuestionStringResourceId;
    private boolean mAnswer;

    public Question(int pTextResourceId, boolean pAnswer) {
        mQuestionStringResourceId = pTextResourceId;
        mAnswer = pAnswer;
    }

    public int getQuestionStringResourceId() {
        return mQuestionStringResourceId;
    }

    public void setQuestionStringResourceId(int pQuestionStringResourceId) {
        mQuestionStringResourceId = pQuestionStringResourceId;
    }

    public boolean isAnswerTrue() {
        return mAnswer;
    }

    public void setAnswer(boolean pAnswer) {
        mAnswer = pAnswer;
    }
}
