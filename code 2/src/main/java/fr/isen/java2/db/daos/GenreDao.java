package fr.isen.java2.db.daos;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres() {
        List<Genre> genres = new ArrayList<>(); //
        String sql = "SELECT * FROM genre"; // Ma première requête ici qui va stocker tous les genres

        // Tentative d'ouverture de la connexion et exécution de la requête
        try (Connection connection = DataSourceFactory.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Parcours du résultat et ajout des genres dans la liste
            while (resultSet.next()) {
                genres.add(new Genre(
                        resultSet.getInt("idgenre"), // Récupère l'ID du genre
                        resultSet.getString("name") // Récupère le nom du genre
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL si quelque chose ne fonctionne pas
        }

        return genres; // Retourne la liste des genres
    }

	public Genre getGenre(String name) {
        String sql = "SELECT * FROM genre WHERE name = ?"; // Requête SQL
        Genre genre = null; // Stockera le genre trouvé (ou null si aucun trouvé)

        // Ouverture de la connexion et exécution de la requête
        try (Connection connection = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name); // Assigne le paramètre "name" à la requête SQL

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) { // Si un genre est trouvé
                    genre = new Genre(
                            resultSet.getInt("idgenre"), // Récupère l'ID
                            resultSet.getString("name") // Récupère le nom
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL si problème
        }

        return genre; // Retourne le genre trouvé (ou null si aucun genre ne correspond)
    }

	public void addGenre(String name) {
        String sql = "INSERT INTO genre(name) VALUES(?)"; // Requête SQL d'insertion

        // Ouverture de la connexion et exécution de la requête
        try (Connection connection = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name); // Assigne le nom du genre à la requête SQL
            statement.executeUpdate(); // Exécute l'insertion

        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL si problème
        }
    }
}
