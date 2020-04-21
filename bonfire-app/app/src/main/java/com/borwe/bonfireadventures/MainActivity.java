package com.borwe.bonfireadventures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borwe.bonfireadventures.viewActions.ViewAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    //Views
    Button signInButton;
    EditText nameEditText;
    EditText phoneNumberEditText;
    TextView errorMessageTextView;
    ProgressBar loadingBar;

    ViewAction<Button> signInButtonViewSetup;

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

        //setup view Action
        signInButtonViewSetup=new ViewAction<>(signInButton,false);

        setupSignInButtonActions();
    }

    private void hideInputViews(){
        //hide buttons and edittext's on this view
        nameEditText.setVisibility(View.GONE);
        phoneNumberEditText.setVisibility(View.GONE);
        signInButton.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
    }

    private void showInputViewsAndHideLoading(){
        //show input views for input
        nameEditText.setVisibility(View.VISIBLE);
        phoneNumberEditText.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
    }

    private void hideEverythingAndShowMainLoadingProgress(){
        hideInputViews();
        loadingBar.setVisibility(View.VISIBLE);
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
        });
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
                    .map(new Function<Map<String,String>, Boolean>() {
                        @Override
                        public Boolean apply(Map<String, String> stringStringMap) throws Exception {
                            //turn the values into a json object, save it on shared preferences
                            for(String keys:stringStringMap.keySet()){
                                Log.d("keys_"+keys,stringStringMap.get(keys));
                            }
                            return false;
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
