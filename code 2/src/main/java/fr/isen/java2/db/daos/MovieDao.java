package fr.isen.java2.db.daos;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import fr.isen.java2.db.entities.Genre;

import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	public List<Movie> listMovies() {
    List<Movie> movies = new ArrayList<>(); // Crée une liste pour stocker les films avec leurs genres
    String sql = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre"; // Requête SQL mise à jour pour joindre les films et les genres

    try (Connection connection = DataSourceFactory.getDataSource().getConnection();
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

        while (resultSet.next()) { // Pour chaque film dans la base de données on récuperer le genre du film et on ajoute à la même ligne le film et son genre associé
            Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name")); 
            Movie movie = new Movie(
                resultSet.getInt("idmovie"), 
                resultSet.getString("title"), 
                resultSet.getDate("release_date").toLocalDate(), 
                genre, 
                resultSet.getInt("duration"), 
                resultSet.getString("director"), 
                resultSet.getString("summary") 
            );
            movies.add(movie); // Ajoute le film à la nouvelle liste
        }

    } catch (SQLException e) {
        e.printStackTrace(); // Affiche une erreur si une exception SQL se produit
    }

    return movies; // Retourne la liste des films
}


	public List<Movie> listMoviesByGenre(String genreName) {
    List<Movie> movies = new ArrayList<>(); // Liste ou on va stocker les films filtré par un genre
    String sql = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?"; // Requête SQL avec le filtre sur le genre

    try (Connection connection = DataSourceFactory.getDataSource().getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, genreName); // Remplace le "?" par le genre passé en paramètre pour rendre la requête dynamique

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) { // Pour chaque film correspondant au genre
                Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));
                Movie movie = new Movie(
                    resultSet.getInt("idmovie"),
                    resultSet.getString("title"),
                    resultSet.getDate("release_date").toLocalDate(),
                    genre,
                    resultSet.getInt("duration"),
                    resultSet.getString("director"),
                    resultSet.getString("summary")
                );
                movies.add(movie); // Ajoute le film trié par genre à la liste
            }
        }

    } catch (SQLException e) {
        e.printStackTrace(); 
    }

    return movies; // Retourne la liste des films du genre donné
}


	public Movie addMovie(Movie movie) {
    String sql = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES(?, ?, ?, ?, ?, ?)"; 

    try (Connection connection = DataSourceFactory.getDataSource().getConnection();
         PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
// ici on va associer chaque paramètres des getters des movies pour les utiliser dans la requête sql d'ajout
        statement.setString(1, movie.getTitle()); 
        statement.setDate(2, java.sql.Date.valueOf(movie.getReleaseDate())); 
        statement.setInt(3, movie.getGenre().getId()); 
        statement.setInt(4, movie.getDuration()); 
        statement.setString(5, movie.getDirector()); 
        statement.setString(6, movie.getSummary()); 

        statement.executeUpdate(); // Exécute la requête d'insertion

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) { // Récupère l'ID généré
            if (generatedKeys.next()) {
                movie.setId(generatedKeys.getInt(1)); // Définit l'ID du film inséré
            }
        }

    } catch (SQLException e) {
        e.printStackTrace(); // Affiche une erreur si une exception SQL se produit
    }

    return movie; // Retourne l'objet film avec son nouvel ID
}

}
