package com.victorldavila.funnyguide.view.fragments;

import com.victorldavila.funnyguide.models.ResponseTv;

public interface InfoTvFragmentView extends InfoItemFragmentView{
  ResponseTv getResponseTv();

  void setLastAirDate(String lastAirDate);
  void setNumberOfEpisodes(int numberOfEpisodes);
  void setNumberOfSeason(int numberOfSeason);
}
