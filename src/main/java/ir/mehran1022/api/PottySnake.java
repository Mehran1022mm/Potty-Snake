package ir.mehran1022.api;

import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A robust library for managing YAML files utilizing the SnakeYAML library.
 * This class provides methods to load, save, and manipulate YAML data with ease.
 * @author Mehran1022
 * @version 1.4
 */
@SuppressWarnings({"unchecked", "unused"})
public final class PottySnake {

    // The SnakeYAML parser instance for YAML operations
    @Getter
    private Yaml snakeYaml;

    // The path to the YAML file managed by this instance
    private final String filePath;

    // The in-memory representation of the YAML data as a nested map
    private Map<String, Object> data;

    /**
     * Constructs a new PottySnake instance associated with the given file path.
     * It initializes the parser and loads the existing YAML content into memory.
     *
     * @param filePath The path to the YAML file to manage.
     * @throws IOException If the file cannot be read or written to.
     */
    public PottySnake(String filePath) throws IOException {
        this.snakeYaml = new Yaml(getDumperOptions());
        this.filePath = filePath;
        data = new LinkedHashMap<>();
        load();
    }

    /**
     * Constructs a new PottySnake instance with custom DumperOptions.
     *
     * @param filePath The path to the YAML file to manage.
     * @param options  Custom DumperOptions for YAML formatting.
     * @throws IOException If the file cannot be read or written to.
     */
    public PottySnake(String filePath, DumperOptions options) throws IOException {
        this.snakeYaml = new Yaml(options);
        this.filePath = filePath;
        data = new LinkedHashMap<>();
        load();
    }

    /**
     * Sets custom DumperOptions for subsequent saves.
     *
     * @param options DumperOptions to use.
     */
    public void setDumperOptions(DumperOptions options) {
        this.snakeYaml = new Yaml(options);
    }

    /**
     * Loads the YAML content from the file into the data map.
     * If the file does not exist, an empty file is created.
     * If the file is empty or contains invalid YAML, an empty map is initialized.
     *
     * @throws IOException If the file cannot be read.
     */
    public void load() throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            data = new LinkedHashMap<>();
            save();
            return;
        }
        String content = Files.readString(path);
        Map<String, Object> loadedData = getSnakeYaml().load(content);
        data = Objects.requireNonNullElseGet(loadedData, LinkedHashMap::new);
    }

    /**
     * Reloads the YAML content from the file, discarding any in-memory changes.
     * Throws if the file does not exist.
     *
     * @throws IOException If the file cannot be read or does not exist.
     */
    public void reload() throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + filePath);
        }
        String content = Files.readString(path);
        Map<String, Object> loadedData = getSnakeYaml().load(content);
        data = Objects.requireNonNullElseGet(loadedData, LinkedHashMap::new);
    }

    /**
     * Saves the in-memory data map to the YAML file.
     * The data is converted to a YAML-formatted string and written to the file.
     *
     * @throws IOException If the file cannot be written to.
     */
    public void save() throws IOException {
        String content = getSnakeYaml().dump(data);
        Files.writeString(Path.of(filePath), content);
    }

    /**
     * Retrieves a value from the YAML data using a dot-notation key.
     * Example: getEntry("database.host") returns the "host" inside "database".
     *
     * @param key The key to retrieve the value for (nullable/empty not allowed).
     * @return The value, or null if the key does not exist.
     * @throws IllegalArgumentException if key is null or empty.
     */
    public Object getEntry(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);

            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return null; // The key does not exist
            }
        }

        return currentMap.get(keys[keys.length - 1]);
    }

    /**
     * Returns the value as a String, or null if missing or not a String.
     *
     * @param key Dot-notation key.
     * @return String value or null.
     */
    public String getString(String key) {
        Object val = getEntry(key);
        return val instanceof String ? (String) val : null;
    }

    /**
     * Returns the value as an Integer, or null if missing or not a number.
     *
     * @param key Dot-notation key.
     * @return Integer value or null.
     */
    public Integer getInt(String key) {
        Object val = getEntry(key);
        return val instanceof Number ? ((Number) val).intValue() : null;
    }

    /**
     * Returns the value as a Long, or null if missing or not a number.
     *
     * @param key Dot-notation key.
     * @return Long value or null.
     */
    public Long getLong(String key) {
        Object val = getEntry(key);
        return val instanceof Number ? ((Number) val).longValue() : null;
    }

    /**
     * Returns the value as a Double, or null if missing or not a number.
     *
     * @param key Dot-notation key.
     * @return Double value or null.
     */
    public Double getDouble(String key) {
        Object val = getEntry(key);
        return val instanceof Number ? ((Number) val).doubleValue() : null;
    }

    /**
     * Returns the value as a Boolean, or null if missing or not a boolean.
     *
     * @param key Dot-notation key.
     * @return Boolean value or null.
     */
    public Boolean getBoolean(String key) {
        Object val = getEntry(key);
        return val instanceof Boolean ? (Boolean) val : null;
    }

    /**
     * Returns the value as a List, or null if missing or not a List.
     *
     * @param key Dot-notation key.
     * @return List of Objects or null.
     */
    public List<Object> getList(String key) {
        Object val = getEntry(key);
        return val instanceof List ? (List<Object>) val : null;
    }

    /**
     * Returns the value as a Map, or null if missing or not a Map.
     *
     * @param key Dot-notation key.
     * @return Map or null.
     */
    public Map<String, Object> getMap(String key) {
        Object val = getEntry(key);
        return val instanceof Map ? (Map<String, Object>) val : null;
    }

    /**
     * Adds an entry to the specified section.
     * - If the section is a map, the key-value pair is added (key must not be null).
     * - If the section is a list, the value is appended (key must be null).
     * - If the section does not exist, a map is created (if key non-null) or a list (if key null).
     *
     * @param section The section key (root-level only, not nested).
     * @param key     The key for the entry within the section, or null if adding to a list.
     * @param value   The value to add.
     * @throws IOException          If save fails.
     * @throws IllegalArgumentException If section is null/empty or key usage mismatches type.
     */
    public void addEntry(String section, String key, Object value) throws IOException {
        if (section == null || section.isEmpty()) {
            throw new IllegalArgumentException("Section cannot be null or empty");
        }
        Object sectionObject = data.get(section);

        if (sectionObject instanceof Map) {
            if (key == null) {
                throw new IllegalArgumentException("Key cannot be null when adding to a map section");
            }
            ((Map<String, Object>) sectionObject).put(key, value);
        } else if (sectionObject instanceof List) {
            if (key != null) {
                throw new IllegalArgumentException("Key must be null when adding to a list section");
            }
            ((List<Object>) sectionObject).add(value);
        } else {
            // Section does not exist or is null
            if (key == null) {
                List<Object> newList = new ArrayList<>();
                newList.add(value);
                data.put(section, newList);
            } else {
                Map<String, Object> newMap = new LinkedHashMap<>();
                newMap.put(key, value);
                data.put(section, newMap);
            }
        }
        save();
    }

    /**
     * Adds or updates an entry in the YAML data using a dot-notation key.
     * Intermediate maps are created as needed; will throw if an intermediate path is not a map.
     *
     * @param key   Dot-notation key.
     * @param value Value to set.
     * @throws IOException            If save fails.
     * @throws IllegalStateException  If an intermediate path is not a map.
     */
    public void setEntry(String key, Object value) throws IOException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object mapValue = currentMap.get(keys[i]);

            if (mapValue == null) {
                // Create a new map if the key does not exist
                Map<String, Object> newMap = new LinkedHashMap<>();
                currentMap.put(keys[i], newMap);
                currentMap = newMap;
            } else if (mapValue instanceof Map) {
                currentMap = (Map<String, Object>) mapValue;
            } else {
                // Intermediate path exists but is not a map (e.g., list or scalar)
                throw new IllegalStateException("Cannot traverse into non-map value at key: " + keys[i]);
            }
        }

        currentMap.put(keys[keys.length - 1], value);
        save();
    }

    /**
     * Removes an entry from the YAML data using a dot-notation key.
     * Silently does nothing if the key does not exist or an intermediate path is missing.
     *
     * @param key Dot-notation key.
     * @throws IOException If save fails.
     */
    public void removeEntry(String key) throws IOException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);

            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return; // The key does not exist in a structure
            }
        }

        currentMap.remove(keys[keys.length - 1]);
        save();
    }

    /**
     * Ensures a dot-notation path exists as a map. Creates nested maps as needed.
     * If the leaf already exists and is a map, no change. If leaf exists as non-map, overwrites with empty map.
     *
     * @param section Dot-notation path.
     * @throws IOException If save fails.
     */
    public void createSection(String section) throws IOException {
        if (section == null || section.isEmpty()) {
            throw new IllegalArgumentException("Section cannot be null or empty");
        }
        String[] keys = section.split("\\.");
        Map<String, Object> currentMap = data;
        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);
            if (!(value instanceof Map)) {
                Map<String, Object> newMap = new LinkedHashMap<>();
                currentMap.put(keys[i], newMap);
                currentMap = newMap;
            } else {
                currentMap = (Map<String, Object>) value;
            }
        }
        String leafKey = keys[keys.length - 1];
        Object leaf = currentMap.get(leafKey);
        if (!(leaf instanceof Map)) {
            currentMap.put(leafKey, new LinkedHashMap<>());
        }
        save();
    }

    /**
     * Checks if a section exists (any non-null value) at the given dot-notation path.
     *
     * @param section Dot-notation path.
     * @return true if the path exists and has a non-null value.
     */
    public boolean hasSection(String section) {
        return getEntry(section) != null;
    }

    /**
     * Renames a section: moves the value from oldSection to newSection.
     * Only works if oldSection exists; does nothing otherwise.
     *
     * @param oldSection Existing dot-notation path.
     * @param newSection New dot-notation path.
     * @throws IOException If save fails.
     */
    public void renameSection(String oldSection, String newSection) throws IOException {
        if (hasSection(oldSection)) {
            Object sectionData = getEntry(oldSection);
            removeEntry(oldSection);
            setEntry(newSection, sectionData);
        }
    }

    /**
     * Ensures a dot-notation path exists as a list. Creates nested maps as needed.
     * If the leaf already exists and is a list, no change. If leaf exists as non-list, overwrites with empty list.
     *
     * @param section Dot-notation path.
     * @throws IOException If save fails.
     */
    public void createList(String section) throws IOException {
        if (section == null || section.isEmpty()) {
            throw new IllegalArgumentException("Section cannot be null or empty");
        }
        String[] keys = section.split("\\.");
        Map<String, Object> currentMap = data;
        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);
            if (!(value instanceof Map)) {
                Map<String, Object> newMap = new LinkedHashMap<>();
                currentMap.put(keys[i], newMap);
                currentMap = newMap;
            } else {
                currentMap = (Map<String, Object>) value;
            }
        }
        String leafKey = keys[keys.length - 1];
        Object leaf = currentMap.get(leafKey);
        if (!(leaf instanceof List)) {
            currentMap.put(leafKey, new ArrayList<>());
        }
        save();
    }

    /**
     * Replaces the entire data with the given entries and saves.
     *
     * @param entries Map of entries to set (clears existing data first). If null, does nothing.
     * @throws IOException If save fails.
     */
    public void setEntries(Map<String, Object> entries) throws IOException {
        if (entries == null) return;
        data.clear();
        data.putAll(entries);
        save();
    }

    /**
     * Merges the given entries into the existing data (overwrites existing keys) and saves.
     *
     * @param entries Map of entries to add. If null, does nothing.
     * @throws IOException If save fails.
     */
    public void addEntries(Map<String, Object> entries) throws IOException {
        if (entries == null) return;
        data.putAll(entries);
        save();
    }

    /**
     * Clears all data and saves (writes an empty YAML file).
     *
     * @throws IOException If save fails.
     */
    public void clear() throws IOException {
        data.clear();
        save();
    }

    /**
     * Checks if the data is empty (no root keys).
     *
     * @return true if data is empty.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Checks if a root-level key exists.
     *
     * @param key Root key to check.
     * @return true if the key exists at root.
     */
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    private DumperOptions getDumperOptions() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setIndent(4);
        dumperOptions.setCanonical(false);
        dumperOptions.setAllowReadOnlyProperties(false);
        dumperOptions.setLineBreak(DumperOptions.LineBreak.UNIX);
        return dumperOptions;
    }
}