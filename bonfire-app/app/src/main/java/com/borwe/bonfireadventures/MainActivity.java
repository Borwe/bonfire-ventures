package com.borwe.bonfireadventures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.borwe.bonfireadventures.user.UserDataProfile;
import com.borwe.bonfireadventures.viewActions.ViewAction;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
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

        setupViews();
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

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks generatePhoneAuthCallbak(){
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callback=
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //meaning code was sent and read succesfully
                        //automatically or manually
                        //then go ahead and do a firbase signin
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        //if invalid phone number format is passed
                        if(e instanceof FirebaseAuthInvalidCredentialsException){
                            //display warnning message about bad phone number
                            //input

                        }else if(e instanceof FirebaseTooManyRequestsException){
                            //display error message that user exceeded limit
                        }else{
                            //display error message that something unknown went wrong
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        MainActivity.this.hideEverythingButShowSMSCode();
                    }
                };
        return callback;
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
