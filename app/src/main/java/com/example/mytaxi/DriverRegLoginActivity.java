package com.example.mytaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegLoginActivity extends AppCompatActivity {

    TextView driverStatus, question,tvyet;
    Button signInBtn, signUpBtn;
    EditText emailET, passwordET;

    FirebaseAuth mAuth;
    DatabaseReference DriverDatabaseRef;
    String OnlineDriverID;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reg_login);

        driverStatus = (TextView)findViewById(R.id.statusDriver);
        question = (TextView)findViewById(R.id.accountCreate);
        tvyet=(TextView)findViewById(R.id.TVYet);
        signInBtn = (Button) findViewById(R.id.signInDriver);
        signUpBtn = (Button) findViewById(R.id.signUpDriver);
        emailET = (EditText) findViewById(R.id.driverEmail);
        passwordET = (EditText) findViewById(R.id.driverPassword);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        signUpBtn.setVisibility(View.INVISIBLE);
        signUpBtn.setEnabled(false);

        //действие при нажатии кнопки "зарегаться"
        //исчезновение кнопки "войти" и появление кнопки "регистр"
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInBtn.setVisibility(View.INVISIBLE);
                question.setVisibility(View.INVISIBLE);
                tvyet.setVisibility(View.INVISIBLE);
                signUpBtn.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(true);
                driverStatus.setText("Регистрация для водителей");
            }
        });

        //регистрация
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                RegisterDriver(email, password);
            }
        });

        //вход
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                SignInDriver(email, password);
            }
        });

    }

    // метод входа
    //отправка данных и проверка в firebase
    private void SignInDriver(String email, String password)
    {
        loadingBar.setTitle("Вход водителя");
        loadingBar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingBar.show();

        //вход c email и с паролем
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(DriverRegLoginActivity.this, "Успешный вход.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    //переход в карту водителя
                    Intent driverIntent = new Intent(DriverRegLoginActivity.this, DriversMapActivity.class);
                    startActivity(driverIntent);
                }
                else
                {
                    Toast.makeText(DriverRegLoginActivity.this, "Произошла ошибка, попробуйте снова", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
    // метод регистрации
    //отправка данных в firebase
    private void RegisterDriver(String email, String password)
    {
        loadingBar.setTitle("Регистрация водителя");
        loadingBar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingBar.show();

        //регистрация c email и с паролем
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    //запись в firebase в папку drivers
                    OnlineDriverID = mAuth.getCurrentUser().getUid();
                    DriverDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Drivers").child(OnlineDriverID);
                    DriverDatabaseRef.setValue(true);

                    //переход в карту водителя
                    Intent driverIntent = new Intent(DriverRegLoginActivity.this, DriversMapActivity.class);
                    startActivity(driverIntent);

                    Toast.makeText(DriverRegLoginActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
                else
                {
                    Toast.makeText(DriverRegLoginActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

}