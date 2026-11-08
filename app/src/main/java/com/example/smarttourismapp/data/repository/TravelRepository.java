package com.example.smarttourismapp.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiErrorParser;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.FavoriteItem;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.model.Province;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelRepository {
    private final ApiService apiService;

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public TravelRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public void getPlaces(Long categoryId, Long provinceId, String search, RepositoryCallback<List<Place>> callback) {
        getPlaces(categoryId, provinceId, search, null, null, null, null, null, callback);
    }

    public void getPlaces(
            Long categoryId,
            Long provinceId,
            String search,
            String sortBy,
            Float minRating,
            Boolean featured,
            Integer page,
            Integer perPage,
            RepositoryCallback<List<Place>> callback
    ) {
        apiService.getPlaces(categoryId, provinceId, search, sortBy, minRating, featured, page, perPage)
                .enqueue(new Callback<ApiResponse<List<Place>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<Place>>> call, @NonNull Response<ApiResponse<List<Place>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            callback.onSuccess(response.body().getData());
                        } else {
                            callback.onError(ApiErrorParser.parseMessage(response));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<Place>>> call, @NonNull Throwable t) {
                        callback.onError(ApiErrorParser.parseThrowable(t));
                    }
                });
    }

    public void getPlaceDetails(Long id, RepositoryCallback<Place> callback) {
        apiService.getPlace(id).enqueue(new Callback<ApiResponse<Place>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Place>> call, @NonNull Response<ApiResponse<Place>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Place>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void getCategories(RepositoryCallback<List<Category>> callback) {
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void getProvinces(RepositoryCallback<List<Province>> callback) {
        apiService.getProvinces().enqueue(new Callback<ApiResponse<List<Province>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Province>>> call, @NonNull Response<ApiResponse<List<Province>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Province>>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void getFavorites(RepositoryCallback<List<Place>> callback) {
        apiService.getFavorites().enqueue(new Callback<ApiResponse<List<FavoriteItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<FavoriteItem>>> call, @NonNull Response<ApiResponse<List<FavoriteItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<FavoriteItem> items = response.body().getData();
                    List<Place> places = new ArrayList<>();
                    if (items != null) {
                        for (FavoriteItem item : items) {
                            if (item.getPlace() != null) {
                                places.add(item.getPlace());
                            }
                        }
                    }
                    callback.onSuccess(places);
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<FavoriteItem>>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void getFavoriteItems(RepositoryCallback<List<FavoriteItem>> callback) {
        apiService.getFavorites().enqueue(new Callback<ApiResponse<List<FavoriteItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<FavoriteItem>>> call, @NonNull Response<ApiResponse<List<FavoriteItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<FavoriteItem>>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void toggleFavorite(Long placeId, RepositoryCallback<Void> callback) {
        apiService.toggleFavorite(placeId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void removeFavorite(Long placeId, RepositoryCallback<Void> callback) {
        apiService.removeFavorite(placeId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void toggleVisited(Long favoriteId, RepositoryCallback<Void> callback) {
        apiService.toggleVisited(favoriteId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }
}
