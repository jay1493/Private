package com.example.anubhav.musicapp.Animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.List;
import java.util.Random;

/**
 * Created by anubhav on 17/2/17.
 */

public class LoginAnimation {
    private final Random random;
    ImageView view;
    private int start,end;

    public LoginAnimation(ImageView view) {
        this.view = view;
        random = new Random(System.currentTimeMillis());
    }
    public void animate(final List<Integer> images) {
        updateImages(images);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.1f);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);
        alphaAnimator.setDuration(10000);
        // For backgroundResource we are not able to get Setter for this property.
        ObjectAnimator backgroundAnimator = ObjectAnimator.ofInt(view, "imageResource", start, end);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.invalidate();
            }
        });
        backgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
        backgroundAnimator.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(alphaAnimator).before(backgroundAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animate(images);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animatorSet.start();
      /*  alphaAnimator.setInterpolator(new AccelerateInterpolator());
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.invalidate();
            }
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Random random = new Random(System.currentTimeMillis());
                int pos = random.nextInt(images.size()-1);
                view.setBackgroundResource(images.get(pos));
                animate(images);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        alphaAnimator.start();*/
    }
    public void animateAlpha(final List<Integer> images){
        final ObjectAnimator alphaInAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.2f);
        alphaInAnimator.setDuration(10000);
        alphaInAnimator.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator alphaOutAnimator = ObjectAnimator.ofFloat(view, "alpha", 0.2f, 1f);
        alphaOutAnimator.setDuration(10000);
        alphaOutAnimator.setInterpolator(new AccelerateInterpolator());

        alphaOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
              view.invalidate();
            }
        });
        alphaOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.clearAnimation();
                int pos = random.nextInt(images.size()-1);
                view.setImageResource(images.get(pos));
                Log.e("", "onAnimationEnd==In: "+pos );
                alphaInAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });



        alphaInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

            }
        });
        alphaInAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.clearAnimation();
                int pos = random.nextInt(images.size()-1);
                view.setImageResource(images.get(pos));
                Log.e("", "onAnimationEnd: "+pos );
                alphaOutAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        alphaInAnimator.start();




      /*  AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(20000);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.play(alphaInAnimator).before(alphaOutAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });*/
    }
    private void updateImages(List<Integer> images) {
        int pos = random.nextInt(images.size()-1);
        start = images.get(pos);
        if(pos == images.size()-1){
            end = images.get(0);
        }else{
            end = images.get(pos+1);
        }
    }

}
