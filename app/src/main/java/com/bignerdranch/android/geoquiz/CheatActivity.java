package com.bignerdranch.android.geoquiz;

import android.content.Intent;
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
    private Button mShowAnswerButton;
    private Button mCancelButton;
    private TextView mCheatAnswerTextView;

    private void setAnswerShownResult(boolean pAnswerShown) {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(EXTRA_USER_CHEATED, pAnswerShown);
        setResult(RESULT_OK, returnIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mShowAnswerButton = (Button) findViewById(R.id.cheatShowAnswerButton);
        mCheatAnswerTextView = (TextView) findViewById(R.id.cheatAnswerTextView);
        mCancelButton = (Button) findViewById(R.id.cheatCancelButton);

        mShowAnswerButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    boolean answerIsTrue = CheatActivity.this.getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, true);
                    mCheatAnswerTextView.setText("The answer is: " + answerIsTrue);
                    setAnswerShownResult(true);
                }
            }
        );

        mCancelButton.setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View pView) {
                    setAnswerShownResult(false);
                    CheatActivity.this.finish();
                }
            }
        );

    }

}
