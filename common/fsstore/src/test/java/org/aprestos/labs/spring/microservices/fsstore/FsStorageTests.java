package org.aprestos.labs.spring.microservices.fsstore;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class FsStorageTests {

  @Autowired
  private StorageService storageService;

  @Value("${org.aprestos.labs.spring.microservices.fsstore.rootLocation}")
  private String rootLocation;

  @Value("${org.aprestos.labs.spring.microservices.fsstore.test.medium-sized-file}")
  private String mediumSizedFile;
  @Value("${org.aprestos.labs.spring.microservices.fsstore.test.big-sized-file}")
  private String bigSizedFile;

  private static final Set<Path> input = new HashSet<>();


  @Before
  public void init() throws IOException {

    storageService.init(rootLocation);
    input.add( Paths.get(mediumSizedFile) );
    input.add( Paths.get(bigSizedFile) );


    Optional<Path> assortedFile = Files.walk(Paths.get(System.getProperty("java.io.tmpdir")), 1)
            .filter(path -> !path.toFile().isDirectory()).findAny();

    if(assortedFile.isPresent())
      input.add( assortedFile.get() );

  }


  @Test
  public void test_allfiles() throws IOException {

    for( Path path: input ){
      log.info("testing storage of file {}", path.toString());
      File file = path.toFile();
      storageService.store( new FileInputStream(file), file.getName() );
      Optional<Path> found = storageService.loadAll().filter(o -> o.toFile().getName().equals(file.getName())).findFirst();
      Assert.assertTrue(format("failed to store and find file %s", file.getName()), found.isPresent());
    }

    Assert.assertEquals(input.size(), storageService.list().size() );
    storageService.deleteAll();
    Assert.assertEquals(0, storageService.list().size());

  }




}
