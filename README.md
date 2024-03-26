# 🐍 Potty-Snake

Potty-Snake is a Java library that simplifies the creation, manipulation, and management of YAML files. Leveraging the power of SnakeYAML, it provides an accessible and efficient way to handle YAML data for applications of any scale.

## Features

- **Easy to Use**: Simple API for reading and writing YAML.
- **Efficient**: Optimized for performance with large files.
- **Flexible**: Supports complex YAML structures, including nested objects.
- **Reliable**: Built on the robust SnakeYAML engine.

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven (Optional)

### Installation

Add the following dependency to your `pom.xml` for Maven:

```xml
<dependency>
    <groupId>ir.mehran1022.api</groupId>
    <artifactId>potty-snake</artifactId>
    <version>1.0.0</version>
</dependency>
```
Or just copy & paste the `PottySnake.java` class.

### Usage

Create an instance of PottySnake and use it to load, manipulate, and save YAML data:

```java
PottySnake pottySnake = new PottySnake("path/to/file.yaml");
pottySnake.load();
pottySnake.save();

// Example utility method
Object object = pottySnake.getEntry(myKey);
```

### License

Potty-Snake is released under the MIT License. See the bundled LICENSE file for details.

### Acknowledgments

- SnakeYAML, for the powerful YAML engine.
- All contributors who help maintain and improve this project.

Potty-Snake is maintained with ♥.
