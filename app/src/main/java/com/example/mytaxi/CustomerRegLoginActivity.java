package com.example.mytaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegLoginActivity extends AppCompatActivity {

    TextView customerStatus, question, tvyetcus;
    Button signInBtn, signUpBtn;
    EditText emailET, passwordET;

    FirebaseAuth mAuth;
    DatabaseReference CustomerDatabaseRef;
    String OnlineCustomerID;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_reg_login);

        customerStatus = (TextView)findViewById(R.id.statusCustomer);
        question = (TextView)findViewById(R.id.accountCreateCustomer);
        tvyetcus=(TextView)findViewById(R.id.TVYetCustomer);
        signInBtn = (Button) findViewById(R.id.signInCustomer);
        signUpBtn = (Button) findViewById(R.id.signUpCustomer);
        emailET = (EditText) findViewById(R.id.customerEmail);
        passwordET = (EditText) findViewById(R.id.customerPassword);

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
                tvyetcus.setVisibility(View.INVISIBLE);
                signUpBtn.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(true);
                customerStatus.setText("Регистрация для клиентов");
            }
        });

        //регистрация
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                RegisterCustomer(email, password);
            }
        });

        //вход
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                SignInCustomer(email, password);
            }
        });
    }

    // метод входа
    //отправка данных и проверка в firebase
    private void SignInCustomer(String email, String password) {

        loadingBar.setTitle("Вход для клиентов");
        loadingBar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingBar.show();

        //вход c email и с паролем
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CustomerRegLoginActivity.this, "Успешный вход.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    //переход в карту клиента
                    Intent customerIntent = new Intent(CustomerRegLoginActivity.this, CustomersMapActivity.class);
                    startActivity(customerIntent);
                }
                else
                {
                    Toast.makeText(CustomerRegLoginActivity.this, "Произошла ошибка, попробуйте снова", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }


    // метод регистрации
    //отправка данных в firebase
    private void RegisterCustomer(String email, String password)
    {
        loadingBar.setTitle("Регистрация клиента");
        loadingBar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingBar.show();

        //регистрация c email и с паролем
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    //запись в firebase  в папку customers
                    OnlineCustomerID = mAuth.getCurrentUser().getUid();
                    CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Customers").child(OnlineCustomerID);
                    CustomerDatabaseRef.setValue(true);

                    //переход в карту клиента
                    Intent customerIntent = new Intent(CustomerRegLoginActivity.this, CustomersMapActivity.class);
                    startActivity(customerIntent);

                    Toast.makeText(CustomerRegLoginActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(CustomerRegLoginActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}
