package core;

import models.Movie;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class MovieDatabaseTests {
    private interface InternalTest {
        void execute();
    }

    private MovieDatabase movieDatabase;

    private Movie getRandomMovie() {
        return new Movie(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                (int) Math.min(1, Math.random() * 2_000),
                Math.min(1, Math.random() * 10),
                new ArrayList<>(IntStream.range(1, (int) Math.min(2, Math.random() * 10)).mapToObj(x -> UUID.randomUUID().toString()).collect(Collectors.toList())));
    }

    @Before
    public void setup() {
        this.movieDatabase = new MovieDatabaseImpl();
    }

    public void performCorrectnessTesting(InternalTest[] methods) {
        Arrays.stream(methods)
                .forEach(method -> {
                    this.movieDatabase = new MovieDatabaseImpl();

                    try {
                        method.execute();
                    } catch (IllegalArgumentException ignored) {
                    }
                });

        this.movieDatabase = new MovieDatabaseImpl();
    }

    // Correctness Tests

    @Test
    public void testAddMovie_WithCorrectData_ShouldSuccessfullyAddMovie() {
        this.movieDatabase.addMovie(this.getRandomMovie());
        this.movieDatabase.addMovie(this.getRandomMovie());

        assertEquals(2, this.movieDatabase.size());
    }

    @Test
    public void testContains_WithExistentMovie_ShouldReturnTrue() {
        Movie randomMovie = this.getRandomMovie();

        this.movieDatabase.addMovie(randomMovie);

        assertTrue(this.movieDatabase.contains(randomMovie));
    }

    @Test
    public void testContains_WithNonexistentMovie_ShouldReturnFalse() {
        Movie randomMovie = this.getRandomMovie();

        this.movieDatabase.addMovie(randomMovie);

        assertFalse(this.movieDatabase.contains(this.getRandomMovie()));
    }

    @Test
    public void testCount_With5Movies_ShouldReturn5() {
        this.movieDatabase.addMovie(this.getRandomMovie());
        this.movieDatabase.addMovie(this.getRandomMovie());
        this.movieDatabase.addMovie(this.getRandomMovie());
        this.movieDatabase.addMovie(this.getRandomMovie());
        this.movieDatabase.addMovie(this.getRandomMovie());

        assertEquals(5, this.movieDatabase.size());
    }

    @Test
    public void testCount_WithEmpty_ShouldReturnZero() {
        assertEquals(0, this.movieDatabase.size());
    }

    @Test
    public void testGetMoviesOrderedByMultiCriteria_WithCorrectData_ShouldReturnCorrectResults() {
        Movie Movie = new Movie("asd", "bsd", 2010, 5000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);

        List<Movie> list =
                StreamSupport.stream(this.movieDatabase.getAllMoviesOrderedByActorPopularityThenByRatingThenByYear().spliterator(), false)
                        .collect(Collectors.toList());

        assertEquals(5, list.size());
        assertEquals(Movie, list.get(0));
        assertEquals(Movie5, list.get(1));
        assertEquals(Movie4, list.get(2));
        assertEquals(Movie2, list.get(3));
        assertEquals(Movie3, list.get(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeMovie_shouldThrowException() {
        Movie Movie = new Movie("asd", "bsd", 2010, 5000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);

        this.movieDatabase.removeMovie(UUID.randomUUID().toString());
    }

    @Test
    public void test_removeMovie_shouldRemoveSuccessfully() {
        Movie Movie = new Movie("asd", "bsd", 2010, 5000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);

        this.movieDatabase.removeMovie("hsd");
        Assert.assertEquals(4, this.movieDatabase.size());
        Assert.assertFalse(this.movieDatabase.contains(Movie3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getMoviesByActor_shouldThrowException_whenNoMoviesWithGivenActorAreFound() {
        Movie Movie = new Movie("asd", "bsd", 2010, 5000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);

        this.movieDatabase.getMoviesByActor(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getMoviesByActor_shouldThrowException_whenNoEntities() {
        this.movieDatabase.getMoviesByActor(UUID.randomUUID().toString());
    }

    @Test
    public void test_getMoviesByActor_shouldReturnSortedCollection() {
        Movie Movie = new Movie("asd", "bsd", 2010, 4000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));
        Movie Movie6 = new Movie("qsd", "msd", 2010, 4000, List.of("Sasho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);
        this.movieDatabase.addMovie(Movie6);

        String[] expected = {Movie5.getId(), Movie4.getId(), Movie.getId(), Movie6.getId()};

        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesByActor("Gosho");
        List<models.Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(4, movies.size());
        int counter = 0;
        for (Movie movie : movies) {
            Assert.assertEquals(expected[counter++], movie.getId());
        }
    }

    @Test
    public void test_getMoviesByYear_shouldReturnEmptyCollection_WhenNoEntities() {
        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesByYear(3000);
        List<models.Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, movies.size());
    }

    @Test
    public void test_getMoviesByYear_shouldReturnEmptyCollection_WhenMoviesWithGivenYearAreNotFound() {
        Movie Movie = new Movie("asd", "bsd", 2010, 4000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));
        Movie Movie6 = new Movie("qsd", "msd", 2010, 4000, List.of("Sasho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);
        this.movieDatabase.addMovie(Movie6);

        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesByYear(3000);
        List<Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, movies.size());
    }

    @Test
    public void test_getMoviesByYear_shouldReturnSortedCollection() {
        Movie Movie = new Movie("asd", "bsd", 2012, 4000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4500, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 5000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));
        Movie Movie6 = new Movie("qsd", "msd", 2012, 4000, List.of("Sasho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);
        this.movieDatabase.addMovie(Movie6);

        String[] expected = {Movie3.getId(), Movie2.getId(), Movie.getId(), Movie6.getId()};

        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesByYear(2012);
        List<Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(4, movies.size());
        int counter = 0;
        for (Movie movie : movies) {
            Assert.assertEquals(expected[counter++], movie.getId());
        }
    }

    @Test
    public void test_getMoviesInRatingRange_shouldReturnEmptyCollection_WhenNoEntities() {
        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesInRatingRange(1000, 2000);
        List<models.Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, movies.size());
    }

    @Test
    public void test_getMoviesInRatingRange_shouldReturnEmptyCollection_WhenNoMoviesInGivenRangeAreFound() {
        Movie Movie = new Movie("asd", "bsd", 2010, 4000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));
        Movie Movie6 = new Movie("qsd", "msd", 2010, 4000, List.of("Sasho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);
        this.movieDatabase.addMovie(Movie6);

        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesInRatingRange(2000, 3000);
        List<Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, movies.size());
    }

    @Test
    public void test_getMoviesInRatingRange_shouldReturnSortedCollection() {
        Movie Movie = new Movie("asd", "bsd", 2012, 4000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4500, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 5000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4750, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));
        Movie Movie6 = new Movie("qsd", "msd", 2012, 4000, List.of("Sasho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);
        this.movieDatabase.addMovie(Movie6);

        String[] expected = {Movie3.getId(), Movie4.getId(), Movie2.getId(), Movie5.getId()};

        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesInRatingRange(4500, 5000);
        List<Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(4, movies.size());
        int counter = 0;
        for (Movie movie : movies) {
            Assert.assertEquals(expected[counter++], movie.getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getMoviesByActors_shouldThrowException_whenNoMoviesWithGivenActorAreFound() {
        Movie Movie = new Movie("asd", "bsd", 2010, 5000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);

        this.movieDatabase.getMoviesByActors(List.of("Stamat", "Pesho"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getMoviesByActors_shouldThrowException_whenNoEntities() {
        this.movieDatabase.getMoviesByActors(List.of("Stamat", "Pesho"));
    }

    @Test
    public void test_getMoviesByActors_shouldReturnSortedCollection() {
        Movie Movie = new Movie("asd", "bsd", 2010, 4000, List.of("Pesho", "Gosho"));
        Movie Movie2 = new Movie("dsd", "esd", 2013, 4500, List.of("Gosho", "Pesho"));
        Movie Movie3 = new Movie("hsd", "isd", 2012, 4000, List.of("Sasho", "Tosho"));
        Movie Movie4 = new Movie("ksd", "lsd", 2013, 4500, List.of("Pesho", "Gosho"));
        Movie Movie5 = new Movie("nsd", "osd", 2014, 4500, List.of("Pesho", "Gosho"));
        Movie Movie6 = new Movie("qsd", "msd", 2010, 4000, List.of("Sasho", "Gosho"));

        this.movieDatabase.addMovie(Movie);
        this.movieDatabase.addMovie(Movie2);
        this.movieDatabase.addMovie(Movie3);
        this.movieDatabase.addMovie(Movie4);
        this.movieDatabase.addMovie(Movie5);
        this.movieDatabase.addMovie(Movie6);

        String[] expected = {Movie5.getId(), Movie2.getId(), Movie4.getId(), Movie.getId()};

        Iterable<Movie> movieIterable = this.movieDatabase.getMoviesByActors(List.of("Gosho", "Pesho"));
        List<Movie> movies = StreamSupport.stream(movieIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(4, movies.size());
        int counter = 0;
        for (Movie movie : movies) {
            Assert.assertEquals(expected[counter++], movie.getId());
        }
    }
}
