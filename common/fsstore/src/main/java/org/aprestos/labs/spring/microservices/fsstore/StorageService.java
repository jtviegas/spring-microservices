package org.aprestos.labs.spring.microservices.fsstore;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public interface StorageService {

    void init(String rootLocation);

    Set<String> list();

    void store(InputStream input, String name);

    Stream<Path> loadAll();

    InputStream load(String filename);

    void delete( String name );

    void deleteAll();

}
