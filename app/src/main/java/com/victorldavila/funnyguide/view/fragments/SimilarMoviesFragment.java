package com.victorldavila.funnyguide.view.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.victorldavila.funnyguide.FunnyGuideApp;
import com.victorldavila.funnyguide.R;
import com.victorldavila.funnyguide.adapter.MovieGridAdapter;
import com.victorldavila.funnyguide.api.FunnyApi;
import com.victorldavila.funnyguide.models.ResponseMovie;
import com.victorldavila.funnyguide.presenters.SimilarMoviePresenterImp;
import com.victorldavila.funnyguide.repository.MovieRepositoryImp;
import com.victorldavila.funnyguide.view.activities.DetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SimilarMoviesFragment extends Fragment implements SimilarMoviesFragmentView{
  @BindView(R.id.recycler_movie_item)
  RecyclerView movieRecyclerView;
  @BindView(R.id.coordinator_layout_movie)
  CoordinatorLayout coordinatorLayoutMovie;

  private MovieGridAdapter movieGridAdapter;
  private SimilarMoviePresenterImp presenter;
  private Unbinder unbinder;

  private int movieId;

  public SimilarMoviesFragment() { }

  public static SimilarMoviesFragment newInstance(int movieId) {
    SimilarMoviesFragment fragment = new SimilarMoviesFragment();
    Bundle args = new Bundle();
    args.putInt(ReviewsFragment.ARG_MOVIE_ID, movieId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    createReenterTransition();

    super.onCreate(savedInstanceState);

    getExtras();
    configPresenter();
  }

  private void configPresenter() {
    FunnyApi api = ((FunnyGuideApp)getActivity().getApplication()).getFunnyApi();

    presenter = new SimilarMoviePresenterImp(new MovieRepositoryImp(api));
    presenter.addView(this);
  }

  private void getExtras() {
    if (getArguments() != null) {
      movieId = getArguments().getInt(ReviewsFragment.ARG_MOVIE_ID);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    movieGridAdapter =  new MovieGridAdapter();
    movieGridAdapter.setMainAction((responseMovie, simpleDraweeView) -> changeActivity(responseMovie, simpleDraweeView));
    movieRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    movieRecyclerView.setAdapter(movieGridAdapter);

    movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        presenter.verifyScrolled(movieRecyclerView.getLayoutManager().getChildCount()
            , movieRecyclerView.getLayoutManager().getItemCount()
            , ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()
            , dy);
      }
    });
  }

  @Override
  public void changeActivity(ResponseMovie responseMovie, SimpleDraweeView image) {
    Intent intent = new Intent(getContext(), DetailActivity.class);
    intent.putExtra(DetailActivity.MOVIE_ITEM, responseMovie);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ActivityOptionsCompat options = ActivityOptionsCompat
          .makeSceneTransitionAnimation(getActivity()
              , image
              , getString(R.string.poster_transition));

      startActivity(intent, options.toBundle());
    } else
      startActivity(intent);
  }

  @Override
  public void setLoadRecycler(boolean isLoad) {
    movieGridAdapter.setLoad(isLoad);
    movieGridAdapter.notifyDataSetChanged();
  }

  @Override
  public int getMovieId() {
    return movieId;
  }

  @Override
  public void onItemList(List<ResponseMovie> results) {
    movieGridAdapter.addList(results);
  }

  @Override
  public void onError(String error) {
    Snackbar snackbar = Snackbar.make(coordinatorLayoutMovie, error, Snackbar.LENGTH_LONG);
    snackbar.show();
  }

  private void createReenterTransition() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      //configReenterTransition();
      //configExitTransition();

      configSharedElementExit();
      configSharedElementReenter();
    }
  }

  private void configSharedElementReenter() {
    getActivity()
        .getWindow()
        .setSharedElementReenterTransition(DraweeTransition
            .createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP,
                ScalingUtils.ScaleType.CENTER_CROP));
  }

  private void configSharedElementExit() {
    getActivity()
        .getWindow()
        .setSharedElementExitTransition(DraweeTransition
            .createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP,
                ScalingUtils.ScaleType.CENTER_CROP));
  }

  /*private void configExitTransition() {
    getActivity()
        .getWindow()
        .setExitTransition(new Fade()
            .setDuration(300)
            .excludeTarget(R.id.toolbar, true)
            .excludeTarget(R.id.collapsingToolbarLayout, true)
            .excludeTarget(android.R.id.statusBarBackground, true)
            .excludeTarget(android.R.id.navigationBarBackground, true));
  }

  private void configReenterTransition() {
    getActivity()
        .getWindow()
        .setReenterTransition(new Fade()
            .setDuration(300)
            .excludeTarget(R.id.toolbar, true)
            .excludeTarget(R.id.collapsingToolbarLayout, true)
            .excludeTarget(android.R.id.statusBarBackground, true)
            .excludeTarget(android.R.id.navigationBarBackground, true));
  }*/

  @Override
  public void startActivity(Intent intent) {
    setExitSharedElementCallback(
        new SharedElementCallback() {
          @Override
          public void onSharedElementEnd(List<String> names, List<View> elements, List<View> snapshots) {
            super.onSharedElementEnd(names, elements, snapshots);
            movieGridAdapter.notifyDataSetChanged();
          }
        }
    );
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    if (isVisibleToUser && movieGridAdapter != null) {
      movieGridAdapter.notifyDataSetChanged();
    }
  }
}
