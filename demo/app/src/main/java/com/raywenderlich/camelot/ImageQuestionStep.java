package com.raywenderlich.camelot;

import org.researchstack.backbone.step.Step;

import java.io.File;

/**
 * Created by kanbudong on 1/27/18.
 */

public class ImageQuestionStep extends Step {

    private int mDuration;
    private File imageFile;

    public ImageQuestionStep(String identifier)
    {
        super(identifier);
        setOptional(false);
        setStepLayoutClass(ImageQuestionStepLayout.class);

    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setImage(File file){
        imageFile = file;
    }
    public File getImage(){
        return imageFile;
    }

}
