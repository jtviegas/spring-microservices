package org.aprestos.labs.spring.microservices.fsstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileSystemUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Slf4j
public class FsStorageService implements StorageService {

  private static final String STORAGE_SERVICE_FOLDER = FsStorageService.class.getSimpleName();
  private Path rootLocation;


  public void init(String rootLocation){
    log.trace("[init|in] ({})", rootLocation);
    try {
      this.rootLocation = Paths.get(rootLocation, STORAGE_SERVICE_FOLDER);
      if( ! this.rootLocation.toFile().exists() )
        Files.createDirectories(this.rootLocation);

      log.info("[init] rootLocation initialized: {}", this.rootLocation);

    }
    catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
    log.trace("[init|out]");
  }

  private void verify(){
    if( null == rootLocation )
      throw new StorageException("storage service was not initialized, please do init before using the component !");
  }

  @Override
  public void store(InputStream input, String name) {
    log.trace("[store|in] (name: {})", name);
    verify();
    Path filePath = this.rootLocation.resolve(Paths.get(this.rootLocation.toFile().getAbsolutePath(), name ) );
    try {
      Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
      log.trace("[store] stored file {}", filePath.toString());
    }
    catch (IOException e) {
      throw new StorageException("Failed to store file " + filePath.toString(), e);
    }
    log.trace("[store|out]");
  }

  @Override
  public Set<String> list() {
    log.trace("[list|in]");
    verify();
    Set<String> result = null;
    try {
      if( this.rootLocation.toFile().exists() )
      result = Files.walk(this.rootLocation, 1)
              .filter(path -> !path.equals(this.rootLocation))
              .map(o -> o.toFile().getAbsolutePath()).collect(Collectors.toSet());
      else
        result = new HashSet<>();
    }
    catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
    log.trace("[list|out] => {}", result);
    return result;
  }

  @Override
  public Stream<Path> loadAll() {
    log.trace("[loadAll|in]");
    Stream<Path> result = null;
    verify();
    try {
      result = Files.walk(this.rootLocation, 1)
              .filter(path -> !path.equals(this.rootLocation));
    }
    catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
    log.trace("[loadAll|out]");
    return result;
  }

  public Path resolve(String filename) {
    log.trace("[resolve|in] ({})", filename);
    verify();
    Path result = rootLocation.resolve(filename);
    log.trace("[resolve|out] => {}", result);
    return result;
  }


  public InputStream load(String filename) {
    log.trace("[load|in] ({})", filename);
    InputStream result = null;
    verify();
    try {
      result = new FileInputStream(resolve(filename).toFile());
    } catch (FileNotFoundException e) {
      throw new StorageFileNotFoundException(format("cannot find file: %s", filename), e);
    }
    log.trace("[load|out]");
    return result;
  }


  public void delete( String name ){
    log.trace("[delete|in] ({})", name);
    verify();
    try {
      Path file = resolve(name);
      Files.deleteIfExists(file);
    }
    catch (IOException e) {
      throw new StorageException("Failed to delete stored files", e);
    }
    log.trace("[delete|out]");
  }

  @Override
  public void deleteAll() {
    log.trace("[deleteAll|in]");
    verify();
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
    log.trace("[deleteAll|out]");
  }


}
