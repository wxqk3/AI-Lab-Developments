package com.raywenderlich.camelot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.FileUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import static com.raywenderlich.camelot.R.*;

/**
 * Created by kanbudong on 1/27/18.
 */

public class ImageQuestionStepLayout extends RelativeLayout implements StepLayout {

    public static final String KEY_AUDIO = "AudioStep.Audio";

    private StepCallbacks mStepCallbacks;
    private ImageQuestionStep mStep;
    private StepResult<String> mResult;
    private boolean mIsRecordingComplete = false;
    private String mFilename;
    private File imageFile;

    public ImageQuestionStepLayout(Context context)
    {
        super(context);
    }

    public ImageQuestionStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ImageQuestionStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {

        this.mStep = (ImageQuestionStep) step;
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
                .inflate(layout.picture_record_time_layout, this, true);

        TextView title = (TextView) findViewById(id.title);
        title.setText(mStep.getTitle());

        ImageView image = (ImageView)findViewById(id.image1);
        imageFile = mStep.getImage();


        System.out.println(imageFile.length());
        Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        image.setImageBitmap(bm);
        //image show up time:


        final Button button1 = (Button) findViewById(id.begin_recording);
        final Button button2 = (Button) findViewById(id.no_skip);
        final TextView ins = (TextView) findViewById(id.instruction);

        button1.setText("Yes");
        button2.setText("No");
        ins.setText("Could you recognize this picture?");

        final SubmitBar submitBar = (SubmitBar)findViewById(R.id.submit_bar);
        submitBar.setPositiveTitle(org.researchstack.backbone.R.string.rsb_next);
        submitBar.getPositiveActionView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepCallbacks.onSaveStep(StepCallbacks.ACTION_NEXT, mStep, mResult);
            }
        });
        submitBar.getNegativeActionView().setVisibility(View.GONE);
        submitBar.getPositiveActionView().setVisibility(View.INVISIBLE);
        submitBar.getPositiveActionView().setEnabled(false);

        /* This is a timer for reaction, if it countdown to 0, then it will skip this question
         */
        final CountDownTimer reactionCount  = new CountDownTimer(mStep.getDuration()*1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                submitBar.getPositiveActionView().callOnClick();
            }
        };

        //The reaction countdown timer start
        reactionCount.start();

        //The click listener for button 1
        button1.setOnClickListener(new OnClickListener() {
            CountDownTimer Count = null;

            @Override
            public void onClick(View view) {


                //When the button is yes
                if(button1.getText().equals("Yes")){
                    //if the button1's text is yes,
                    //upload timestamp here
                    button1.setText("Record");
                    button2.setVisibility(GONE);
                    ins.setText("Click Record button to start recording.");
                    reactionCount.cancel();

                }
                //When the button is record (which means that the record can start
                else if(button1.getText().equals("Record")){
                    //2
                    button1.setText("Stop");

                    mFilename = getContext().getFilesDir().getAbsolutePath();
                    mFilename += "/testRecording.3gp";

                    final AudioRecorder audioRecorder = new AudioRecorder();
                    audioRecorder.startRecording(mFilename);

                    Count = new CountDownTimer(mStep.getDuration()*1000, 1000) {
                        @Override
                        public void onTick(long l) {
                            ins.setText("The record will be end in " + l/1000 + " seconds.");
                        }

                        @Override
                        public void onFinish() {
                            mIsRecordingComplete = true;

                            audioRecorder.stopRecording();;


                            FirebaseAdaptor fb = new FirebaseAdaptor();
                            fb.upload(mFilename);

                            ImageQuestionStepLayout.this.setDataToResult();
                            //mStepCallbacks.onSaveStep(StepCallbacks.ACTION_NEXT, mStep, mResult);
                            audioRecorder.stopRecording();
                            button1.setEnabled(false);
                            ins.setText("The record is done");
                            submitBar.getPositiveActionView().setEnabled(true);
                            submitBar.getPositiveActionView().setVisibility(View.VISIBLE);

                        }
                    };

                    //7
                    Count.start();
                }
                //When recording, click stop button can stop the timer and record

                else if(button1.getText().equals("Stop")){
                    Count.onFinish();
                    Count.cancel();
                }

            }
        });
    }
}
