package com.borwe.bonfireadventures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borwe.bonfireadventures.viewActions.ViewAction;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
                            Log.d("BOOM: ","BOOM!!!");
                            Toast.makeText(MainActivity.this,"YOLO",Toast.LENGTH_SHORT).show();
                            signInButtonViewSetup.doneAction();
                            return integer;
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
