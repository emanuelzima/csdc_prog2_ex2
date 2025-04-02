package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public TextField releaseYearField;

    @FXML
    public TextField ratingField;

    @FXML
    public JFXListView movieListView;

    @FXML
    public JFXComboBox genreComboBox;

    @FXML
    public JFXButton sortBtn;

    @FXML
    public JFXButton clearFiltersBtn;

    public List<Movie> allMovies = Movie.initializeMovies();

    public final ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    public SortedState sortedState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeState();

        movieListView.setItems(observableMovies);
        movieListView.setCellFactory(listView -> new MovieCell());

        genreComboBox.setPromptText("Filter by Genre");
        genreComboBox.getItems().addAll(Genre.values());        // fügt die genres in die combobox hinzu

        //event handlers
        searchBtn.setOnAction(event -> filterMovies() );
        sortBtn.setOnAction(event -> sortMovies());
        clearFiltersBtn.setOnAction(event -> initializeState());
    }

    public void initializeState() {
        observableMovies.clear();
        observableMovies.addAll(allMovies);
        sortedState = SortedState.NONE;
        if (sortBtn != null) {
            sortBtn.setText("Sort (asc)");
        }
    }

    public void filterMovies() {
        String query = searchField.getText();
        Genre genre = (Genre) genreComboBox.getValue();
        String releaseYear = releaseYearField.getText();
        String rating = ratingField.getText();
        applyAllFilters(query, genre, releaseYear, rating);
    }

    public void applyAllFilters(String query, Genre genre, String releaseYear, String rating) {
        int releaseYearInt = 0;
        double ratingDouble = 0.0;

        if (releaseYear != null && !releaseYear.trim().isEmpty()) {
            try {
                releaseYearInt = Integer.parseInt(releaseYear.trim());
            } catch (NumberFormatException e) {
                releaseYearInt = 0;
            }
        }

        if (rating != null && !rating.trim().isEmpty()) {
            try {
                ratingDouble = Double.parseDouble(rating.trim());
            } catch (NumberFormatException e) {
                ratingDouble = 0.0;
            }
        }

        List<Movie> filtered = filterByGenre(allMovies, genre);
        filtered = filterByQuery(filtered, query);
        filtered = filterByRelease(filtered, releaseYearInt);
        filtered = filterByRating(filtered, ratingDouble);

        observableMovies.setAll(filtered);
    }

    public List<Movie> filterByQuery(List<Movie> movies, String query) {
        if (movies == null) {
            throw new IllegalArgumentException("Movie list cannot be null");
        }
        if (query == null || query.trim().isEmpty()) {
            return movies;
        }
        String lowerQuery = query.toLowerCase().trim();
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getTitle().toLowerCase().contains(lowerQuery) || movie.getDescription().toLowerCase().contains(lowerQuery))
            {
                filtered.add(movie);
            }
        }
        return filtered;
    }

    public List<Movie> filterByGenre(List<Movie> movies, Genre genre) {
        if (movies == null) {
            throw new IllegalArgumentException("Movie list cannot be null");
        }
        if (genre == null) {
            return movies;
        }
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getGenres().contains(genre)) {
                filtered.add(movie);
            }
        }
        return filtered;
    }

    public List<Movie> filterByRelease(List<Movie> movies, int releaseYear) {
        if (releaseYear <= 0) {
            return movies;
        }
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getReleaseYear() == releaseYear) {
                filtered.add(movie);
            }
        }
        return filtered;
    }

    public List<Movie> filterByRating(List<Movie> movies, double rating) {
        if (rating <= 0) {
            return movies;
        }
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getRating() >= rating) {
                filtered.add(movie);
            }
        }
        return filtered;
    }

    public void sortMovies() {
        if (sortedState == SortedState.NONE || sortedState == SortedState.DESCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle));
            sortedState = SortedState.ASCENDING;
            if (sortBtn != null) {
                sortBtn.setText("Sort (desc)");
            }
        } else {
            observableMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
            sortedState = SortedState.DESCENDING;
            if (sortBtn != null) {
                sortBtn.setText("Sort (asc)");
            }
        }
    }

    public void setMovieList(List<Movie> movies) {
        if (movies == null) {
            throw new IllegalArgumentException("Movie list cannot be null");
        }
        this.allMovies = movies;
        observableMovies.setAll(movies);
    }
}