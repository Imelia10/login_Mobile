package com.example.rewear_app1.presentation.splash.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import androidx.lifecycle.ViewModelProvider;

import com.example.rewear_app1.AdminActivity;
import com.example.rewear_app1.HomeActivity;
import com.example.rewear_app1.presentation.login.view.LoginActivity;
import com.example.rewear_app1.databinding.ActivitySplashScreenBinding;
import com.example.rewear_app1.presentation.splash.viewmodel.SplashScreenViewModel;

public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private SplashScreenViewModel splashScreenViewModel;


    private void listenObserver(){
        splashScreenViewModel.getNavigationTarget().observe(this, target -> {
            Intent intent;
            switch (target) {
                case "admin":
                    intent = new Intent(this, AdminActivity.class);
                    break;
                case "user":
                    intent = new Intent(this, HomeActivity.class);
                    break;
                default:
                    intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // jangan kembali ke splash
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        splashScreenViewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);

        listenObserver();

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(this::runSplashTransition, 1000);
    }
    private void runSplashTransition() {
        // Pastikan splash2 dan iconApp siap
        binding.ivSplash2.setVisibility(View.VISIBLE);
        binding.ivSplash2.setAlpha(0f); // Awalnya tidak terlihat
        binding.ivIconApp.setVisibility(View.INVISIBLE);

        // --- Zoom In splash1 (1x → 1.3x) ---
        ObjectAnimator zoomInX = ObjectAnimator.ofFloat(binding.ivSplash1, "scaleX", 1f, 1.9f);
        ObjectAnimator zoomInY = ObjectAnimator.ofFloat(binding.ivSplash1, "scaleY", 1f, 1.9f);
        AnimatorSet zoomInSplash1 = new AnimatorSet();
        zoomInSplash1.playTogether(zoomInX, zoomInY);
        zoomInSplash1.setDuration(300);
        zoomInSplash1.setInterpolator(new AccelerateInterpolator());

        // Setelah zoom, sembunyikan splash1 dan munculkan splash2 (fade in)
        zoomInSplash1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.ivSplash1.setVisibility(View.GONE); // ⛔ hilang total
                // Fade in splash2
                ObjectAnimator fadeInSplash2 = ObjectAnimator.ofFloat(binding.ivSplash2, "alpha", 0f, 1f);
                fadeInSplash2.setDuration(300);
                fadeInSplash2.setInterpolator(new AccelerateInterpolator());

                fadeInSplash2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Setelah splash2 muncul → rotasi iconApp
                        ObjectAnimator rotateIconApp = ObjectAnimator.ofFloat(binding.ivIconApp, "rotation", 0f, 360f);
                        rotateIconApp.setDuration(800);
                        rotateIconApp.setInterpolator(new AccelerateInterpolator());

                        rotateIconApp.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                binding.ivIconApp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                splashScreenViewModel.checkSession(SplashScreenActivity.this);
                            }
                        });

                        rotateIconApp.start();
                    }
                });

                fadeInSplash2.start();
            }
        });

        // Mulai animasi
        zoomInSplash1.start();
    }

}