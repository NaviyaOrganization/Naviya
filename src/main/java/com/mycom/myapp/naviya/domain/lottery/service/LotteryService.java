package com.mycom.myapp.naviya.domain.lottery.service;

import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;

public interface LotteryService {

    String submitLotteryEntry(LotteryEntryRequest lotteryEntryRequest);

    void processLotteryResults();
}
