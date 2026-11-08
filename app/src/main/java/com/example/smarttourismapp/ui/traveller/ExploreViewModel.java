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

public class ExploreViewModel extends AndroidViewModel {

    private final TravelRepository travelRepository;

    private final MutableLiveData<Resource<List<Place>>> placesState = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Category>>> categoriesState = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Province>>> provincesState = new MutableLiveData<>();

    private Long selectedCategoryId = null;
    private Long selectedProvinceId = null;
    private String searchQuery = null;
    private String sortBy = "popular";
    private Float minRating = null;

    public ExploreViewModel(@NonNull Application application) {
        super(application);
        this.travelRepository = new TravelRepository(application);
    }

    public LiveData<Resource<List<Place>>> getPlacesState() {
        return placesState;
    }

    public LiveData<Resource<List<Category>>> getCategoriesState() {
        return categoriesState;
    }

    public LiveData<Resource<List<Province>>> getProvincesState() {
        return provincesState;
    }

    public void setCategoryFilter(Long categoryId) {
        this.selectedCategoryId = categoryId;
        loadPlaces();
    }

    public void setProvinceFilter(Long provinceId) {
        this.selectedProvinceId = provinceId;
        loadPlaces();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        loadPlaces();
    }

    public void setSortBy(String sort) {
        this.sortBy = sort;
        loadPlaces();
    }

    public void setMinRating(Float rating) {
        this.minRating = rating;
        loadPlaces();
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

    public void loadPlaces() {
        placesState.setValue(Resource.loading());
        travelRepository.getPlaces(selectedCategoryId, selectedProvinceId, searchQuery, sortBy, minRating, null, 1, 30, new TravelRepository.RepositoryCallback<List<Place>>() {
            @Override
            public void onSuccess(List<Place> result) {
                placesState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                placesState.setValue(Resource.error(errorMessage));
            }
        });
    }
}

