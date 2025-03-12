package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foodsellingapp.Adapter.OnboardingAdapter;
import com.example.foodsellingapp.Model.OnboardingItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class Onboarding extends AppCompatActivity {
    private static final String TAG = "Onboarding";
    private ViewPager2 viewPager;
    private TabLayout dotsIndicator;
    private MaterialButton getStartedBtn;
    private TextView signInLink;
    private TextView skipBtn, notsigninUser;
    private Handler autoSlideHandler;
    private Runnable autoSlideRunnable;
    private boolean isUserInteracting = false;
    private static final long SLIDE_INTERVAL = 5000;
    private static final long INITIAL_DELAY = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        Log.d(TAG, "OnCreate called");

        viewPager = findViewById(R.id.viewPager);
        if (viewPager == null) Log.e(TAG, "viewPager is null");
        dotsIndicator = findViewById(R.id.dotsIndicator);
        if (dotsIndicator == null) Log.e(TAG, "dotsIndicator is null");
        getStartedBtn = findViewById(R.id.getStartedBtn);
        if (getStartedBtn == null) Log.e(TAG, "getStartedBtn is null");
        signInLink = findViewById(R.id.signInLink);
        if (signInLink == null) Log.e(TAG, "signInLink is null");
        skipBtn = findViewById(R.id.skipBtn);
        if (skipBtn == null) Log.e(TAG, "skipBtn is null");

        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.logofood_ic, "Food Selling App", ""));
        onboardingItems.add(new OnboardingItem(0, "Your Recipe Haven Awaits Exploration!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt"));
        onboardingItems.add(new OnboardingItem(0, "Explore a World of Culinary Delights.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt"));
        Log.d(TAG, "Onboarding items created: " + onboardingItems.size());

        OnboardingAdapter adapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3); // Ensure all pages are preloaded
        viewPager.setCurrentItem(0, false); // Force start at first slide
        Log.d(TAG, "Adapter set to ViewPager, starting at position 0");

        viewPager.setPageTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            page.setAlpha(1 - absPosition);
            page.setTranslationX(-position * page.getWidth());
            Log.d(TAG, "Page transformer applied, position: " + position);
        });

        new TabLayoutMediator(dotsIndicator, viewPager, (tab, position) -> tab.view.setContentDescription(null)).attach();
        Log.d(TAG, "TabLayoutMediator attached");

        setupTabIndicator();

        setupAutoSlide();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "Page selected: " + position);
                if (position == 1) { // Second slide (position 1)
                    getStartedBtn.setVisibility(View.VISIBLE);
                    signInLink.setVisibility(View.VISIBLE);
                    skipBtn.setVisibility(View.GONE);
                    notsigninUser.setVisibility(View.GONE);
                } else if (position == 2) { // Third slide (position 2)
                    getStartedBtn.setVisibility(View.GONE);
                    signInLink.setVisibility(View.GONE);
                    skipBtn.setVisibility(View.VISIBLE);
                    notsigninUser.setVisibility(View.VISIBLE);
                } else { // First slide (position 0)
                    getStartedBtn.setVisibility(View.VISIBLE);
                    signInLink.setVisibility(View.GONE);
                    skipBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d(TAG, "Scroll state changed: " + state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isUserInteracting = true;
                    stopAutoSlide();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isUserInteracting = false;
                    if (viewPager.getCurrentItem() < onboardingItems.size() - 1) {
                        startAutoSlide();
                    }
                }
            }
        });

        getStartedBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding.this, LoginPage.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Get Started clicked");
        });

        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding.this, LoginPage.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Sign In clicked");
        });

        skipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding.this, LoginPage.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Skip clicked");
        });
        notsigninUser=findViewById(R.id.notsigninUser);
        notsigninUser.setOnClickListener(v->{
            Intent intent = new Intent(Onboarding.this, User_MainPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupTabIndicator() {
        for (int i = 0; i < dotsIndicator.getTabCount(); i++) {
            TabLayout.Tab tab = dotsIndicator.getTabAt(i);
            if (tab != null) {
                View tabView = tab.view;
                if (tabView != null) {
                    tabView.setBackgroundResource(R.drawable.tab_indicator_selected);
                    ViewGroup.LayoutParams params = tabView.getLayoutParams();
                    if (params instanceof ViewGroup.MarginLayoutParams) {
                        ((ViewGroup.MarginLayoutParams) params).setMargins(4, 0, 4, 0);
                        tabView.setLayoutParams(params);
                    }
                    tabView.setMinimumWidth(0);
                    tabView.setMinimumHeight(0);
                }
            }
        }
        Log.d(TAG, "Tab indicator set up");
    }

    private void setupAutoSlide() {
        autoSlideHandler = new Handler(Looper.getMainLooper());
        autoSlideRunnable = new Runnable() {
            private boolean isFirstSlide = true;

            @Override
            public void run() {
                Log.d(TAG, "AutoSlide running, isUserInteracting: " + isUserInteracting);
                if (!isUserInteracting) {
                    int currentItem = viewPager.getCurrentItem();
                    int totalItems = viewPager.getAdapter().getItemCount();
                    if (currentItem < totalItems - 1) {
                        int nextItem = currentItem + 1;
                        viewPager.setCurrentItem(nextItem, true);
                        long delay = isFirstSlide ? INITIAL_DELAY : SLIDE_INTERVAL;
                        isFirstSlide = false;
                        autoSlideHandler.postDelayed(this, delay);
                    } else {
                        stopAutoSlide();
                    }
                } else {
                    autoSlideHandler.postDelayed(this, SLIDE_INTERVAL);
                }
            }
        };
        startAutoSlide();
        Log.d(TAG, "AutoSlide set up");
    }

    private void startAutoSlide() {
        if (autoSlideHandler != null && autoSlideRunnable != null) {
            autoSlideHandler.removeCallbacks(autoSlideRunnable);
            autoSlideHandler.postDelayed(autoSlideRunnable, INITIAL_DELAY);
            Log.d(TAG, "AutoSlide started");
        }
    }

    private void stopAutoSlide() {
        if (autoSlideHandler != null && autoSlideRunnable != null) {
            autoSlideHandler.removeCallbacks(autoSlideRunnable);
            Log.d(TAG, "AutoSlide stopped");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoSlide();
        autoSlideHandler = null;
        autoSlideRunnable = null;
        Log.d(TAG, "OnDestroy called");
    }
}