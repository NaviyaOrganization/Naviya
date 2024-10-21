package com.mycom.myapp.naviya.domain.lottery.service;

import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;

import java.util.List;

public interface LotteryService {

    String submitLotteryEntry(LotteryEntryRequest lotteryEntryRequest);

    void processLotteryResults();

    List<String> getCachedMaskedEntries();
}
