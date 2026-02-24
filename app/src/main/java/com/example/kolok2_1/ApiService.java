package com.example.kolok2_1;

import java.util.List;
//bitno dodati dependece
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface ApiService {

    @GET("products")
    Call<List<Proizvod>> getProizvodi();

    @POST("products")
    Call<Proizvod> addProizvod(@Body Proizvod proizvod);
}