package ir.mehran1022.api;

import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A robust library for managing YAML files utilizing the SnakeYAML library.
 * This class provides methods to load, save, and manipulate YAML data with ease.
 *
 * @author Mehran1022
 * @version 1.2
 */
@SuppressWarnings({"unchecked", "unused"})
public final class PottySnake {


    private @Getter final Yaml snakeYaml;
    private @Getter final String filePath;
    private @Getter final boolean threadSafe;
    private Map<String, Object> data;

    /**
     * Constructs a new PottySnake instance associated with the given file path.
     * It initializes the parser and loads the existing YAML content into memory.
     *
     * @param filePath   The path to the YAML file to manage.
     * @param threadSafe If true, the thread-safe methods will execute.
     * @throws IOException If the file cannot be read or written to.
     */
    public PottySnake(String filePath, boolean threadSafe) throws IOException {
        this.filePath = filePath;
        this.threadSafe = threadSafe;
        data = new ConcurrentHashMap<>();
        this.snakeYaml = new Yaml(getDumperOptions());
        load();
        save();
    }

    private void normalLoad() throws IOException {
        String content = Files.readString(Path.of(filePath));
        Map<String, Object> loadedData = snakeYaml.load(content);
        data.clear();
        data = Objects.requireNonNullElseGet(loadedData, ConcurrentHashMap::new);
    }

    private synchronized void synchronizedLoad() {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            try {
                String content = Files.readString(Path.of(filePath));
                Map<String, Object> loadedData = snakeYaml.load(content);
                data.clear();
                data = Objects.requireNonNullElseGet(loadedData, ConcurrentHashMap::new);
            } catch (IOException e) {
                System.err.println("problem with I/O process \n" + Arrays.asList(e.getStackTrace()));
            }
        });

        executorService.shutdown();
    }

    private void normalSave() throws IOException {
        String content = snakeYaml.dump(data);
        Files.writeString(Path.of(filePath), content);
    }

    private synchronized void synchronizedSave() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String content = snakeYaml.dump(data);
            try {
                Files.writeString(Path.of(filePath), content);
            } catch (IOException e) {
                System.err.println("problem with I/O process \n" + Arrays.asList(e.getStackTrace()));
            }
        });

        executorService.shutdown();
    }


    /**
     * Retrieves a value from the YAML data using a key.
     * The key can represent a nested path with dot notation.
     *
     * @param key The key to retrieve the value for.
     * @return The value, or null if the key does not exist.
     */
    public Object getEntry(String key) {
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
     * Adds an entry to the specified section in the YAML data.
     * If the section is a map, the entry is added to it.
     * If the section is a list, the value is appended to the list.
     * If the section does not exist, it is created as a map and the entry is added.
     *
     * @param section The section under which the entry will be added.
     * @param key     The key for the entry within the section, or null if adding to a list.
     * @param value   The value to add for the entry.
     */
    public void addEntry(String section, String key, Object value) throws IOException {
        Object sectionObject = data.get(section);

        if (sectionObject instanceof Map) {
            // Section exists and is a map, add the key-value pair to it
            ((Map<String, Object>) sectionObject).put(key, value);
        } else if (sectionObject instanceof List) {
            // Section exists and is a list, append the value to the list
            ((List<Object>) sectionObject).add(value);
        } else if (key == null) {
            // If key is null, assume adding to a list and create a new list with the value
            List<Object> newList = new ArrayList<>();
            newList.add(value);
            data.put(section, newList);
        } else {
            // Section does not exist or is null, create a new map and add the key-value pair
            Map<String, Object> newMap = new ConcurrentHashMap<>();
            newMap.put(key, value);
            data.put(section, newMap);
        }

        save();
    }

    /**
     * Adds or updates an entry in the YAML data.
     *
     * @param key   The key for the entry, which can be a simple or nested key.
     * @param value The value to set for the entry.
     */
    public void setEntry(String key, Object value) throws IOException {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object mapValue = currentMap.get(keys[i]);

            if (!(mapValue instanceof Map)) {
                // Create a new map if the key does not exist or is not a map
                Map<String, Object> newMap = new ConcurrentHashMap<>();
                currentMap.put(keys[i], newMap);
                currentMap = newMap;
            } else {
                currentMap = (Map<String, Object>) mapValue;
            }
        }

        currentMap.put(keys[keys.length - 1], value);
        save();
    }

    /**
     * Removes an entry from the YAML data.
     *
     * @param key The key for the entry, which can be a simple or nested key.
     */
    public void removeEntry(String key) throws IOException {
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
     * Creates a new section in the YAML data. If the section already exists, it will be overwritten.
     *
     * @param section The section key, which can be a simple or nested key.
     */
    public void createSection(String section) throws IOException {
        if (data.get(section) instanceof Map) {
            return; // Section already exists as a map, do nothing
        }

        data.put(section, null);
        save();
    }

    /**
     * Checks if a section exists in the YAML data.
     *
     * @param section The section key, which can be a simple or nested key.
     * @return true if the section exists, false otherwise.
     */
    public boolean hasSection(String section) {
        Object value = getEntry(section);
        return value instanceof Map;
    }

    /**
     * Renames a section in the YAML data.
     *
     * @param oldSection The current section key.
     * @param newSection The new section key.
     */
    public void renameSection(String oldSection, String newSection) throws IOException {
        if (hasSection(oldSection)) {
            Object sectionData = getEntry(oldSection);
            removeEntry(oldSection);
            setEntry(newSection, sectionData);
        }
    }

    /**
     * Creates a new list under the specified section in the YAML data.
     * If the section already exists, it will be overwritten with a new list.
     *
     * @param section The section key, which can be a simple or nested key.
     */
    public void createList(String section) throws IOException {
        // Check if the section already exists and is a list
        if (data.get(section) instanceof List) {
            return; // Section already exists as a list, do nothing
        }
        // Otherwise, create a new list under the section
        data.put(section, new ArrayList<>());
        save();
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

    /**
     * Loads the YAML content from the file into the data map.
     * If the file is empty or the content is invalid, an empty map is initialized.
     *
     * @throws IOException If the file cannot be read.
     */
    public void load() throws IOException {
        if (threadSafe) {
            synchronizedLoad();
        } else {
            normalLoad();
        }
    }

    /**
     * Saves the in-memory data map to the YAML file.
     * The data is converted to a YAML-formatted string and written to the file.
     *
     * @throws IOException If the file cannot be written to.
     */
    public void save() throws IOException {
        if (threadSafe) {
            synchronizedSave();
        } else {
            normalSave();
        }
    }
}
