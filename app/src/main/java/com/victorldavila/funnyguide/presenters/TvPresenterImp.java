package com.victorldavila.funnyguide.presenters;

import com.victorldavila.funnyguide.api.ErrorHandler;
import com.victorldavila.funnyguide.models.NetWorkError;
import com.victorldavila.funnyguide.models.ResponseListItem;
import com.victorldavila.funnyguide.models.ResponseTv;
import com.victorldavila.funnyguide.repository.TvRepository;
import com.victorldavila.funnyguide.view.fragments.TvFragmentView;

import rx.Subscription;

public class TvPresenterImp extends BaseRxPresenter implements FragmentPresenter<TvFragmentView> {
  private TvRepository tvRepository;
  private TvFragmentView view;

  private Subscription tvSubscription;

  private int page;

  public TvPresenterImp(TvRepository tvRepository){
        this.tvRepository = tvRepository;
    }

  @Override
  public void addView(TvFragmentView view) {
    this.view = view;
  }

  @Override
  public void onViewCreated() {
    initPage();

    if (view != null) {
      getTvTopRated();
    }
  }

  @Override
  public void onDestroyView() {
        rxUnSubscribe();
    }

  private void initPage() {
        this.page = 1;
    }

  public void getTvTopRated(){
    rxUnSubscribe(tvSubscription);

    if(tvRepository != null) {
      tvSubscription = tvRepository.getTvTopRated(page)
        .doOnError(throwable -> view.setLoadRecycler(false))
        .doOnCompleted(() -> view.setLoadRecycler(false))
        .subscribe(
          this::resultGetTvRated,
          this::errorGetTvRated
        );

      addSubscription(tvSubscription);
    }
  }

  private void errorGetTvRated(Throwable throwable) {
    NetWorkError error = ErrorHandler.parseError(throwable);
    view.onError(error.getStatus_message());
  }

  private void resultGetTvRated(ResponseListItem<ResponseTv> tvResponseListItem) {
    view.onItemList(tvResponseListItem.getResults());
    countPage();
  }

  public void countPage() {
        page++;
    }

  public void verifyScroll(int visibleItemCount, int totalItemCount, int pastVisiblesItems, int dy) {
    if(dy > 0)
      if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
        getTvTopRated();
  }
}
