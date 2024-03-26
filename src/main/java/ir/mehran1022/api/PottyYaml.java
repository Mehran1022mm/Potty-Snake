package ir.mehran1022.api;

import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple library for managing YAML files using SnakeYAML.
 * @author Mehran1022
 * @version 1.0
 * @link <a href="https://potty-snake.github.io">Javadoc</a>
 */
@SuppressWarnings({"unchecked", "unused"})
public final class PottyYaml {

    @Getter
    private final Yaml snakeYaml;
    private final String filePath;
    private Map<String, Object> data;

    public PottyYaml(String filePath) throws IOException {
        this.snakeYaml = new Yaml(getDumperOptions());
        data = new LinkedHashMap<>();
        this.filePath = filePath;

        load();
        save();
    }

    /**
     * Loads the YAML content from the specified file path into the data map.
     * This method reads the file content as a string and uses the SnakeYAML library to parse it into a map.
     * If the file content is not in a valid YAML format, the parser may throw an exception.
     *
     * @throws IOException If there is an issue reading the file, such as if the file does not exist or is not accessible.
     */
    public void load() throws IOException {
        String content = Files.readString(Path.of(filePath));
        data = getSnakeYaml().load(content);
    }

    /**
     * Saves the current data map content into the YAML file at the specified file path.
     * This method converts the map into a YAML-formatted string using the SnakeYAML library and writes it to the file.
     * If there are issues during writing to the file, such as lack of write permissions or disk space, an IOException may be thrown.
     *
     * @throws IOException If there is an issue writing to the file.
     */
    public void save() throws IOException {
        String content = getSnakeYaml().dump(data);
        Files.writeString(Path.of(filePath), content);
    }

    /**
     * Retrieves an entry from the YAML data.
     *
     * @param key      The key for the entry, which can be a simple or nested key.
     * @return The value of the entry, or null if not found.
     */
    public Object getEntry(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);

            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return null; // The key does not exist in a structure
            }
        }

        return currentMap.get(keys[keys.length - 1]);
    }

    /**
     * Adds or updates an entry in the YAML data.
     *
     * @param key      The key for the entry, which can be a simple or nested key.
     * @param value    The value to set for the entry.
     */
    public void setEntry(String key, Object value) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object mapValue = currentMap.get(keys[i]);

            if (!(mapValue instanceof Map)) {
                // Create a new map if the key does not exist or is not a map
                Map<String, Object> newMap = new LinkedHashMap<>();
                currentMap.put(keys[i], newMap);
                currentMap = newMap;
            } else {
                currentMap = (Map<String, Object>) mapValue;
            }
        }

        currentMap.put(keys[keys.length - 1], value);
    }

    /**
     * Removes an entry from the YAML data.
     *
     * @param key      The key for the entry, which can be a simple or nested key.
     */
    public void removeEntry(String key) {
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
    }

    /**
     * Creates a new section in the YAML data. If the section already exists, it will be overwritten.
     *
     * @param section  The section key, which can be a simple or nested key.
     */
    public void createSection(String section) {
        setEntry(section, new LinkedHashMap<>());
    }

    /**
     * Checks if a section exists in the YAML data.
     *
     * @param section  The section key, which can be a simple or nested key.
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
    public void renameSection(String oldSection, String newSection) {
        if (hasSection(oldSection)) {
            Object sectionData = getEntry(oldSection);
            removeEntry(oldSection);
            setEntry(newSection, sectionData);
        }
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
