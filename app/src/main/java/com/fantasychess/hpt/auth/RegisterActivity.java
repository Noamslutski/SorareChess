package com.fantasychess.hpt.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fantasychess.hpt.data.AppDatabase;
import com.fantasychess.hpt.data.entity.User;
import com.fantasychess.hpt.databinding.ActivityRegisterBinding;
import com.fantasychess.hpt.game.AppExecutors;
import com.fantasychess.hpt.ui.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnRegister.setOnClickListener(v -> attemptRegister());
        b.btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String username = b.inputUsername.getText().toString().trim();
        String email = b.inputEmail.getText().toString().trim();
        String password = b.inputPassword.getText().toString();
        String confirm = b.inputConfirm.getText().toString();

        if (username.length() < 3) { toast("שם משתמש קצר מדי"); return; }
        if (password.length() < 4) { toast("הסיסמה חייבת להכיל לפחות 4 תווים"); return; }
        if (!password.equals(confirm)) { toast("הסיסמאות אינן תואמות"); return; }

        b.btnRegister.setEnabled(false);
        AppExecutors.io(() -> {
            AppDatabase db = AppDatabase.get(this);
            if (db.userDao().countByUsername(username) > 0) {
                AppExecutors.main(() -> {
                    b.btnRegister.setEnabled(true);
                    toast("שם המשתמש כבר תפוס");
                });
                return;
            }
            User u = new User();
            u.username = username;
            u.email = email;
            u.salt = PasswordHasher.newSalt();
            u.passwordHash = PasswordHasher.hash(password, u.salt);
            u.createdAt = System.currentTimeMillis();
            long id = db.userDao().insert(u);

            AppExecutors.main(() -> {
                Session.login(this, id, username);
                Toast.makeText(this, "ברוך הבא, " + username + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            });
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
