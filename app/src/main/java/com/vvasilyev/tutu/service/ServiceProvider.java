package com.vvasilyev.tutu.service;

import android.content.Context;

import com.vvasilyev.tutu.ui.ToastManager;

/**
 *
 */
public class ServiceProvider {

    private SearchService searchService;
    
    private ToastManager toastManager;

    public SearchService provideSearchService(Context context) {
        if (this.searchService == null) {
            this.searchService = new SearchService(context);
        }
        return this.searchService;
    }
    
    public ToastManager provideToastManager() {
        if (this.toastManager == null) {
            this.toastManager = new ToastManager();
        }
        return this.toastManager;
    }

    public void use(SearchService searchService) {
        this.searchService = searchService;
    }

    public void use(ToastManager toastManager) {
        this.toastManager = toastManager;
    }

    public static ServiceProvider instance() {
        return ServiceProviderHolder.serviceProvider;
    }

    private static class ServiceProviderHolder {

        static ServiceProvider serviceProvider = new ServiceProvider();
    }
}
