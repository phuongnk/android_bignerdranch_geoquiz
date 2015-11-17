package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_USER_CHEATED = "com.bignerdranch.android.geoquiz.user_cheated";
    //private static boolean staticUserCheated = false; // testing: a static variable, to see if this value survives screen rotations
    private boolean mUserCheated = false;
    private Button mShowAnswerButton;
    private Button mCancelButton;
    private TextView mCheatAnswerTextView;
    private TextView mUserCheatedTextView;
    private TextView mAPILevelTextView;

    private void setAnswerShownResult(boolean pAnswerShown) {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(EXTRA_USER_CHEATED, pAnswerShown);
        setResult(RESULT_OK, returnIntent);
    }

    private void showIfUserCheated() {
        //if (staticUserCheated) {
        if (mUserCheated) {
            mUserCheatedTextView.setText("You cheated");
        } else {
            mUserCheatedTextView.setText("You have not cheated");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mShowAnswerButton = (Button) findViewById(R.id.cheatShowAnswerButton);
        mCheatAnswerTextView = (TextView) findViewById(R.id.cheatAnswerTextView);
        mUserCheatedTextView = (TextView) findViewById(R.id.userCheatedTextView);
        showIfUserCheated();
        mCancelButton = (Button) findViewById(R.id.cheatCancelButton);
        mAPILevelTextView = (TextView) findViewById(R.id.cheat_android_api_level_textview);
        mAPILevelTextView.setText("API Level " + Build.VERSION.SDK_INT);
        if (savedInstanceState != null) {
            mUserCheated = savedInstanceState.getBoolean(EXTRA_USER_CHEATED, false);
        }
        showIfUserCheated();
        setAnswerShownResult(mUserCheated);

        mShowAnswerButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    boolean answerIsTrue = CheatActivity.this.getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, true);
                    mCheatAnswerTextView.setText("The answer is: " + answerIsTrue);
                    //staticUserCheated = true;
                    mUserCheated = true;
                    showIfUserCheated();
                    setAnswerShownResult(mUserCheated);
                }
            }
        );

        mCancelButton.setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View pView) {
                    setAnswerShownResult(mUserCheated);
                    CheatActivity.this.finish();
                }
            }
        );

    }  // onCreate() ---------------

    @Override
    public void onSaveInstanceState(Bundle pBundleToSave) {
        super.onSaveInstanceState(pBundleToSave);
        pBundleToSave.putBoolean(EXTRA_USER_CHEATED, mUserCheated);
    }

}
