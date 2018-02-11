package com.raywenderlich.camelot;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.FileUtils;

import java.io.File;

/**
 * Created by kanbudong on 1/26/18.
 */

public class AudioStepLayout extends RelativeLayout implements StepLayout
{
    public static final String KEY_AUDIO = "AudioStep.Audio";

    private StepCallbacks mStepCallbacks;
    private AudioStep mStep;
    private StepResult<String> mResult;
    private boolean mIsRecordingComplete = false;
    private String mFilename;

    public AudioStepLayout(Context context)
    {
        super(context);
    }

    public AudioStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AudioStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {

        this.mStep = (AudioStep)step;
        this.mResult = result == null ? new StepResult<>(step) : result;

        initializeStep();
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        setDataToResult();
        mStepCallbacks.onSaveStep(StepCallbacks.ACTION_PREV, mStep, mResult);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.mStepCallbacks = callbacks;
    }

    // 1
    private void setDataToResult()
    {
        mResult.setResultForIdentifier(KEY_AUDIO, getBase64EncodedAudio());
    }

    // 2
    private String getBase64EncodedAudio()
    {
        if(mIsRecordingComplete)
        {

            // 3
            File file = new File(mFilename);

            try {
                byte[] bytes = FileUtils.readAll(file);

                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
                return encoded;

            } catch (Exception e) {
                return null;
            }
        }
        else
        {
            return null;
        }
    }


    private void initializeStep()
    {
        LayoutInflater.from(getContext())
                .inflate(R.layout.audio_step_layout, this, true);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(mStep.getTitle());

        TextView text = (TextView) findViewById(R.id.summary);
        text.setText(mStep.getText());

        final TextView countdown = (TextView) findViewById(R.id.countdown);
        countdown.setText("Seconds remaining: " + Integer.toString(mStep.getDuration()));

        final TextView countdown_title = (TextView) findViewById(R.id.countdown_title);

        final Button beginButton = (Button) findViewById(R.id.begin_recording);


        SubmitBar submitBar = (SubmitBar)findViewById(R.id.submit_bar);
        submitBar.setPositiveTitle(org.researchstack.backbone.R.string.rsb_next);
        submitBar.getPositiveActionView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepCallbacks.onSaveStep(StepCallbacks.ACTION_NEXT, mStep, mResult);
            }
        });
        submitBar.getNegativeActionView().setVisibility(View.GONE);

        // TODO: set onClick listener

        // 1

        beginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 2
                mFilename = getContext().getFilesDir().getAbsolutePath();
                mFilename += "/camelotaudiorecord.3gp";

                final AudioRecorder audioRecorder = new AudioRecorder();
                audioRecorder.startRecording(mFilename);

                // 3
                beginButton.setVisibility(GONE);
                countdown_title.setVisibility(View.VISIBLE);

                // 4
                CountDownTimer Count = new CountDownTimer(mStep.getDuration()*1000, 1000) {

                    // 5
                    public void onTick(long millisUntilFinished) {
                        countdown.setText("Seconds remaining: " + millisUntilFinished / 1000);
                    }

                    // 6
                    public void onFinish() {

                        mIsRecordingComplete = true;

                        audioRecorder.stopRecording();

                        AudioStepLayout.this.setDataToResult();

                        mStepCallbacks.onSaveStep(StepCallbacks.ACTION_NEXT, mStep, mResult);
                    }
                };

                // 7
                Count.start();
            }
        });
    }
}