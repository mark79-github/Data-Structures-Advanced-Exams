package core;

import models.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiscordTests {
    private interface InternalTest {
        void execute();
    }

    private Discord discord;

    private Message getRandomMessage() {
        return new Message(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                (int) Math.min(1, Math.random() * 1_000_000_000),
                UUID.randomUUID().toString());
    }

    @Before
    public void setup() {
        this.discord = new DiscordImpl();
    }

    // Correctness Tests

    @Test
    public void testsendMessage_ShouldSuccessfullysendMessage() {
        this.discord.sendMessage(this.getRandomMessage());
        this.discord.sendMessage(this.getRandomMessage());

        assertEquals(2, this.discord.size());
    }

    @Test
    public void testContains_WithExistentMessage_ShouldReturnTrue() {
        Message randomMessage = this.getRandomMessage();

        this.discord.sendMessage(randomMessage);

        assertTrue(this.discord.contains(randomMessage));
    }

    @Test
    public void testCount_With5Messages_ShouldReturn5() {
        this.discord.sendMessage(this.getRandomMessage());
        this.discord.sendMessage(this.getRandomMessage());
        this.discord.sendMessage(this.getRandomMessage());
        this.discord.sendMessage(this.getRandomMessage());
        this.discord.sendMessage(this.getRandomMessage());

        assertEquals(5, this.discord.size());
    }

    @Test
    public void testReactMessage_WithNonexistentMessage_ShouldThrowException() {
        Message message = this.getRandomMessage();

        this.discord.sendMessage(message);

        boolean catched = false;

        try {
            this.discord.reactToMessage(this.getRandomMessage().getId(), "test");
        } catch (IllegalArgumentException e) {
            catched = true;
        }

        assertTrue(catched);
    }

    @Test
    public void testGetMessagesByReactions_WithMessages_ShouldReturnCorrectData() {
        Message Message = new Message("asd", "bsd", 5500, "test");
        Message Message2 = new Message("csd", "dsd", 5500, "pest");
        Message Message3 = new Message("esd", "fsd", 4000, "mest");
        Message Message4 = new Message("hsd", "isd", 10000, "dest");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        this.discord.reactToMessage(Message.getId(), "laugh");
        this.discord.reactToMessage(Message.getId(), "lol");
        this.discord.reactToMessage(Message.getId(), "thumbsup");

        this.discord.reactToMessage(Message2.getId(), "thumbsup");
        this.discord.reactToMessage(Message2.getId(), "lol");

        this.discord.reactToMessage(Message3.getId(), "lol");
        this.discord.reactToMessage(Message3.getId(), "lol");
        this.discord.reactToMessage(Message3.getId(), "thumbsup");

        List<Message> messages = StreamSupport.stream(this.discord.getMessagesByReactions(List.of("thumbsup", "lol")).spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(3, messages.size());

        assertEquals(Message3, messages.get(0));
        assertEquals(Message, messages.get(1));
        assertEquals(Message2, messages.get(2));
    }

    @Test
    public void testRemoveMessage_WithCorrectData_ShouldDecrementCount() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        this.discord.deleteMessage("dsd");

        assertEquals(3, this.discord.size());
    }

    // Performance Tests

    @Test
    public void testSendMessage_With100000Results_ShouldPassQuickly() {

        int count = 100000;

        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            this.discord.sendMessage(new Message(i + "", "Title" + i, i * 100, "Channel" + i));
        }

        long stop = System.currentTimeMillis();

        long elapsedTime = stop - start;

        assertTrue(elapsedTime < 450);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getMessage_shouldThrowException_whenMessageIdDoesNotExists() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        this.discord.getMessage("lsd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getMessage_shouldThrowException_whenNoEntities() {
        this.discord.getMessage("lsd");
    }

    @Test
    public void test_getMessage_shouldReturnCorrectly() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        Message message = this.discord.getMessage("hsd");
        Assert.assertEquals(message.getId(), Message3.getId());
        Assert.assertTrue(this.discord.contains(message));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteMessage_shouldThrowException_whenMessageIdDoesNotExists() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        this.discord.deleteMessage("lsd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteMessage_shouldThrowException_whenNoEntities() {
        this.discord.deleteMessage("lsd");
    }

    @Test
    public void test_deleteMessage_shouldReturnCorrectly() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        this.discord.deleteMessage("hsd");
        Assert.assertFalse(this.discord.contains(Message3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getChannelMessages_shouldThrowException_whenNoEntries() {
        this.discord.getChannelMessages("hsd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getChannelMessages_shouldThrowException_whenMessageInGivenChannel() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        this.discord.getChannelMessages("test3");
    }

    @Test
    public void test_getChannelMessages_shouldReturnCorrect() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        String[] expected = {Message3.getId(), Message4.getId()};

        Iterable<Message> messageIterable = this.discord.getChannelMessages("test2");
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());
        int counter = 0;
        for (Message message : messages) {
            Assert.assertEquals(expected[counter++], message.getId());
        }
    }

    @Test
    public void test_getMessageInTimeRange_shouldThrowException_whenNoEntries() {
        Iterable<Message> messageIterable = this.discord.getMessageInTimeRange(1000, 2000);
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertTrue(messages.isEmpty());
    }

    @Test
    public void test_getMessageInTimeRange_shouldThrowException_whenMessageInGivenChannel() {
        Message Message = new Message("asd", "bsd", 4000, "test");
        Message Message2 = new Message("dsd", "esd", 5000, "test");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);

        Iterable<Message> messageIterable = this.discord.getMessageInTimeRange(3000, 3999);
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertTrue(messages.isEmpty());
    }

    @Test
    public void test_getMessageInTimeRange_shouldReturnCorrect() {
        Message Message = new Message("asd", "bsd", 6000, "test1");
        Message Message2 = new Message("dsd", "esd", 5000, "test1");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");
        Message Message5 = new Message("lsd", "wds", 4000, "test3");
        Message Message6 = new Message("msd", "dsm", 4000, "test3");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);
        this.discord.sendMessage(Message5);
        this.discord.sendMessage(Message6);

        String[] expected = {Message5.getId(), Message6.getId(), Message2.getId(), Message4.getId()};

        Iterable<Message> messageIterable = this.discord.getMessageInTimeRange(4000, 5999);
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(4, messages.size());
        int counter = 0;
        for (Message message : messages) {
            Assert.assertEquals(expected[counter++], message.getId());
        }
    }

    @Test
    public void test_getTop3MostReactedMessages_shouldThrowException_whenNoEntries() {
        Iterable<Message> messageIterable = this.discord.getTop3MostReactedMessages();
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertTrue(messages.isEmpty());
    }


    @Test
    public void test_getTop3MostReactedMessages_shouldReturnCorrect() {
        Message Message = new Message("asd", "bsd", 6000, "test1");
        Message Message2 = new Message("dsd", "esd", 5000, "test1");
        Message Message3 = new Message("hsd", "isd", 6000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");
        Message Message5 = new Message("lsd", "wds", 4000, "test3");
        Message Message6 = new Message("msd", "dsm", 4000, "test3");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);
        this.discord.sendMessage(Message5);
        this.discord.sendMessage(Message6);

        this.discord.reactToMessage("lsd","1");
        this.discord.reactToMessage("lsd","2");
        this.discord.reactToMessage("lsd","3");
        this.discord.reactToMessage("hsd","1");
        this.discord.reactToMessage("hsd","2");
        this.discord.reactToMessage("hsd","3");
        this.discord.reactToMessage("msd","1");
        this.discord.reactToMessage("msd","2");
        this.discord.reactToMessage("dsd","1");
        this.discord.reactToMessage("dsd","2");

        String[] expected = {Message3.getId(), Message5.getId(), Message2.getId()};

        Iterable<Message> messageIterable = this.discord.getTop3MostReactedMessages();
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, messages.size());
        int counter = 0;
        for (Message message : messages) {
            Assert.assertEquals(expected[counter++], message.getId());
        }
    }

    @Test
    public void test_getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent_shouldThrowException_whenNoEntries() {
        Iterable<Message> messageIterable = this.discord.getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent();
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertTrue(messages.isEmpty());
    }


    @Test
    public void test_getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent_shouldReturnCorrect() {
        Message Message = new Message("asd", "bsd", 4000, "test1");
        Message Message2 = new Message("dsd", "esd", 5000, "test1");
        Message Message3 = new Message("hsd", "isd", 3000, "test2");
        Message Message4 = new Message("ksd", "test", 4000, "test2");
        Message Message5 = new Message("lsd", "wds", 4000, "test3");
        Message Message6 = new Message("msd", "ds", 4000, "test3");

        this.discord.sendMessage(Message);
        this.discord.sendMessage(Message2);
        this.discord.sendMessage(Message3);
        this.discord.sendMessage(Message4);
        this.discord.sendMessage(Message5);
        this.discord.sendMessage(Message6);

        this.discord.reactToMessage("lsd","1");
        this.discord.reactToMessage("lsd","2");
        this.discord.reactToMessage("lsd","3");
        this.discord.reactToMessage("asd","3");
        this.discord.reactToMessage("asd","3");
        this.discord.reactToMessage("asd","3");
        this.discord.reactToMessage("hsd","1");
        this.discord.reactToMessage("hsd","2");
        this.discord.reactToMessage("hsd","3");
        this.discord.reactToMessage("msd","1");
        this.discord.reactToMessage("msd","2");
        this.discord.reactToMessage("msd","3");

        String[] expected = {Message3.getId(), Message6.getId(), Message.getId(), Message5.getId(), Message4.getId(), Message2.getId()};

        Iterable<Message> messageIterable = this.discord.getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent();
        List<Message> messages = StreamSupport.stream(messageIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(6, messages.size());
        int counter = 0;
        for (Message message : messages) {
            Assert.assertEquals(expected[counter++], message.getId());
        }
    }
}
