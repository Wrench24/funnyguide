package com.victorldavila.funnyguide.view.presenters.interactors;

import com.victorldavila.funnyguide.api.FunnyApi;
import com.victorldavila.funnyguide.models.Movie;
import com.victorldavila.funnyguide.models.ResponseListItem;
import com.victorldavila.funnyguide.models.Tv;
import com.victorldavila.funnyguide.view.OnViewListener;
import com.victorldavila.funnyguide.view.presenters.MoviePresenter;
import com.victorldavila.funnyguide.view.presenters.TvPresenter;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by victo on 18/12/2016.
 */

public class TvInteractor {

    private OnViewListener<Tv> view;
    private final TvPresenter presenter;
    private FunnyApi api;
    private Realm realm;

    private boolean isLoad;

    public TvInteractor(OnViewListener<Tv> view, TvPresenter presenter, FunnyApi api) {
        this.view = view;
        this.presenter = presenter;
        this.api = api;

        isLoad = true;
    }

    public void bind(){
        //realm = Realm.getDefaultInstance();
    }

    public void unbind(){
        //realm.close();
    }

    public Subscription getTvTopRated(final int page){
        Observable<ResponseListItem<Tv>> genreResponseObservable = (Observable<ResponseListItem<Tv>>)api.getPreparedObservable(api.getAPI()
                        .getSeriesTopRateObservable(api.getQueryStringList(page))
                , Tv.class
                , true
                , true);

        return genreResponseObservable.subscribe(new Observer<ResponseListItem<Tv>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isLoad = false;

                view.onError(e.getMessage());
            }

            @Override
            public void onNext(ResponseListItem<Tv> movieResponseListItem) {
                if(movieResponseListItem.getResults() != null) {
                    if (movieResponseListItem.getResults().size() == 0 || page == movieResponseListItem.getTotal_pages())
                        isLoad = false;
                    else
                        presenter.countPage();

                    view.onItemList(movieResponseListItem.getResults());
                } else{
                    view.onError("Results null");
                }
            }
        });
    }

    public boolean isLoad() {
        return isLoad;
    }
}
