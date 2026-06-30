package com.fantasychess.hpt.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fantasychess.hpt.data.AppDatabase;
import com.fantasychess.hpt.data.entity.User;
import com.fantasychess.hpt.databinding.ActivityLoginBinding;
import com.fantasychess.hpt.game.AppExecutors;
import com.fantasychess.hpt.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Session.isLoggedIn(this)) {
            goToMain();
            return;
        }

        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnLogin.setOnClickListener(v -> attemptLogin());
        b.btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String username = b.inputUsername.getText().toString().trim();
        String password = b.inputPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            toast("נא למלא שם משתמש וסיסמה");
            return;
        }
        b.btnLogin.setEnabled(false);
        AppExecutors.io(() -> {
            AppDatabase db = AppDatabase.get(this);
            User user = db.userDao().findByUsername(username);
            boolean ok = user != null
                    && PasswordHasher.verify(password, user.salt, user.passwordHash);
            AppExecutors.main(() -> {
                b.btnLogin.setEnabled(true);
                if (ok) {
                    Session.login(this, user.id, user.username);
                    goToMain();
                } else {
                    toast("שם משתמש או סיסמה שגויים");
                }
            });
        });
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
