package me.elaineqheart.auctionHouse.pluginDependencies;

import net.milkbowl.vault2.economy.EconomyResponse;

public final class MyEconomyResponse {

    private EconomyResponse response;
    private net.milkbowl.vault.economy.EconomyResponse legacyResponse;
    private final boolean useLegacy;

    public MyEconomyResponse(EconomyResponse response) {
        this.response = response;
        useLegacy = false;
    }

    public MyEconomyResponse(net.milkbowl.vault.economy.EconomyResponse response) {
        this.legacyResponse = response;
        useLegacy = true;
    }

    public boolean transactionSuccess() {
        if (useLegacy) {
            return legacyResponse.transactionSuccess();
        } else {
            return response.transactionSuccess();
        }
    }

}
