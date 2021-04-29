/*
 * Copyright (C) 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.android.trivialdrivesample;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import static com.sample.android.trivialdrivesample.TrivialDriveRepository.SKU_GAS;
import static com.sample.android.trivialdrivesample.TrivialDriveRepository.SKU_INFINITE_GAS_MONTHLY;
import static com.sample.android.trivialdrivesample.TrivialDriveRepository.SKU_INFINITE_GAS_YEARLY;
import static com.sample.android.trivialdrivesample.TrivialDriveRepository.SKU_PREMIUM;

/*
    This is used for any business logic, as well as to echo LiveData from the BillingRepository.
 */
public class MakePurchaseViewModel extends ViewModel {
    static final String TAG = "TrivialDrive:" + MakePurchaseViewModel.class.getSimpleName();
    private final TrivialDriveRepository tdr;
    static private Map<String, Integer> skuToResourceIdMap = new HashMap<>();
    static {
        skuToResourceIdMap.put(SKU_GAS, R.drawable.buy_gas);
        skuToResourceIdMap.put(SKU_PREMIUM, R.drawable.upgrade_app);
        skuToResourceIdMap.put(SKU_INFINITE_GAS_MONTHLY, R.drawable.get_infinite_gas);
        skuToResourceIdMap.put(SKU_INFINITE_GAS_YEARLY, R.drawable.get_infinite_gas);
    }
    public MakePurchaseViewModel(@NonNull TrivialDriveRepository trivialDriveRepository) {
        super();
        tdr = trivialDriveRepository;
    }

    static public class SkuDetails {
        final public String sku;
        final public LiveData<String> title;
        final public LiveData<String> description;
        final public LiveData<String> price;
        final public int iconDrawableId;
        SkuDetails(@NonNull String sku, TrivialDriveRepository tdr) {
            this.sku = sku;
            title = tdr.getSkuTitle(sku);
            description = tdr.getSkuDescription(sku);
            price = tdr.getSkuPrice(sku);
            iconDrawableId = skuToResourceIdMap.get(sku);
        }
    }

    public SkuDetails getSkuDetails(String sku) {
        return new SkuDetails(sku, tdr);
    }

    public LiveData<Boolean> canBuySku(String sku) {
        return tdr.canPurchase(sku);
    }

    /**
     * Starts a billing flow for purchasing gas.
     * @param activity
     * @return whether or not we were able to start the flow
     */
    public boolean buySku(Activity activity, String sku){
        return tdr.buySku(activity, sku);
    }

    public LiveData<Boolean> getBillingFlowInProcess() { return tdr.getBillingFlowInProcess(); }

    public void sendMessage(int message) {
        tdr.sendMessage(message);
    }

    public static class MakePurchaseViewModelFactory implements
            ViewModelProvider.Factory {
        private final TrivialDriveRepository trivialDriveRepository;

        public MakePurchaseViewModelFactory(TrivialDriveRepository tdr) {
            trivialDriveRepository = tdr;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MakePurchaseViewModel.class))
                return (T) new MakePurchaseViewModel(trivialDriveRepository);
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
