package fr.isen.java2.db;

import fr.isen.java2.db.daos.MovieDao;
import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MovieDao movieDao = new MovieDao();

        
        Movie newMovie = new Movie(
            "Interstellar",
            LocalDate.of(2014, 11, 5),
            new Genre(1, "Science-Fiction"),  
            169,
            "Christopher Nolan",
            "Des explorateurs voyagent à travers un trou de ver pour trouver une nouvelle planète habitable."
        );

        Movie addedMovie = movieDao.addMovie(newMovie);
        System.out.println("Film ajouté : " + addedMovie.getId() + " - " + addedMovie.getTitle());

        // 2️⃣ Test : Lister tous les films
        List<Movie> movies = movieDao.listMovies();
        System.out.println("\nListe des films en base :");
        for (Movie movie : movies) {
            System.out.println(movie.getId() + " - " + movie.getTitle() + " (" + movie.getGenre().getName() + ")");
        }

        // 3️⃣ Test : Lister les films par genre
        String genreTest = "Science-Fiction";  // Mets un genre qui existe en base
        List<Movie> sciFiMovies = movieDao.listMoviesByGenre(genreTest);
        System.out.println("\nFilms du genre " + genreTest + " :");
        for (Movie movie : sciFiMovies) {
            System.out.println(movie.getId() + " - " + movie.getTitle());
        }
    }
}
