package com.victorldavila.funnyguide.view.presenters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.victorldavila.funnyguide.R;
import com.victorldavila.funnyguide.view.fragments.MovieFragment;

/**
 * Created by victo on 10/12/2016.
 */

public class MainPresenter {

    private final String nameClass = this.getClass().getSimpleName();

    private static final String MOVIE_NAME = "MOVIE";
    private static final String SERIES_NAME = "SERIES";
    private static final String PROFILE_NAME = "PROFILE";

    Context context;

    public MainPresenter(Context context) {
        this.context = context;
    }

    public boolean fragmentChange(int id) {
        Log.d(nameClass, "Change fragment bottom navigation");

        Fragment fragment = null;
        String fragmentName = null;

        switch (id) {
            case R.id.action_movies:
                fragmentName = MOVIE_NAME;
                fragment = MovieFragment.newInstance();
                break;
            case R.id.action_series:
                fragmentName = SERIES_NAME;
                fragment = MovieFragment.newInstance();
                break;
            case R.id.action_profile:
                fragmentName = PROFILE_NAME;
                fragment = MovieFragment.newInstance();
                break;
            default:
                fragmentName = MOVIE_NAME;
                fragment = MovieFragment.newInstance();
                break;
        }

        if(fragment != null) {
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            // Add to backstack
            ft.addToBackStack(fragmentName);
            ft.commit();

            return true;
        }

        return false;
    }
}