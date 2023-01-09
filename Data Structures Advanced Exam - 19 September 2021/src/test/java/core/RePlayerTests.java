package core;

import models.Track;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class RePlayerTests {
    private RePlayer rePlayer;

    private Track getRandomTrack() {
        return new Track(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                (int) Math.min(1, Math.random() * 1_000_000_000),
                (int) Math.min(10, Math.random() * 10_000));
    }

    @Before
    public void setup() {
        this.rePlayer = new RePlayerImpl();
    }

    @Test
    public void testAddTrack_WithExistentAlbum_ShouldSuccessfullyAddTrack() {
        this.rePlayer.addTrack(this.getRandomTrack(), "randomAlbum");
        this.rePlayer.addTrack(this.getRandomTrack(), "randomAlbum");

        assertEquals(2, this.rePlayer.size());
    }

    @Test
    public void testContains_WithExistentTrack_ShouldReturnTrue() {
        Track randomTrack = this.getRandomTrack();

        this.rePlayer.addTrack(randomTrack, "randomAlbum");

        assertTrue(this.rePlayer.contains(randomTrack));
    }

    @Test
    public void testGetTracksOrderedByMultiCriteria_WithCorrectData_ShouldReturnCorrectResults() {
        Track track = new Track("asd", "bsd", "csd", 4000, 400);
        Track track2 = new Track("dsd", "esd", "fsd", 5000, 400);
        Track track3 = new Track("hsd", "isd", "jsd", 5000, 500);
        Track track4 = new Track("ksd", "lsd", "msd", 5000, 600);
        Track track5 = new Track("nsd", "osd", "psd", 6000, 100);

        this.rePlayer.addTrack(track, "randomAlbum");
        this.rePlayer.addTrack(track2, "bandomAlbum");
        this.rePlayer.addTrack(track3, "aandomAlbum2");
        this.rePlayer.addTrack(track4, "aandomAlbum2");
        this.rePlayer.addTrack(track5, "aandomAlbum2");

        List<Track> list =
                StreamSupport.stream(this.rePlayer.getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending().spliterator(), false)
                        .collect(Collectors.toList());

        assertEquals(5, list.size());
        assertEquals(track5, list.get(0));
        assertEquals(track4, list.get(1));
        assertEquals(track3, list.get(2));
        assertEquals(track2, list.get(3));
        assertEquals(track, list.get(4));
    }

    @Test
    public void testContains_With1000000Results_ShouldPassInstantly() {
        int count = 1000000;

        Track trackToContain = null;

        for (int i = 0; i < count; i++) {
            Track track = new Track(i + "", "Title" + i, "Artist" + i, i * 100, i * 10);

            this.rePlayer.addTrack(track, "randomAlbum");

            if (i == 800000) {
                trackToContain = track;
            }
        }

        long start = System.currentTimeMillis();

        this.rePlayer.contains(trackToContain);

        long stop = System.currentTimeMillis();
        long elapsedTime = stop - start;

        assertTrue(elapsedTime <= 1);
    }

    @Test
    public void testRemoveTrack_With1000000ResultsAndQueue_ShouldPassQuickly() {
        int count = 100000;

        Track actual = null;

        for (int i = count; i >= 0; i--) {
            Track track = new Track(i + "", "Title" + i, "Artist" + i, i * 1000, i * 100);

            String album = null;

            if (i <= 5000) {
                album = "randomAlbum5";
            } else if (i <= 30000) {
                album = "randomAlbum3";
            } else {
                album = "randomAlbum";
            }

            this.rePlayer.addTrack(track, album);

            if (i == 5000) {
                actual = track;
            }

            if (i <= 7500 && i >= 2500) {
                this.rePlayer.addToQueue(track.getTitle(), album);
            }
        }

        long start = System.currentTimeMillis();

        this.rePlayer.removeTrack(actual.getTitle(), "randomAlbum5");

        long stop = System.currentTimeMillis();

        long elapsedTime = stop - start;

        assertTrue(elapsedTime <= 5);

        while (true) {
            try {
                assertNotEquals(this.rePlayer.play(), actual);
            } catch (IllegalArgumentException e) {
                break;
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addToQueue_shouldThrowException_whenAlbumDoesNotExists() {
        Track track = new Track("1", "1", "1", 1, 1);
        this.rePlayer.addTrack(track, "1");
        this.rePlayer.addToQueue("1", "2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addToQueue_shouldThrowException_whenTrackDoesNotExists() {
        Track track = new Track("1", "1", "1", 1, 1);
        this.rePlayer.addTrack(track, "1");
        this.rePlayer.addToQueue("2", "1");
    }

    @Test
    public void test_addToQueue_shouldReturnCorrectly() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        this.rePlayer.addTrack(track_1, "1");
        this.rePlayer.addTrack(track_2, "1");
        this.rePlayer.addToQueue("1", "1");
        this.rePlayer.addToQueue("2", "1");

        Track track = this.rePlayer.play();

        Assert.assertEquals(track_1.getId(), track.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDiscography_shouldThrowException_whenArtistDoesNotExists() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        this.rePlayer.addTrack(track_1, "1");
        this.rePlayer.addTrack(track_2, "1");

        this.rePlayer.getDiscography("3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDiscography_shouldThrowException_whenNoTrackEntries() {
        this.rePlayer.getDiscography("1");
    }

    @Test
    public void test_getDiscography_shouldReturnCorrectly() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "artist", 300, 30);
        Track track_4 = new Track("4", "4", "artist", 400, 40);
        Track track_5 = new Track("5", "5", "5", 500, 50);
        this.rePlayer.addTrack(track_1, "1");
        this.rePlayer.addTrack(track_2, "1");
        this.rePlayer.addTrack(track_3, "1");
        this.rePlayer.addTrack(track_4, "1");
        this.rePlayer.addTrack(track_5, "1");

        Map<String, List<Track>> discography = this.rePlayer.getDiscography("artist");
        Assert.assertEquals(1, discography.size());
        String[] expected = {"3", "4"};
        int counter = 0;
        for (Track track : discography.get("1")) {
            Assert.assertEquals(expected[counter++], track.getId());
        }
    }

    @Test()
    public void test_getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending_shouldReturnEmptyCollection_whenNoTrackEntities() {
        Iterable<Track> trackIterable = this.rePlayer.getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending();
        List<Track> trackList = StreamSupport.stream(trackIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, trackList.size());
    }

    @Test()
    public void test_getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending_shouldReturnEmptyCollection_whenRemovedAllTracks() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        this.rePlayer.addTrack(track_1, "1");
        this.rePlayer.addTrack(track_2, "1");
        this.rePlayer.removeTrack(track_1.getTitle(), "1");
        this.rePlayer.removeTrack(track_2.getTitle(), "1");

        Iterable<Track> trackIterable = this.rePlayer.getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending();
        List<Track> trackList = StreamSupport.stream(trackIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, trackList.size());
    }

    @Test()
    public void test_getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending_shouldReturnSortedCollection() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 10);
        Track track_3 = new Track("3", "3", "3", 200, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);
        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "Aa1");
        this.rePlayer.addTrack(track_5, "AaA");

        String[] expected = {track_3.getId(), track_2.getId(), track_1.getId(), track_4.getId(), track_5.getId()};

        Iterable<Track> trackIterable = this.rePlayer.getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending();

        int counter = 0;
        for (Track track : trackIterable) {
            Assert.assertEquals(expected[counter++], track.getId());
        }
    }

    @Test()
    public void test_getTracksInDurationRangeOrderedByDurationThenByPlaysDescending_shouldReturnEmptyCollection_whenNoTrackEntities() {
        Iterable<Track> trackIterable = this.rePlayer.getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(1, 2);
        List<Track> trackList = StreamSupport.stream(trackIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, trackList.size());
    }

    @Test()
    public void test_getTracksInDurationRangeOrderedByDurationThenByPlaysDescending_shouldReturnEmptyCollection_shouldReturnEmptyCollection_whenRemovedAllTracks() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        this.rePlayer.addTrack(track_1, "1");
        this.rePlayer.addTrack(track_2, "1");
        this.rePlayer.removeTrack(track_1.getTitle(), "1");
        this.rePlayer.removeTrack(track_2.getTitle(), "1");

        Iterable<Track> trackIterable = this.rePlayer.getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(1, 2);
        List<Track> trackList = StreamSupport.stream(trackIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, trackList.size());
    }

    @Test()
    public void test_getTracksInDurationRangeOrderedByDurationThenByPlaysDescending_shouldReturnSortedCollection() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "3", 300, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);
        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "AAA");
        this.rePlayer.addTrack(track_5, "AAA");

        String[] expected = {track_3.getId(), track_2.getId(), track_4.getId()};

        Iterable<Track> trackIterable = this.rePlayer.getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(11, 49);

        int counter = 0;
        for (Track track : trackIterable) {
            Assert.assertEquals(expected[counter++], track.getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeTrack_shouldThrowException_whenTrackDoesNotExists() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "3", 300, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);
        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "AAA");
        this.rePlayer.addTrack(track_5, "AAA");

        this.rePlayer.removeTrack("6", "AAA");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeTrack_shouldThrowException_whenAlbumDoesNotExists() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "3", 300, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);
        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "AAA");
        this.rePlayer.addTrack(track_5, "AAA");

        this.rePlayer.removeTrack("1", "AaA");
    }

    @Test
    public void test_removeTrack_shouldReturnCorrectly() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "3", 300, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);
        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "AAA");
        this.rePlayer.addTrack(track_5, "AAA");

        Assert.assertEquals(5, this.rePlayer.size());
        this.rePlayer.removeTrack("4", "AAA");
        Assert.assertEquals(4, this.rePlayer.size());
        Assert.assertFalse(this.rePlayer.contains(track_4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAlbum_shouldThrowException_whenNoEntities() {
        this.rePlayer.getAlbum(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAlbum_shouldThrowException_whenNoSuchAlbumIsFound() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "3", 200, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);

        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "Aa1");
        this.rePlayer.addTrack(track_5, "AaA");

        this.rePlayer.getAlbum(UUID.randomUUID().toString());
    }

    @Test
    public void test_getAlbum_shouldReturnSortedCollection() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        Track track_3 = new Track("3", "3", "3", 200, 20);
        Track track_4 = new Track("4", "4", "4", 200, 30);
        Track track_5 = new Track("5", "5", "5", 500, 50);

        this.rePlayer.addTrack(track_1, "AAA");
        this.rePlayer.addTrack(track_2, "AAA");
        this.rePlayer.addTrack(track_3, "AAA");
        this.rePlayer.addTrack(track_4, "Aa1");
        this.rePlayer.addTrack(track_5, "AaA");

        String[] expected = {track_2.getId(), track_3.getId(), track_1.getId()};

        Iterable<Track> trackIterable = this.rePlayer.getAlbum("AAA");
        List<Track> tracks = StreamSupport.stream(trackIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(3, tracks.size());

        int counter = 0;
        for (Track track : tracks) {
            Assert.assertEquals(expected[counter++], track.getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_play_shouldThrowException_whenNoEntities() {
        this.rePlayer.play();
    }

    @Test
    public void test_play_shouldReturnCorrectly() {
        Track track_1 = new Track("1", "1", "1", 100, 10);
        Track track_2 = new Track("2", "2", "2", 200, 20);
        this.rePlayer.addTrack(track_1, "1");
        this.rePlayer.addTrack(track_2, "1");
        this.rePlayer.addToQueue("1", "1");
        this.rePlayer.addToQueue("2", "1");

        Track track = this.rePlayer.play();

        Assert.assertEquals(track_1.getId(), track.getId());
        Assert.assertEquals(track_1.getPlays(), track.getPlays());
    }

    @Test
    public void test_remove_shouldRemoveTrackFromQueue() {
        Track track_1 = new Track("id_1", "title_1", "artist_1", 100, 10);
        Track track_2 = new Track("id_2", "title_2", "artist_2", 200, 20);
        Track track_3 = new Track("id_3", "title_3", "artist_3", 300, 30);
        this.rePlayer.addTrack(track_1, "album_1");
        this.rePlayer.addTrack(track_2, "album_1");
        this.rePlayer.addTrack(track_3, "album_1");
        this.rePlayer.addToQueue("title_1", "album_1");
        this.rePlayer.addToQueue("title_2", "album_1");
        this.rePlayer.addToQueue("title_3", "album_1");

        this.rePlayer.removeTrack("title_1", "album_1");
        Track track = this.rePlayer.play();

        Assert.assertEquals(track_2.getId(), track.getId());
        Assert.assertEquals(track_2.getPlays(), track.getPlays());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDiscography_shouldThrowException_whenGivenArtistHasNoTracks() {
        Track track_1 = new Track("id_1", "title_1", "artist_1", 100, 10);
        this.rePlayer.addTrack(track_1, "album_1");
        this.rePlayer.removeTrack("title_1", "album_1");
        this.rePlayer.getDiscography("artist_1");
    }
}
