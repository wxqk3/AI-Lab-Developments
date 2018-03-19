package com.raywenderlich.camelot;

import org.researchstack.backbone.model.Choice;

import java.util.List;

/**
 * Created by kanbudong on 2/24/18.
 */

public class Question {
    public String title;
    public String type;
    public List<String> choices;
    public Long answer;

    public Question(){

    }

    public Question(String title,String type, List<String> choices,Long answer){
        this.title = title;
        this.type = type;
        this.choices = choices;
        this.answer = answer;

    }


}
