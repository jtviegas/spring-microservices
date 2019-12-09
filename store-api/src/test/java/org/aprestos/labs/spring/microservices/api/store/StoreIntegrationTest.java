package org.aprestos.labs.spring.microservices.api.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.lang.String.format;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StoreIntegrationTest {

	private static final String TMP = "/tmp/";
	private final String STORE_ENDPOINT = "http://localhost:7700/store/files";
	private final String STORE_ENDPOINT_SINGULAR = "http://localhost:7700/store/files/{filename}";

	private final Set<Path> input = new HashSet<>();
	@Value("${org.aprestos.labs.spring.microservices.fsstore.test.medium-sized-file}")
	private String mediumSizedFile;
	@Value("${org.aprestos.labs.spring.microservices.fsstore.test.big-sized-file}")
	private String bigSizedFile;

	@Autowired
	private com.fasterxml.jackson.databind.ObjectMapper jsonMapper;

	@Before
	public void init() throws IOException {
		Unirest.config().setObjectMapper(new ObjectMapper() {

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jsonMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return jsonMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});

		input.add( Paths.get(mediumSizedFile) );
		input.add( Paths.get(bigSizedFile) );

/*		Optional<Path> assortedFile = Files.walk(Paths.get(System.getProperty("java.io.tmpdir")), 1)
				.filter(path -> !path.toFile().isDirectory()).findAny();

		if(assortedFile.isPresent())
			input.add( assortedFile.get() );*/

	}

	private static class ResourceReader implements Function<RawResponse, Optional<File>> {

		private final String file;

		public ResourceReader(String filename){
			this.file = filename;
		}

		@Override
		public Optional<File> apply(RawResponse response) {
			Optional<File> result = Optional.empty();
			BufferedInputStream inbs = null;
			BufferedOutputStream outbs = null;
			try {
				File fPointer = Paths.get(this.file).toFile();
				inbs = new BufferedInputStream(response.getContent());
				outbs = new BufferedOutputStream(new FileOutputStream(fPointer));
				byte[] buff = new byte[32 * 1024];
				int len = 0;
				while ((len = inbs.read(buff)) > 0) //If necessary readLine()
					outbs.write(buff, 0, len);

				result = Optional.of(fPointer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
					try {
						if( null != inbs ) {
							inbs.close();
						}
						if( null != outbs ) {
							outbs.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			return result;
		}
	}

	@Test
	public void test_allfiles() throws IOException, UnirestException {


		List<String> list = jsonMapper.readValue(Unirest.get(STORE_ENDPOINT)
				.asString().getBody(), new TypeReference<List<String>>() {});
		int storeSize = list.size();

		for( Path path: input ){

			log.info("testing storage of file {}", path.toString());
			File file = path.toFile();

			HttpResponse<Void> response = Unirest.post(STORE_ENDPOINT)
					.field("file", file).asEmpty();
			Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

			String tmpFile = format("%s%s", TMP, file.getName());

			Optional<File> response2 = Unirest.get(STORE_ENDPOINT_SINGULAR)
					.routeParam("filename", file.getName())
					.asObject( new ResourceReader(tmpFile) ).getBody();
			Assert.assertTrue(response2.isPresent());

			HttpResponse<JsonNode> response3 = Unirest.delete(STORE_ENDPOINT_SINGULAR)
					.routeParam("filename", response2.get().getName()).asJson();
			Assert.assertEquals(HttpStatus.OK.value(), response3.getStatus());
		}

		list = jsonMapper.readValue(Unirest.get(STORE_ENDPOINT)
				.asString().getBody(), new TypeReference<List<String>>() {});
		Assert.assertEquals(storeSize, list.size());


	}
}
