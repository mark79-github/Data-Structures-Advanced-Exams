package core;

import models.Movie;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDatabaseImpl implements MovieDatabase {

    private final Set<Movie> movies;
    private final Map<String, List<Movie>> actorsWithMovies;

    public MovieDatabaseImpl() {
        this.movies = new LinkedHashSet<>();
        this.actorsWithMovies = new LinkedHashMap<>();
    }

    @Override
    public void addMovie(Movie movie) {
        this.movies.add(movie);
        List<String> actors = movie.getActors();
        for (String actor : actors) {
            this.actorsWithMovies.computeIfAbsent(actor, s -> new ArrayList<>());
            List<Movie> movieList = this.actorsWithMovies.get(actor);
            movieList.add(movie);
            this.actorsWithMovies.put(actor, movieList);
        }
    }

    @Override
    public void removeMovie(String movieId) {
        Movie movie = this.movies
                .stream()
                .filter(m -> m.getId().equals(movieId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        this.movies.remove(movie);
        for (String actor : movie.getActors()) {
            List<Movie> movieList = this.actorsWithMovies.get(actor);
            movieList.remove(movie);
            if (movieList.isEmpty()) {
                this.actorsWithMovies.remove(actor);
            }
        }
    }

    @Override
    public int size() {
        return this.movies.size();
    }

    @Override
    public boolean contains(Movie movie) {
        return this.movies.contains(movie);
    }

    @Override
    public Iterable<Movie> getMoviesByActor(String actorName) {
        if (!this.actorsWithMovies.containsKey(actorName)) {
            throw new IllegalArgumentException();
        }
        return this.actorsWithMovies.get(actorName)
                .stream()
                .sorted((o1, o2) -> {
                    if (o2.getRating() == o1.getRating()) {
                        return o2.getReleaseYear() - o1.getReleaseYear();
                    }
                    return Double.compare(o2.getRating(), o1.getRating());
                }).collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesByActors(List<String> actors) {
        List<Movie> movieList = this.movies
                .stream()
                .filter(movie -> new HashSet<>(movie.getActors()).containsAll(actors))
                .sorted((o1, o2) -> {
                    if (o2.getRating() == o1.getRating()) {
                        return o2.getReleaseYear() - o1.getReleaseYear();
                    }
                    return Double.compare(o2.getRating(), o1.getRating());
                })
                .collect(Collectors.toList());
        if (movieList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return movieList;
    }

    @Override
    public Iterable<Movie> getMoviesByYear(Integer releaseYear) {
        return this.movies
                .stream()
                .filter(movie -> movie.getReleaseYear() == releaseYear)
                .sorted((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesInRatingRange(double lowerBound, double upperBound) {
        return this.movies
                .stream()
                .filter(movie -> movie.getRating() >= lowerBound && movie.getRating() <= upperBound)
                .sorted((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getAllMoviesOrderedByActorPopularityThenByRatingThenByYear() {
        return this.movies
                .stream()
                .sorted((o1, o2) -> {
                    int o1MovieCount = o1.getActors()
                            .stream()
                            .mapToInt(actor -> this.actorsWithMovies.get(actor).size())
                            .sum();
                    int o2MovieCount = o2.getActors()
                            .stream()
                            .mapToInt(actor -> this.actorsWithMovies.get(actor).size())
                            .sum();
                    if (o1MovieCount == o2MovieCount) {
                        if (Double.compare(o2.getRating(), o1.getRating()) == 0) {
                            return o2.getReleaseYear() - o1.getReleaseYear();
                        }
                        return Double.compare(o2.getRating(), o1.getRating());
                    }
                    return o2MovieCount - o1MovieCount;
                })
                .collect(Collectors.toList());
    }
}
