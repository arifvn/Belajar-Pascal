package com.lingkarkode.belajarpascal;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQestionList";


    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private Button buttonConfirmNext;
    private TextView textViewCorrectInfo;
    private TextView textViewCategory;

    private ColorStateList textColorDefaultRadioButton;
    private ColorStateList textColorDefaultCountDown;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;
    private static Toast toastMessage = null;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        changeStatusBarColor();

        textViewQuestion = (TextView) findViewById(R.id.textview_questions);
        textViewScore = (TextView) findViewById(R.id.textview_score);
        textViewQuestionCount = (TextView) findViewById(R.id.textview_questions_count);
        textViewCountDown = (TextView) findViewById(R.id.textview_countdown);
        rbGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioButton1 = (RadioButton) findViewById(R.id.radiobutton_1);
        radioButton2 = (RadioButton) findViewById(R.id.radiobutton_2);
        radioButton3 = (RadioButton) findViewById(R.id.radiobutton_3);
        radioButton4 = (RadioButton) findViewById(R.id.radiobutton_4);
        buttonConfirmNext = (Button) findViewById(R.id.button_confirm_next);
        textViewCorrectInfo = (TextView) findViewById(R.id.textview_correct_info);
        textViewCategory = (TextView) findViewById(R.id.textview_category);

        //Ambil warna default radio button (hitam)
        textColorDefaultRadioButton = radioButton1.getTextColors();
        textColorDefaultCountDown = textViewCountDown.getTextColors();

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra(StartingQuizActivity.EXTRA_CATEGORY_ID, 0);
        String categoryName = intent.getStringExtra(StartingQuizActivity.EXTRA_CATEGORY_NAME);

        textViewCategory.setText(categoryName);

        if (savedInstanceState == null) {
            //Panggil data dari database
            QuizDbHelper quizDbHelper = QuizDbHelper.getInstance(this);

            questionList = quizDbHelper.getQuestions(categoryId);

            //jumlah object question yang di retrieve
            questionCountTotal = questionList.size();

            Collections.shuffle(questionList);

            showNextQuestion();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            if (questionList == null) {
                finish();
            }
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            score = savedInstanceState.getInt(KEY_SCORE);
            currentQuestion = questionList.get(questionCounter - 1);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);

            if (!answered) {
                startCountDown();
            } else {
                updateCountDownTextView();
                showSolution();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (radioButton1.isChecked() || radioButton2.isChecked()
                            || radioButton3.isChecked() || radioButton4.isChecked()) {
                        checkAnswer();
                    } else {
                        //Mencegah toast message tumpuk
                        if (toastMessage != null) {
                            toastMessage.cancel();
                        }
                        toastMessage = Toast.makeText(QuizActivity.this, "Pilih salah satu jawaban!", Toast.LENGTH_SHORT);
                        toastMessage.show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }

    private void checkAnswer() {
        answered = true;

        countDownTimer.cancel();

        RadioButton radioButtonSelected = (RadioButton) findViewById(rbGroup.getCheckedRadioButtonId());
        int answeredQuestion = rbGroup.indexOfChild(radioButtonSelected) + 1;

        if (answeredQuestion == currentQuestion.getAnswerNumber()) {
            score++;
            textViewScore.setText("Skor: " + score);
        }

        showSolution();
    }

    private void showSolution() {
        radioButton1.setTextColor(getResources().getColor(R.color.red));
        radioButton2.setTextColor(getResources().getColor(R.color.red));
        radioButton3.setTextColor(getResources().getColor(R.color.red));
        radioButton4.setTextColor(getResources().getColor(R.color.red));

        switch (currentQuestion.getAnswerNumber()) {
            case 1:
                radioButton1.setTextColor(getResources().getColor(R.color.colorPrimary));
                textViewCorrectInfo.setVisibility(View.VISIBLE);
                textViewCorrectInfo.setText("Jawaban Tepat adalah A");
                break;
            case 2:
                radioButton2.setTextColor(getResources().getColor(R.color.colorPrimary));
                textViewCorrectInfo.setVisibility(View.VISIBLE);
                textViewCorrectInfo.setText("Jawaban Tepat adalah B");
                break;
            case 3:
                radioButton3.setTextColor(getResources().getColor(R.color.colorPrimary));
                textViewCorrectInfo.setVisibility(View.VISIBLE);
                textViewCorrectInfo.setText("Jawaban Tepat adalah C");
                break;
            case 4:
                radioButton4.setTextColor(getResources().getColor(R.color.colorPrimary));
                textViewCorrectInfo.setVisibility(View.VISIBLE);
                textViewCorrectInfo.setText("Jawaban Tepat adalah D");
                break;
        }

        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Lanjut");
        } else {
            buttonConfirmNext.setText("Selesai");
        }
    }

    private void showNextQuestion() {
        textViewCorrectInfo.setVisibility(View.GONE);
        radioButton1.setTextColor(textColorDefaultRadioButton);
        radioButton2.setTextColor(textColorDefaultRadioButton);
        radioButton3.setTextColor(textColorDefaultRadioButton);
        radioButton4.setTextColor(textColorDefaultRadioButton);
        rbGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            radioButton1.setText(currentQuestion.getOption1());
            radioButton2.setText(currentQuestion.getOption2());
            radioButton3.setText(currentQuestion.getOption3());
            radioButton4.setText(currentQuestion.getOption4());

            //Karena dimulai dari 0 counter langsung menjadi 1
            questionCounter++;
            textViewQuestionCount.setText("Soal: " + questionCounter + "/" + questionCountTotal);

            answered = false;
            buttonConfirmNext.setText("Jawab");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownTextView();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownTextView();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownTextView() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewCountDown.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
            textViewCountDown.setTextColor(getResources().getColor(R.color.red));
        } else {
            textViewCountDown.setTextColor(textColorDefaultCountDown);
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            //Mencegah toast message tumpuk
            if (toastMessage != null) {
                toastMessage.cancel();
            }
            toastMessage = Toast.makeText(QuizActivity.this, "Tekan tombol kembali lagi untuk selesai", Toast.LENGTH_SHORT);
            toastMessage.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }

    private void changeStatusBarColor() {
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }
}
