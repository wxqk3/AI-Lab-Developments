package com.raywenderlich.camelot;

import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.Step;

/**
 * Created by kanbudong on 1/26/18.
 */

public class AudioStep extends Step
{
    private int mDuration;

    public AudioStep(String identifier)
    {
        super(identifier);
        setOptional(false);
        setStepLayoutClass(AudioStepLayout.class);
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }
}