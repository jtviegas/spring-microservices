package org.aprestos.labs.spring.microservices.fsstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FsStorageService implements StorageService {

  private Path rootLocation;

  public void init(String rootLocation){
    try {
      this.rootLocation = Paths.get(rootLocation);
      if( ! this.rootLocation.toFile().exists() )
        Files.createDirectories(this.rootLocation);
    }
    catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  private void verify(){
    if( null == rootLocation )
      throw new StorageException("storage service was not initialized, please do init before using the component !");
  }

  @Override
  public void store(MultipartFile file) {
    verify();
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file " + filename);
      }
      if (filename.contains("..")) {
        // This is a security check
        throw new StorageException(
                "Cannot store file with relative path outside current directory "
                        + filename);
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, this.rootLocation.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
      }
    }
    catch (IOException e) {
      throw new StorageException("Failed to store file " + filename, e);
    }
  }

  @Override
  public void store(InputStream input, String name) {
    verify();
    Path filePath = Paths.get(this.rootLocation.toFile().getAbsolutePath(), name );
    try {
      Files.copy(input, this.rootLocation.resolve(filePath), StandardCopyOption.REPLACE_EXISTING);
    }
    catch (IOException e) {
      throw new StorageException("Failed to store file " + filePath.toString(), e);
    }
  }

  @Override
  public void store(Path path) {
    verify();
    File file = path.toFile();
    String filename = StringUtils.cleanPath(file.getAbsolutePath());

    try {
      if (0l == file.length()) {
        throw new StorageException("Failed to store empty file " + filename);
      }
      if (filename.contains("..")) {
        // This is a security check
        throw new StorageException(
                "Cannot store file with relative path outside current directory "
                        + filename);
      }
      try (InputStream inputStream = new FileInputStream(file)) {
        Files.copy(inputStream, this.rootLocation.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
      }
    }
    catch (IOException e) {
      throw new StorageException("Failed to store file " + filename, e);
    }
  }

  @Override
  public Set<String> list() {
    verify();
    try {
      return Files.walk(this.rootLocation, 1)
              .filter(path -> !path.equals(this.rootLocation))
              .map(this.rootLocation::relativize).map(o -> o.toFile().getAbsolutePath()).collect(Collectors.toSet());
    }
    catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }

  }

  @Override
  public Stream<Path> loadAll() {
    verify();
    try {
      return Files.walk(this.rootLocation, 1)
              .filter(path -> !path.equals(this.rootLocation))
              .map(this.rootLocation::relativize);
    }
    catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }

  }

  @Override
  public Path load(String filename) {
    verify();
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String filename) {
    verify();
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      }
      else {
        throw new StorageFileNotFoundException(
                "Could not read file: " + filename);
      }
    }
    catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
  }

  public void delete( String name ){
    verify();
    try {
      Path file = load(name);
      Files.deleteIfExists(file);
    }
    catch (IOException e) {
      throw new StorageException("Failed to delete stored files", e);
    }
  }

  @Override
  public void deleteAll() {
    verify();
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }


}
