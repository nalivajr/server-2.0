package by.bsu.up.chat.storage;

public interface Persistable {

    /**
     * Persists the objects state
     * @return true if object was persisted successfully and false otherwise
     */
    boolean persist();

    /**
     * Loads previously persisted object state
     * @return true if object state was loaded successfully and false otherwise
     */
    boolean load();

}
