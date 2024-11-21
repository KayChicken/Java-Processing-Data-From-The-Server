package ru.mirea.kainov.rickmorty;

import java.io.IOException;
import java.util.Comparator;

import com.fasterxml.jackson.databind.json.JsonMapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
                .addConverterFactory(JacksonConverterFactory.create(new JsonMapper()))
                .build();

        RickAndMortyApi api = retrofit.create(RickAndMortyApi.class);
        api.getEpisodes().enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Episode maxEpisode = response.body().getEpisodes().stream()
                            .max(Comparator.comparingInt(e -> e.getCharacters().size()))
                            .orElse(null);

                    if (maxEpisode != null) {
                        System.out.println("Эпизод с максимальным количеством персонажей: " + maxEpisode.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Thread.sleep(5000);
    }
}
