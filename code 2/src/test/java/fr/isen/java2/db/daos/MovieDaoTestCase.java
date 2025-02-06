package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.fail;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.isen.java2.db.entities.Movie;
import fr.isen.java2.db.entities.Genre;
import java.time.LocalDate;



public class MovieDaoTestCase {

    private MovieDao movieDao = new MovieDao();
	
    @BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	 @Test
public void shouldListMovies() throws Exception {
    // WHEN: Appel de la méthode listMovies() pour récupérer tous les films
    List<Movie> movies = movieDao.listMovies();

    // THEN: Vérification que la liste contient les films attendus
    assertThat(movies).hasSize(3); // Il y a 3 films dans la base de données
    assertThat(movies).extracting("title").containsExactly("Title 1", "My Title 2", "Third title"); // Vérifie les titres
    assertThat(movies).extracting("genre.name").containsExactly("Drama", "Comedy", "Comedy"); // Vérifie les genres associés
}

	
	 @Test
public void shouldListMoviesByGenre() throws Exception {
    
    List<Movie> movies = movieDao.listMoviesByGenre("Comedy");

    // THEN: Vérification que les films retournés sont bien du genre "Comedy"
    assertThat(movies).hasSize(2); // Il y a 2 films dans le genre "Comedy"
    assertThat(movies).extracting("title").containsExactly("My Title 2", "Third title"); // Vérifie les titres des films
    assertThat(movies).extracting("genre.name").containsOnly("Comedy"); // Vérifie que les films ont le genre "Comedy"
}

	
	 @Test
public void shouldAddMovie() throws Exception {
    // GIVEN: Création d'un film à ajouter
    Movie newMovie = new Movie("New Movie", LocalDate.of(2025, 1, 1), new Genre(1, "Drama"), 130, "New Director", "New summary");

    // WHEN: Appel de la méthode addMovie() pour ajouter ce film
    Movie addedMovie = movieDao.addMovie(newMovie);

    // THEN: Vérification que le film a bien été ajouté
    Connection connection = DataSourceFactory.getDataSource().getConnection();
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT * FROM movie WHERE title='New Movie'");

    // Vérifie que le film existe dans la base de données
    assertThat(resultSet.next()).isTrue();
    assertThat(resultSet.getString("title")).isEqualTo("New Movie");
    assertThat(resultSet.getInt("duration")).isEqualTo(130);
    assertThat(resultSet.getString("director")).isEqualTo("New Director");
    assertThat(resultSet.getString("summary")).isEqualTo("New summary");
    assertThat(resultSet.getInt("genre_id")).isEqualTo(1); // Le genre doit être "Drama"

    // Vérifie qu'il n'y a pas d'autres résultats
    assertThat(resultSet.next()).isFalse();

    resultSet.close();
    stmt.close();
    connection.close();
}

}
