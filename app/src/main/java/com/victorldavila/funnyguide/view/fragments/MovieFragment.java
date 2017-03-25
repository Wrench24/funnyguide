package com.victorldavila.funnyguide.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.victorldavila.funnyguide.FunnyGuideApp;
import com.victorldavila.funnyguide.R;
import com.victorldavila.funnyguide.adapter.MovieGridAdapter;
import com.victorldavila.funnyguide.api.FunnyApi;
import com.victorldavila.funnyguide.models.Movie;
import com.victorldavila.funnyguide.view.activities.DetailItemActivity;
import com.victorldavila.funnyguide.view.presenters.MoviePresenterImp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * Use the {@link MovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment implements MovieFragmentView{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_GENRE_ID = "GENRE_ID";

    @BindView(R.id.recycler_movie_item) RecyclerView movieRecyclerView;
    @BindView(R.id.coordinator_layout_movie) CoordinatorLayout coordinatorLayoutMovie;

    private MovieGridAdapter movieGridAdapter;
    private MoviePresenterImp presenter;
    private Unbinder unbinder;

    public MovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param genreId Parameter 1.
     * @return A new instance of fragment MovieSerieFragment.
     */
    public static MovieFragment newInstance(int genreId) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GENRE_ID, genreId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MovieSerieFragment.
     */
    public static MovieFragment newInstance() {
        MovieFragment fragment = new MovieFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FunnyApi api = ((FunnyGuideApp)getActivity().getApplication()).getFunnyApi();
        presenter = new MoviePresenterImp(this, api);

        presenter.verifyArgs(getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_serie, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        configRecycler();
        presenter.onViewCreated();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        presenter.onDestroyView();
    }

    private void configRecycler() {
        movieGridAdapter =  new MovieGridAdapter(this, presenter);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        movieRecyclerView.setAdapter(movieGridAdapter);

        movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                presenter.verifyScrolled(movieRecyclerView.getLayoutManager().getChildCount()
                        , movieRecyclerView.getLayoutManager().getItemCount()
                        , ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()
                        , dx
                        , dy);
            }
        });
    }

    @Override
    public void changeActivity(Movie movie, SimpleDraweeView image){
        Intent intent = new Intent(getContext(), DetailItemActivity.class);
        intent.putExtra(DetailItemActivity.MOVIE_ITEM, movie);

        if (false/*Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP*/) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(getActivity()
                            , (View)image
                            , getString(R.string.poster_transition));

            startActivity(intent, options.toBundle());
        } else
            startActivity(intent);
    }

    @Override
    public void onItemList(List<Movie> results) {
        movieGridAdapter.addList(results);
    }

    @Override
    public void onError(String error) {
        Snackbar snackbar = Snackbar.make(coordinatorLayoutMovie, error, Snackbar.LENGTH_LONG);
        snackbar.show();

        movieGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete() {
        movieGridAdapter.notifyDataSetChanged();
    }
}
