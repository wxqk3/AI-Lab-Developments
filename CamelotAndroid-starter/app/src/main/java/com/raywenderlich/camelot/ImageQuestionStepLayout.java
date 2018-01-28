package com.raywenderlich.camelot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Base64;
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

import java.io.File;

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
        Bitmap bm = BitmapFactory.decodeResource(this.getContext().getResources(), mipmap.blue);
        image.setImageBitmap(bm);

        final Button beginButton = (Button) findViewById(id.begin_recording);
        final Button listenButton = (Button) findViewById(id.listen_recording);

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
    }
}
