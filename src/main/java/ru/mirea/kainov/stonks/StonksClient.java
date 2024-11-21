package ru.mirea.kainov.stonks;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class StonksClient {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        Retrofit client = new Retrofit.Builder()
                .baseUrl("https://www.cbr.ru")
                .addConverterFactory(JacksonConverterFactory.create(new XmlMapper()))
                .build();
        StonksService stonksService = client.create(StonksService.class);
        Response<DailyCurs> response = stonksService
                .getDailyCurs(LocalDate.of(2002, 11, 25)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .execute();
        DailyCurs dailyCurs = response.body();

        Optional<Valute> maxValute = dailyCurs.getValutes().stream()
                .filter(valute -> !valute.getName().equals("СДР (специальные права заимствования)"))
                .max(Comparator.comparingDouble(Valute::getValue));
        DatabaseService databaseService = new DatabaseServiceImpl();
        if (maxValute.isPresent()) {
            System.out.println(maxValute.get());

            Valute mv = maxValute.get();

            databaseService.saveMaxValuteOfDate("кайновда", mv, LocalDate.now());
        }
        Valute valute = databaseService.getValuteOfDate(LocalDate.now());
    }
}
