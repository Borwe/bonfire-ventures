package com.borwe.bonfireadventures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.borwe.bonfireadventures.dialogs.PromptGenerator;
import com.borwe.bonfireadventures.user.UserDataProfile;
import com.borwe.bonfireadventures.viewActions.ViewAction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    //Views
    Button signInButton;
    Button verifyButton;
    EditText nameEditText;
    EditText phoneNumberEditText;
    EditText smsCode;
    TextView errorMessageTextView;
    ProgressBar loadingBar;

    ViewAction<Button> signInButtonViewSetup;
    ViewAction<Button> verifyButtonSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkIfUserSignedIn()){
            //if user logged in, then move to real activity
        }
        //otherwise user didn't login, keep on witht his activity

        setupViews();
    }

    /**
     * @return true, if signed in, false if not
     */
    private boolean checkIfUserSignedIn(){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser() ==null){
            Log.d("LOGGED_IN:","NOPE");
            return false;
        }
        Log.d("LOGGED_IN:","YES");
        return true;
    }

    private void setupViews(){

        //setup main views
        nameEditText=findViewById(R.id.user_name_editText);
        phoneNumberEditText=findViewById(R.id.user_phone_editText);
        signInButton=findViewById(R.id.signInButton);
        errorMessageTextView=findViewById(R.id.error_message);
        loadingBar=findViewById(R.id.loadingBar);
        smsCode=findViewById(R.id.verify_phone);
        verifyButton=findViewById(R.id.verify_button);

        //setup view Action
        signInButtonViewSetup=new ViewAction<>(signInButton,false);
        verifyButtonSetup=new ViewAction<>(verifyButton,false);

        setupSignInButtonActions();
    }

    private void hideInputViews(){
        //hide buttons and edittext's on this view
        nameEditText.setVisibility(View.GONE);
        phoneNumberEditText.setVisibility(View.GONE);
        signInButton.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
        smsCode.setVisibility(View.GONE);
        verifyButton.setVisibility(View.GONE);
    }

    private void showInputViewsAndHideLoading(){
        //show input views for input
        nameEditText.setVisibility(View.VISIBLE);
        phoneNumberEditText.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
        smsCode.setVisibility(View.GONE);
        verifyButton.setVisibility(View.GONE);
    }

    private void hideEverythingAndShowMainLoadingProgress(){
        hideInputViews();
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void hideEverythingButShowSMSCode(){
        hideInputViews();
        loadingBar.setVisibility(View.GONE);
        smsCode.setVisibility(View.VISIBLE);
        verifyButton.setVisibility(View.VISIBLE);
    }

    private Single<Boolean> checkInputValuesAreValid(){
        return Observable.fromIterable(getInputValuesAsLIst()).filter(new Predicate<Pair<String, String>>() {
            @Override
            public boolean test(Pair<String, String> stringStringPair) throws Exception {
                String value=stringStringPair.second;
                if(value==null || value.trim().isEmpty()){
                    return false;
                }
                return true;
            }
        }).count().map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                return aLong>0;
            }
        }).observeOn(AndroidSchedulers.mainThread()).map(new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean aBoolean) throws Exception {
                if(aBoolean==false){
                    //show error message prompting user to input all fields with data
                    AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this)
                            .setTitle(MainActivity.this.getString(R.string.error_occured))
                            .setMessage(MainActivity.this.getString(R.string.input_all_fields))
                            .setPositiveButton(MainActivity.this.getString(R.string.ok),null)
                            .create();
                    alertDialog.show();
                }
                return aBoolean;
            }
        }).observeOn(Schedulers.computation());
    }

    private List<Pair<String,String>> getInputValuesAsLIst() {
        List<Pair<String, String>> pairs = new ArrayList<>();
        Pair<String, String> name = new Pair<>(this.getString(R.string.name_hint), nameEditText.getText().toString());
        Pair<String, String> phone = new Pair<>(this.getString(R.string.phone_hint), phoneNumberEditText.getText().toString());
        pairs.add(name);
        pairs.add(phone);

       return pairs;
    }

    private Maybe<Map<String,String>> getValuesInputByKeyMap(){
        return checkInputValuesAreValid().filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        }).map(new Function<Boolean, Map<String,String>>() {
            @Override
            public Map<String, String> apply(Boolean aBoolean) throws Exception {
                Map<String,String> mappedValues=new HashMap<>();

                List<Pair<String,String>> values=getInputValuesAsLIst();
                for(Pair<String,String> pair:values){
                    mappedValues.put(pair.first,pair.second);
                }
                return mappedValues;
            }
        });
    }

    /**
     * Method for doing signIn
     * @param credential
     */
    private synchronized void signIn(PhoneAuthCredential credential){
        //signin with credentials
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        if(MainActivity.this.checkIfUserSignedIn()==true){
            //then means user already signed in
            //so skip going ahead
            return;
        }
        mauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("LOGIN","SUCCESS");
                }else{
                    Log.e("LOGIN","FAILED");
                    //show error message, with button that closes app
                    AlertDialog dialog=PromptGenerator.generatePrompt(MainActivity.this,
                            MainActivity.this.getString(R.string.error_occured),
                            MainActivity.this.getString(R.string.error_invalid_verify_input),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            });
                    dialog.show();
                }
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks generatePhoneAuthCallbak(){
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callback=
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //meaning code was sent and read succesfully
                        //automatically or manually
                        //then go ahead and do a firebase signin
                        signIn(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        //if invalid phone number format is passed
                        if(e instanceof FirebaseAuthInvalidCredentialsException){
                            //display warnning message about bad phone number
                            //input
                            AlertDialog dialog= PromptGenerator.generatePrompt(MainActivity.this,
                                    MainActivity.this.getString(R.string.error_invalid_credentials),
                                    MainActivity.this.getString(R.string.error_invalid_credentials_message),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //hide everything and redisplay the logging in GUI
                                            MainActivity.this.showInputViewsAndHideLoading();
                                        }
                                    });
                            dialog.show();
                        }else if(e instanceof FirebaseTooManyRequestsException){
                            //display error message that user exceeded limit
                            AlertDialog dialog= PromptGenerator.generatePrompt(MainActivity.this,
                                    MainActivity.this.getString(R.string.error_request_many),
                                    MainActivity.this.getString(R.string.error_request_many_message),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.this.showInputViewsAndHideLoading();
                                        }
                                    });
                            dialog.show();
                        }else{
                            //display error message that something unknown went wrong
                            AlertDialog dialog= PromptGenerator.generatePrompt(MainActivity.this,
                                    MainActivity.this.getString(R.string.error_occured),
                                    MainActivity.this.getString(R.string.error_occured_message),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.this.showInputViewsAndHideLoading();
                                        }
                                    });
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        MainActivity.this.hideEverythingButShowSMSCode();

                        //get code info once user clicks button
                        MainActivity.this.startVerifyButtonSetup(s);
                    }
                };
        return callback;
    }

    private void startVerifyButtonSetup(@NonNull final String verificationID){
        verifyButtonSetup.setOnClickListerner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.just(1).
                        filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return verifyButtonSetup.isStarted()==false;
                            }
                        }).
                        map(new Function<Integer, Integer>() {
                            @Override
                            public Integer apply(Integer integer) throws Exception {
                                verifyButtonSetup.startAction();
                                return integer;
                            }
                        }).observeOn(Schedulers.io())
                        .map(new Function<Integer, String>() {

                            @Override
                            public String apply(Integer integer) throws Exception {
                                //get string from verification field
                                String smsCode=MainActivity.this.smsCode.getText().toString();
                                return smsCode;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                //check if smscode has value or not
                                if(s==null || s.trim().isEmpty()){
                                    //meaning no value actualy passed
                                    //popup a dialog box asking user to input a propper code
                                    AlertDialog dialog=PromptGenerator.generatePromptTwoButtonActions(MainActivity.this,
                                            MainActivity.this.getString(R.string.error_occured),
                                            MainActivity.this.getString(R.string.error_no_valid_verifyNumber),
                                            null, MainActivity.this.getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //exit application
                                                    MainActivity.this.finish();
                                                }
                                            },MainActivity.this.getString(R.string.exit));
                                    dialog.show();
                                    return false;
                                }
                                return true;
                            }
                        }).observeOn(Schedulers.computation())
                        .map(new Function<String, PhoneAuthCredential>() {
                            @Override
                            public PhoneAuthCredential apply(String verifyCode) throws Exception {
                                Log.d("VERIFYCODE:",verifyCode);
                                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,verifyCode);
                                return credential;
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<PhoneAuthCredential, PhoneAuthCredential>() {
                            @Override
                            public PhoneAuthCredential apply(PhoneAuthCredential credential) throws Exception {
                                //do sign in, and return credential after done
                                signIn(credential);
                                return credential;
                            }
                        })
                        .map(new Function<PhoneAuthCredential, Integer>() {
                            @Override
                            public Integer apply(PhoneAuthCredential credential) throws Exception {
                                verifyButtonSetup.doneAction();
                                return 0;
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });
    }

    private void setupSignInButtonActions(){
        signInButtonViewSetup.setOnClickListerner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if action not done, then go ahead and do it
                if(signInButtonViewSetup.isStarted()==false){
                    Observable.just(1).map(new Function<Integer, Integer>() {

                        @Override
                        public Integer apply(Integer integer) throws Exception {
                            signInButtonViewSetup.startAction();
                            return integer;
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer integer) throws Exception {
                            //hide the everything and show progress bar
                            hideEverythingAndShowMainLoadingProgress();
                            return integer;
                        }
                    }).observeOn(Schedulers.computation())
                    .flatMap(new Function<Integer, ObservableSource<Map<String,String>>>() {
                        @Override
                        public ObservableSource<Map<String,String>> apply(Integer integer) throws Exception {
                            //get key values
                            return getValuesInputByKeyMap().toObservable();
                        }
                    })
                    .map(new Function<Map<String,String>, UserDataProfile>() {
                        @Override
                        public UserDataProfile apply(Map<String, String> stringStringMap) throws Exception {
                            //turn the values into a json object, save it on shared preferences
                            UserDataProfile userDataProfile=new UserDataProfile();
                            userDataProfile
                                    .setUser_name(stringStringMap
                                            .get(MainActivity.this.getString(R.string.name_hint)));
                            userDataProfile.
                                    setUser_phone(stringStringMap
                                            .get(MainActivity.this.getString(R.string.phone_hint)));

                            Log.d("name:",userDataProfile.getUser_name());
                            Log.d("phone:",userDataProfile.getUser_phone());
                            return userDataProfile;
                        }
                    }).map(new Function<UserDataProfile, UserDataProfile>() {
                        @Override
                        public UserDataProfile apply(UserDataProfile userDataProfile) throws Exception {
                            //do the logging in here
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(userDataProfile.getUser_phone(),
                                    60, TimeUnit.SECONDS,MainActivity.this
                                    ,MainActivity.this.generatePhoneAuthCallbak());
                            return userDataProfile;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread()).doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            //allow signInButton to be clickable again
                            showInputViewsAndHideLoading();
                            signInButtonViewSetup.doneAction();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe();
                }
            }
        });
    }
}
