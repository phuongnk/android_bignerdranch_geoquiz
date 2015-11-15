package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity {
    private static final String CURRENT_QUESTION_BUNDLE_KEY = "what question am i at";
    private final String TAG = "QuizActivity";
    private static final int REQUEST_CODE_CHEAT = 12345;
    private static final String EXTRA_USER_CHEATED = "com.bignerdranch.android.geoquiz.user_cheated";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private Toast mButtonToast;
    private TextView mQuestionTextView;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private Question[] mQuestionBank = new Question[]{
                new Question(R.string.question_oceans, true),
                new Question(R.string.question_mideast, false),
                new Question(R.string.question_africa, false),
                new Question(R.string.question_americas, true),
                new Question(R.string.question_asia, true),
                new Question(R.string.question_ann, true)
            };
    private int mCurrentIndex = 0;
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";

    private void updateQuestion() {
        //Log.d(TAG, "Updating TextView for question #" + mCurrentIndex, new Exception());
        int questionTextResourceId = mQuestionBank[mCurrentIndex].getQuestionStringResourceId();
        mQuestionTextView.setText(questionTextResourceId);
    }

    private void checkAnswer(boolean mButtonPressed) {
        int msgResourceId = 0;
        if (mQuestionBank[mCurrentIndex].isAnswerTrue() == mButtonPressed) {
            msgResourceId = R.string.correct_toast_msg;
        } else {
            msgResourceId = R.string.incorrect_toast_msg;
        }

        Toast.makeText(this, msgResourceId, Toast.LENGTH_SHORT).show();
    }

    private void changeQuestion(int pOffset) {
        mCurrentIndex += pOffset;
        if (mCurrentIndex < 0) {
            // if moved to an index that is < 0
            // (for example, clicking "Previous" when at the first question)
            // then move to the last question instead
            mCurrentIndex = mQuestionBank.length - 1;
        }

        mCurrentIndex = mCurrentIndex % mQuestionBank.length;
        updateQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(CURRENT_QUESTION_BUNDLE_KEY);
        }
        Log.d(TAG, "Restored from Bundle: key: '" + CURRENT_QUESTION_BUNDLE_KEY + "', value: " + mCurrentIndex);
        setContentView(R.layout.activity_quiz);

        //
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);


        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch the CheatActivity
                // note: we are inside an anonymous inner class so we have to use QuizActivity.this
                // instead of just "this"
                Intent intent = newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue());
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuestion(-1); // show previous question
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuestion(1); // show next question
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuestion(1); // show next question
            }
        });

        updateQuestion();
    }

    // catching the result from a child activity started by this activity (via startActivityForResult())
    @Override
    protected void onActivityResult(int pRequestCode, int pResultCode, Intent resultIntent) {
        boolean userCheated = false;

        if (pRequestCode == REQUEST_CODE_CHEAT) {
            if (pResultCode == RESULT_OK) {
                userCheated = resultIntent.getBooleanExtra(EXTRA_USER_CHEATED, false);
            }
            if (userCheated) {
                Toast.makeText(this, R.string.cheat_judgement_toast_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.cheat_appreciation_toast_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundleToBeSaved) {
        //Call the superclass method. It should save the layout hierarchy’s view states
        super.onSaveInstanceState(bundleToBeSaved);
        //Save additional app-specific information: current question’s index
        //Needs to have a pair of key-value. The key must be String.
        Log.d(TAG, "Saving to Bundle: key: '" + CURRENT_QUESTION_BUNDLE_KEY + "', value: " + mCurrentIndex);
        bundleToBeSaved.putInt(CURRENT_QUESTION_BUNDLE_KEY, mCurrentIndex);
    }

    // Create a new Intent obj to start CheatActivity, that contains an extra about whether the
    // current question's answer is true
    public static Intent newIntent(Context pPackageContext, boolean pAnswerIsTrue) {
        Intent intent = new Intent(pPackageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, pAnswerIsTrue);
        return intent;
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called.");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
