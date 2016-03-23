package by.bsu.up.chat.storage;

import by.bsu.up.chat.common.models.Message;
import by.bsu.up.chat.logging.Logger;
import by.bsu.up.chat.logging.impl.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InMemoryMessageStorage implements MessageStorage, Persistable {

    private static final String DEFAULT_PERSISTENCE_FILE = "messages.srg";

    private static final Logger logger = Log.create(InMemoryMessageStorage.class);

    private List<Message> messages = new ArrayList<>();

    @Override
    public synchronized List<Message> getPortion(Portion portion) {
        int from = portion.getFromIndex();
        if (from < 0) {
            throw new IllegalArgumentException(String.format("Portion from index %d can not be less then 0", from));
        }
        int to = portion.getToIndex();
        if (to != -1 && to < portion.getFromIndex()) {
            throw new IllegalArgumentException(String.format("Porting last index %d can not be less then start index %d", to, from));
        }
        to = Math.max(to, messages.size());
        return messages.subList(from, to);
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public boolean updateMessage(Message message) {
        throw new UnsupportedOperationException("Update for messages is not supported yet");
    }

    @Override
    public synchronized boolean removeMessage(String messageId) {
        throw new UnsupportedOperationException("Removing of messages is not supported yet");
    }

    @Override
    public int size() {
        return messages.size();
    }

    @Override
    public synchronized boolean persist() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DEFAULT_PERSISTENCE_FILE))){
            outputStream.writeObject(messages);
            return true;
        } catch (IOException e) {
            logger.error("Error while saving storage", e);
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean load() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(DEFAULT_PERSISTENCE_FILE))){
            messages = (List<Message>) objectInputStream.readObject();
            return true;
        } catch (ClassNotFoundException | IOException e) {
            logger.error("Error while loading data from storage persistence file", e);
            logger.info("Resuming with empty storage");
            return false;
        }
    }
}
