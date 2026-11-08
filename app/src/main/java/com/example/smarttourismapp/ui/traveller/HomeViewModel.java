package com.example.smarttourismapp.ui.traveller;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarttourismapp.data.remote.Resource;
import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.model.Province;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final TravelRepository travelRepository;

    private final MutableLiveData<Resource<List<Category>>> categoriesState = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Province>>> provincesState = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Place>>> featuredPlacesState = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Place>>> popularPlacesState = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.travelRepository = new TravelRepository(application);
    }

    public LiveData<Resource<List<Category>>> getCategoriesState() {
        return categoriesState;
    }

    public LiveData<Resource<List<Province>>> getProvincesState() {
        return provincesState;
    }

    public LiveData<Resource<List<Place>>> getFeaturedPlacesState() {
        return featuredPlacesState;
    }

    public LiveData<Resource<List<Place>>> getPopularPlacesState() {
        return popularPlacesState;
    }

    public void loadHomeData() {
        loadCategories();
        loadProvinces();
        loadFeaturedPlaces();
        loadPopularPlaces();
    }

    public void loadCategories() {
        categoriesState.setValue(Resource.loading());
        travelRepository.getCategories(new TravelRepository.RepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                categoriesState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                categoriesState.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadProvinces() {
        provincesState.setValue(Resource.loading());
        travelRepository.getProvinces(new TravelRepository.RepositoryCallback<List<Province>>() {
            @Override
            public void onSuccess(List<Province> result) {
                provincesState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                provincesState.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadFeaturedPlaces() {
        featuredPlacesState.setValue(Resource.loading());
        travelRepository.getPlaces(null, null, null, null, null, true, 1, 6, new TravelRepository.RepositoryCallback<List<Place>>() {
            @Override
            public void onSuccess(List<Place> result) {
                featuredPlacesState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                featuredPlacesState.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadPopularPlaces() {
        popularPlacesState.setValue(Resource.loading());
        travelRepository.getPlaces(null, null, null, "popular", null, null, 1, 10, new TravelRepository.RepositoryCallback<List<Place>>() {
            @Override
            public void onSuccess(List<Place> result) {
                popularPlacesState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                popularPlacesState.setValue(Resource.error(errorMessage));
            }
        });
    }
}

