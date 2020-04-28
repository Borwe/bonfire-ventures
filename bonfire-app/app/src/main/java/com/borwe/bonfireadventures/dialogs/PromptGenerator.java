package com.borwe.bonfireadventures.dialogs;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class PromptGenerator {

    public static AlertDialog generatePrompt(Activity activity,String title, String message,
                                             DialogInterface.OnClickListener okAction){
        return new AlertDialog.Builder(activity).setPositiveButton("Okay",okAction)
                .setTitle(title)
                .setMessage(message)
                .create();
    }

    public static AlertDialog generatePromptTwoButtonActions(Activity activity,String title,
                                                             String message,
                                                             DialogInterface.OnClickListener okAction,
                                                             String okText,
                                                             DialogInterface.OnClickListener cancelAction,
                                                             String cancelText){
        return new AlertDialog.Builder(activity).setPositiveButton(okText,okAction)
                .setNegativeButton(cancelText,cancelAction)
                .setTitle(title)
                .setMessage(message)
                .create();
    }
}
