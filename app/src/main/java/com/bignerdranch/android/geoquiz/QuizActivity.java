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
    // inner class to store one question and user's answer to that question
    // including whether the user cheated
    private class QuestionAndAnswer {
        private Question mQuestion;
        //private boolean mUserLastAnswer; // what was user's last answer to this question (true or false)
        private boolean mUserCheated;

        QuestionAndAnswer(Question pQuestion) {
            this.mQuestion = pQuestion;
            this.mUserCheated = false;
        }

        public Question getQuestion() {
            return mQuestion;
        }

        public boolean isUserCheated() {
            return mUserCheated;
        }

        public void setUserCheated(boolean pUserCheated) {
            mUserCheated = pUserCheated;
        }
    }
    private static final String EXTRA_USER_CHEATED = "com.bignerdranch.android.geoquiz.user_cheated";
    private static final String STATE_BUNDLE_KEY_CURRENT_QUESTION = "STATE_BUNDLE_KEY_CURRENT_QUESTION";
    private static final String STATE_BUNDLE_KEY_USER_CHEATING_STAT = "STATE_BUNDLE_KEY_USER_CHEATING_STAT";
    private static final int REQUEST_CODE_CHEAT = 123;
    private static final String TAG = "QuizActivity";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private Toast mButtonToast;
    private TextView mQuestionTextView;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private QuestionAndAnswer[] mQABank = new QuestionAndAnswer[]{
                new QuestionAndAnswer(new Question(R.string.question_oceans, true)),
                new QuestionAndAnswer(new Question(R.string.question_mideast, true)),
                new QuestionAndAnswer(new Question(R.string.question_africa, true)),
                new QuestionAndAnswer(new Question(R.string.question_americas, true)),
                new QuestionAndAnswer(new Question(R.string.question_asia, true)),
                new QuestionAndAnswer(new Question(R.string.question_ann, true))
            };
    private int mCurrentIndex = 0;
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";

    // update the "Cheat!" button label, depending on whether user has cheated on this same question before
    private void updateCheatButtonLabel() {
        if (mQABank[mCurrentIndex].isUserCheated()) {
            mCheatButton.setText(R.string.launch_cheat_activity_button_label_again);
        }
        else {
            mCheatButton.setText(R.string.launch_cheat_activity_button_label);
        }
    }

    private void updateQuestion() {
        //Log.d(TAG, "Updating TextView for question #" + mCurrentIndex, new Exception());
        int questionStringResourceId = mQABank[mCurrentIndex].getQuestion().getQuestionStringResourceId();
        mQuestionTextView.setText(questionStringResourceId);
        updateCheatButtonLabel();
    }

    private void checkAnswer(boolean mButtonPressed) {
        int msgResourceId = 0;
        if (mQABank[mCurrentIndex].getQuestion().isAnswerTrue() == mButtonPressed) {
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
            mCurrentIndex = mQABank.length - 1;
        }

        mCurrentIndex = mCurrentIndex % mQABank.length;
        updateQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        // Restoring the activity state from previously saved Bundle ----------
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(STATE_BUNDLE_KEY_CURRENT_QUESTION, 0);
            boolean[] userCheatingStat = savedInstanceState.getBooleanArray(STATE_BUNDLE_KEY_USER_CHEATING_STAT);
            if (userCheatingStat != null) {
                for (int i = 0; i < userCheatingStat.length; i++) {
                    mQABank[i].setUserCheated(userCheatingStat[i]);
                }
            }
        }
        Log.d(TAG, "Restored from Bundle: key: '" + STATE_BUNDLE_KEY_CURRENT_QUESTION + "', value: " + mCurrentIndex);

        //
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);

        updateCheatButtonLabel();

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
                Intent intent = newIntent(QuizActivity.this, mQABank[mCurrentIndex].getQuestion().isAnswerTrue());
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
        Log.d(TAG, "QuizActivity.onActivityResult() starting...");

        boolean userCheated = false;

        Log.d(TAG, "QuizActivity.onActivityResult(): pRequestCode = \"" + pRequestCode +"\"");

        Log.d(TAG, "QuizActivity.onActivityResult(): pRequestCode == REQUEST_CODE_CHEAT?" + (pRequestCode == REQUEST_CODE_CHEAT));
        if (pRequestCode == REQUEST_CODE_CHEAT) {
            if (pResultCode == RESULT_OK) {
                Log.d(TAG, "QuizActivity.onActivityResult(): pResultCode == RESULT_OK");
            } else if (pResultCode == RESULT_CANCELED) {
                Log.d(TAG, "QuizActivity.onActivityResult(): pResultCode == RESULT_CANCELED");
            }
            if (resultIntent != null) {
                userCheated = resultIntent.getBooleanExtra(EXTRA_USER_CHEATED, false);
            } else {
                Log.d(TAG, "QuizActivity.onActivityResult(): resultIntent is null!");
            }
            Log.d(TAG, "QuizActivity.onActivityResult(): userCheated = " + userCheated);
            // if user had not cheated on this question before launching the cheat sreen,
            // then set the cheating indicator to whatever result the cheat screen returned
            // But if user already cheated on this question before, then we'll keep the indicator as "true" (cheated)
            if (!mQABank[mCurrentIndex].isUserCheated()) {
                mQABank[mCurrentIndex].setUserCheated(userCheated);
            }

            if (userCheated) {
                Toast.makeText(this, R.string.cheat_judgement_toast_msg, Toast.LENGTH_SHORT).show();
            } else {
                if (mQABank[mCurrentIndex].isUserCheated()) {
                    Toast.makeText(this, "You didn't cheat this time, but you cheated on this question before!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, R.string.cheat_appreciation_toast_msg, Toast.LENGTH_SHORT).show();
                }
            }
            updateCheatButtonLabel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundleToBeSaved) {
        //Call the superclass method. It should save the layout hierarchy’s view states
        super.onSaveInstanceState(bundleToBeSaved);
        //Save additional app-specific information: current question’s index
        //Needs to have a pair of key-value. The key must be String.
        Log.d(TAG, "Saving to Bundle: key: '" + STATE_BUNDLE_KEY_CURRENT_QUESTION + "', value: " + mCurrentIndex);
        bundleToBeSaved.putInt(STATE_BUNDLE_KEY_CURRENT_QUESTION, mCurrentIndex);

        // Saving the boolean array about whether user cheated for each question
        boolean[] userCheatingStatus = new boolean[mQABank.length];
        for (int i=0; i< mQABank.length; i++) {
            userCheatingStatus[i] = mQABank[i].isUserCheated();
        }
        bundleToBeSaved.putBooleanArray(STATE_BUNDLE_KEY_USER_CHEATING_STAT, userCheatingStatus);
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
