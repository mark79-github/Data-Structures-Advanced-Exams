package core;

import models.Message;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordImpl implements Discord {

    private final Map<String, Message> messages;

    public DiscordImpl() {
        this.messages = new LinkedHashMap<>();
    }

    @Override
    public void sendMessage(Message message) {
        this.messages.put(message.getId(), message);
    }

    @Override
    public boolean contains(Message message) {
        return this.messages.containsKey(message.getId());
    }

    @Override
    public int size() {
        return this.messages.size();
    }

    @Override
    public Message getMessage(String messageId) {
        if (!this.messages.containsKey(messageId)) {
            throw new IllegalArgumentException();
        }
        return this.messages.get(messageId);
    }

    @Override
    public void deleteMessage(String messageId) {
        if (!this.messages.containsKey(messageId)) {
            throw new IllegalArgumentException();
        }
        this.messages.remove(messageId);
    }

    @Override
    public void reactToMessage(String messageId, String reaction) {
        if (!this.messages.containsKey(messageId)) {
            throw new IllegalArgumentException();
        }
        this.messages.get(messageId).getReactions().add(reaction);
    }

    @Override
    public Iterable<Message> getChannelMessages(String channel) {
        List<Message> messageList = this.messages.values()
                .stream()
                .filter(message -> message.getChannel().equals(channel))
                .collect(Collectors.toList());
        if (messageList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return messageList;
    }

    @Override
    public Iterable<Message> getMessagesByReactions(List<String> reactions) {
        return this.messages.values()
                .stream()
                .filter(message -> new HashSet<>(message.getReactions()).containsAll(reactions))
                .sorted((o1, o2) -> {
                    if (o2.getReactions().size() == o1.getReactions().size()) {
                        return Integer.compare(o1.getTimestamp(), o2.getTimestamp());
                    }
                    return Integer.compare(o2.getReactions().size(), o1.getReactions().size());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getMessageInTimeRange(Integer lowerBound, Integer upperBound) {
        Map<String, List<Message>> channelMessages = new LinkedHashMap<>();
        this.messages.values()
                .stream()
                .filter(message -> message.getTimestamp() >= lowerBound && message.getTimestamp() <= upperBound)
                .forEach(message -> {
                    channelMessages.computeIfAbsent(message.getChannel(), s -> new ArrayList<>());
                    channelMessages.get(message.getChannel()).add(message);
                });
        return channelMessages.values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.size(), o1.size()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getTop3MostReactedMessages() {
        return this.messages.values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getReactions().size(), o1.getReactions().size()))
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent() {
        return this.messages.values()
                .stream()
                .sorted((o1, o2) -> {
                    if (o2.getReactions().size() == o1.getReactions().size()) {
                        if (o1.getTimestamp() == o2.getTimestamp()) {
                            return Integer.compare(o1.getContent().length(), o2.getContent().length());
                        }
                        return Integer.compare(o1.getTimestamp(), o2.getTimestamp());
                    }
                    return Integer.compare(o2.getReactions().size(), o1.getReactions().size());
                })
                .collect(Collectors.toList());
    }
}
