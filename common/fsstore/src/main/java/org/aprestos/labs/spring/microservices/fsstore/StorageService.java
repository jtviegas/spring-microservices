package org.aprestos.labs.spring.microservices.fsstore;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public interface StorageService {

    void init(String rootLocation);

    void store(MultipartFile file);

    void store(Path path);

    void store(InputStream input, String name);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete( String name );

    void deleteAll();

    Set<String> list();
}
