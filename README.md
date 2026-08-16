# 🐍 Potty-Snake

Potty-Snake is a Java library that simplifies the creation, manipulation, and management of YAML files. Leveraging SnakeYAML, it provides an accessible and efficient way to handle YAML data for applications of any scale.

## Features

- **Simple API** – load, save, read, write, and modify YAML with minimal boilerplate.
- **Dot‑notation paths** – access nested values like `database.host` or `users[0].name`.
- **Typed getters** – retrieve values as `String`, `Integer`, `Long`, `Double`, `Boolean`, `List`, or `Map` without casting.
- **Section & list management** – create, check, rename, and delete entire sections or lists.
- **Batch operations** – `setEntries(Map)` and `addEntries(Map)` to update multiple keys with a single save.
- **Custom formatting** – pass your own `DumperOptions` to control indentation, flow style, etc.
- **Reload** – discard in‑memory changes and re‑read the file from disk.
- **Lightweight** – only two dependencies: SnakeYAML and Lombok (compile‑only).

## 📦 Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ir.mehran1022.api</groupId>
    <artifactId>potty-snake</artifactId>
    <version>1.4</version>
</dependency>
```

Or just copy `PottySnake.java` into your project and add the dependencies manually.

## 🚀 Quick Start

```java
import ir.mehran1022.api.PottySnake;

// Load or create a YAML file
PottySnake yaml = new PottySnake("config.yml");

// Read a value
String host = yaml.getString("database.host");
int port = yaml.getInt("database.port");

// Write a value
yaml.setEntry("database.timeout", 30);

// Add a nested section
yaml.createSection("logging.appenders");

// Add a list
yaml.createList("logging.appenders.file");
yaml.addEntry("logging.appenders.file", null, "console");
yaml.addEntry("logging.appenders.file", null, "file");

// Reload from disk (discard changes)
yaml.reload();

// Save explicitly
yaml.save();
```

## 📚 Detailed Usage

### Creating an Instance

```java
// Default formatting (block style, indent 4, UNIX line breaks)
PottySnake yaml = new PottySnake("path/to/file.yml");

// Custom DumperOptions
DumperOptions options = new DumperOptions();
options.setIndent(2);
options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
PottySnake yaml = new PottySnake("file.yml", options);
```

### Reading Values

| Method | Return type | Behaviour |
|--------|-------------|-----------|
| `getEntry(key)` | `Object` | Raw value, may be `Map`, `List`, `String`, `Number`, etc. |
| `getString(key)` | `String` | `null` if missing or not a `String` |
| `getInt(key)` | `Integer` | `null` if missing or not a number |
| `getLong(key)` | `Long` | same |
| `getDouble(key)` | `Double` | same |
| `getBoolean(key)` | `Boolean` | `null` if missing or not `Boolean` |
| `getList(key)` | `List<Object>` | `null` if missing or not `List` |
| `getMap(key)` | `Map<String,Object>` | `null` if missing or not `Map` |

Keys use dot notation, e.g. `"a.b.c"` accesses `a -> b -> c`.

### Writing / Updating

- `setEntry(key, value)` – writes any object at the given path, creating intermediate maps as needed. Throws if an intermediate path is not a `Map`.
- `addEntry(section, key, value)` – adds to a top‑level section:
  - If `section` is a `Map`, `key` must not be `null`; the pair is inserted.
  - If `section` is a `List`, `key` must be `null`; the value is appended.
  - If `section` does not exist, a `Map` is created if `key != null`, else a `List`.

### Section Management

```java
yaml.createSection("server.ssl");      // nested map
yaml.createList("server.hosts");       // nested list
boolean exists = yaml.hasSection("server");
yaml.renameSection("old", "new");
yaml.removeEntry("server.hosts");
```

### Batch Operations

```java
Map<String, Object> updates = new LinkedHashMap<>();
updates.put("app.name", "MyApp");
updates.put("app.version", 2.0);
yaml.setEntries(updates);   // replaces whole data
// or
yaml.addEntries(updates);   // merges into existing
```

### Clearing & Checking

```java
yaml.clear();                // wipe all data and save
boolean empty = yaml.isEmpty();
boolean hasKey = yaml.containsKey("app");
```

### Reload & Save

- `load()` – (re‑)reads the file; if missing, creates an empty file.
- `reload()` – same, but throws if file does not exist (useful for discarding unsaved changes).
- `save()` – writes the current in‑memory state to disk.

All modifying methods (`setEntry`, `addEntry`, `removeEntry`, `createSection`, `createList`, `clear`, etc.) automatically call `save()` after the change. Use `reload()` to discard them.

## 🔧 Advanced: Custom DumperOptions

Pass a `DumperOptions` instance to control output:

```java
DumperOptions opts = new DumperOptions();
opts.setIndent(2);
opts.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
opts.setLineBreak(DumperOptions.LineBreak.WINDOWS);
PottySnake yaml = new PottySnake("file.yml", opts);
// later
yaml.setDumperOptions(newOpts);  // change at runtime
```

## 📖 Javadoc

Full API documentation is available in the `docs/javadoc/` directory. Open `docs/javadoc/index.html` in your browser.

## 📄 License

MIT License – see the `LICENSE` file.

## 🤝 Contributing

Issues and pull requests are welcome. Please ensure your changes are tested and documented.

## 🙏 Acknowledgments

- [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml) for the YAML engine.
- All contributors who help maintain this project.

---

Maintained with ♥ by Mehran1022.