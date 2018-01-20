/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.camelot;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_CONSENT = 0;
  private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

  private boolean mPermissionToRecordAccepted = false;
  private String[] mPermissions = {RECORD_AUDIO};

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button consentButton = (Button)findViewById(R.id.consentButton);

    consentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        displayConsent();
      }
    });

    Button surveyButton = (Button)findViewById(R.id.surveyButton);

    surveyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        displaySurvey();
      }
    });

    Button microphoneButton = (Button)findViewById(R.id.microphoneButton);

    microphoneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
            RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
          displayAudioTask();
        } else {
          ActivityCompat.requestPermissions(MainActivity.this, mPermissions,
              REQUEST_RECORD_AUDIO_PERMISSION);
        }
      }
    });
  }


  private void displayConsent() {
// 1
    ConsentDocument document = createConsentDocument();

// 2
    List<Step> steps = createConsentSteps(document);

// 3
    Task consentTask = new OrderedTask("consent_task", steps);

// 4
    Intent intent = ViewTaskActivity.newIntent(this, consentTask);
    startActivityForResult(intent, REQUEST_CONSENT);
  }

  private void displaySurvey() {

  }

  private void displayAudioTask() {

  }

  private ConsentDocument createConsentDocument() {

    ConsentDocument document = new ConsentDocument();

    document.setTitle("Demo Consent");
    document.setSignaturePageTitle(R.string.rsb_consent);

    List<ConsentSection> sections = new ArrayList<>();

    sections.add(createSection(ConsentSection.Type.Overview, "Overview Info", "<h1>Read " +
            "This!</h1><p>Some " +
            "really <strong>important</strong> information you should know about this step"));
    sections.add(createSection(ConsentSection.Type.DataGathering, "Data Gathering Info", ""));
    sections.add(createSection(ConsentSection.Type.Privacy, "Privacy Info", ""));
    sections.add(createSection(ConsentSection.Type.DataUse, "Data Use Info", ""));
    sections.add(createSection(ConsentSection.Type.TimeCommitment, "Time Commitment Info", ""));
    sections.add(createSection(ConsentSection.Type.StudySurvey, "Study Survey Info", ""));
    sections.add(createSection(ConsentSection.Type.StudyTasks, "Study Task Info", ""));
    sections.add(createSection(ConsentSection.Type.Withdrawing, "Withdrawing Info", "Some detailed steps " +
            "to withdrawal from this study. <ul><li>Step 1</li><li>Step 2</li></ul>"));

    document.setSections(sections);

    ConsentSignature signature = new ConsentSignature();
    signature.setRequiresName(true);
    signature.setRequiresSignatureImage(true);

    document.addSignature(signature);

    document.setHtmlReviewContent("<div style=\"padding: 10px;\" class=\"header\">" +
            "<h1 style='text-align: center'>Review Consent!</h1></div>");


    return document;
  }

  private ConsentSection createSection(ConsentSection.Type type, String summary, String content) {

    ConsentSection section = new ConsentSection(type);
    section.setSummary(summary);
    section.setHtmlContent(content);



    return section;
  }


  private List<Step> createConsentSteps(ConsentDocument document) {

    List<Step> steps = new ArrayList<>();

    for (ConsentSection section: document.getSections()) {
      ConsentVisualStep visualStep = new ConsentVisualStep(section.getType().toString());
      visualStep.setSection(section);
      visualStep.setNextButtonString(getString(R.string.rsb_next));
      steps.add(visualStep);
    }

    ConsentDocumentStep documentStep = new ConsentDocumentStep("consent_doc");
    documentStep.setConsentHTML(document.getHtmlReviewContent());
    documentStep.setConfirmMessage(getString(R.string.rsb_consent_review_reason));

    steps.add(documentStep);

    ConsentSignature signature = document.getSignature(0);

    if (signature.requiresName()) {
      TextAnswerFormat format = new TextAnswerFormat();
      format.setIsMultipleLines(false);

      QuestionStep fullName = new QuestionStep("consent_name_step", "Please enter your full name",
              format);
      fullName.setPlaceholder("Full name");
      fullName.setOptional(false);
      steps.add(fullName);
    }

    if (signature.requiresSignatureImage()) {

      ConsentSignatureStep signatureStep = new ConsentSignatureStep("signature_step");
      signatureStep.setTitle(getString(R.string.rsb_consent_signature_title));
      signatureStep.setText(getString(R.string.rsb_consent_signature_instruction));
      signatureStep.setOptional(false);

      signatureStep.setStepLayoutClass(ConsentSignatureStepLayout.class);

      steps.add(signatureStep);
    }
    return steps;
  }

}
