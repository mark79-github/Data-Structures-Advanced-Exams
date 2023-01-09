package core;

import models.Track;

import java.util.*;
import java.util.stream.Collectors;


public class RePlayerImpl implements RePlayer {

    static class Pair {
        private final String title;
        private final String album;

        public Pair(String title, String album) {
            this.title = title;
            this.album = album;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;

            Pair pair = (Pair) o;

            if (!getTitle().equals(pair.getTitle())) return false;
            return getAlbum().equals(pair.getAlbum());
        }

        @Override
        public int hashCode() {
            int result = getTitle().hashCode();
            result = 31 * result + getAlbum().hashCode();
            return result;
        }
    }

    private final Map<Pair, Track> tracks;
    private final Map<String, Track> trackIds;
    private final Map<String, Set<Track>> albums;
    private final Queue<Track> queue;
    private final Map<String, Map<String, List<Track>>> artists;
    private final TreeMap<Integer, Set<Track>> durations;

    public RePlayerImpl() {
        this.tracks = new LinkedHashMap<>();
        this.trackIds = new LinkedHashMap<>();
        this.albums = new TreeMap<>();
        this.queue = new LinkedList<>();
        this.artists = new LinkedHashMap<>();
        this.durations = new TreeMap<>();
    }

    @Override
    public void addTrack(Track track, String album) {
        String title = track.getTitle();
        Pair pair = new Pair(title, album);
        this.tracks.put(pair, track);
        this.trackIds.put(track.getId(), track);
        this.albums.computeIfAbsent(album, s -> new LinkedHashSet<>()).add(track);
        this.artists.computeIfAbsent(track.getArtist(), s -> new LinkedHashMap<>()).computeIfAbsent(album, s -> new LinkedList<>()).add(track);
        this.durations.computeIfAbsent(track.getDurationInSeconds(), s -> new TreeSet<>(Comparator.comparingInt(Track::getPlays).reversed())).add(track);
    }

    @Override
    public void removeTrack(String trackTitle, String albumName) {
        Pair pair = new Pair(trackTitle, albumName);
        if (!this.tracks.containsKey(pair)) {
            throw new IllegalArgumentException();
        }

        Track track = this.tracks.remove(pair);

        this.trackIds.remove(track.getId());

        this.albums.get(albumName).remove(track);
        if (this.albums.get(albumName).isEmpty()) {
            this.albums.remove(albumName);
        }

        String artist = track.getArtist();
        Map<String, List<Track>> albumMap = this.artists.get(artist);
        List<Track> albumTracks = albumMap.get(albumName);
        albumTracks.remove(track);
        if (albumTracks.isEmpty()) {
            albumMap.remove(albumName);
        }
        if (albumMap.isEmpty()) {
            this.artists.remove(track.getArtist());
        }

        this.queue.remove(track);

        this.durations.get(track.getDurationInSeconds()).remove(track);
        if (this.durations.get(track.getDurationInSeconds()).isEmpty()) {
            this.durations.remove(track.getDurationInSeconds());
        }
    }

    @Override
    public boolean contains(Track track) {
        return this.trackIds.containsKey(track.getId());
    }

    @Override
    public int size() {
        return this.trackIds.size();
    }

    @Override
    public Track getTrack(String title, String albumName) {
        Pair pair = new Pair(title, albumName);
        if (!this.tracks.containsKey(pair)) {
            throw new IllegalArgumentException();
        }
        return this.tracks.get(pair);
    }

    @Override
    public Iterable<Track> getAlbum(String albumName) {
        if (!this.albums.containsKey(albumName)) {
            throw new IllegalArgumentException();
        }
        return this.albums.get(albumName)
                .stream()
                .sorted((o1, o2) -> o2.getPlays() - o1.getPlays())
                .collect(Collectors.toList());
    }

    @Override
    public void addToQueue(String trackName, String albumName) {
        Track track = this.getTrack(trackName, albumName);
        this.queue.add(track);
    }

    @Override
    public Track play() {
        if (this.queue.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Track track = this.queue.poll();
        track.setPlays(track.getPlays() + 1);
        return track;
    }

    @Override
    public Iterable<Track> getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(int lowerBound, int upperBound) {
        return this.durations.subMap(lowerBound, true, upperBound, true).values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Track> getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending() {
        return this.albums.values()
                .stream()
                .map(trackSet -> trackSet.stream().sorted((o1, o2) -> {
                    if (o2.getPlays() == o1.getPlays()) {
                        return Integer.compare(o2.getDurationInSeconds(), o1.getDurationInSeconds());
                    }
                    return Integer.compare(o2.getPlays(), o1.getPlays());
                }).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Track>> getDiscography(String artistName) {
        if (!this.artists.containsKey(artistName)) {
            throw new IllegalArgumentException();
        }
        return this.artists.get(artistName);
    }
}
