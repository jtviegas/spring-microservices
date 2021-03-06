package org.aprestos.labs.spring.microservices.api.store.resources;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.apiutils.exceptions.ApiException;
import org.aprestos.labs.spring.microservices.fsstore.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import springfox.documentation.service.ApiListing;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/store/files")
@Api(tags = { "fileStore api" }, value = "API root for fileStore facade")
@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value"),
        @ApiResponse(code = 500, message = "Internal server error") })
public class Files {

    private final StorageService storageService;

    @Autowired
    public Files (StorageService storageService, @Value("${org.aprestos.labs.spring.microservices.fsstore.rootLocation}") String rootLocation){
        this.storageService = storageService;
        this.storageService.init(rootLocation);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Used to post a file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation" ) })
    public ResponseEntity<Void> postFile(@RequestBody MultipartFile file) throws ApiException {
        log.trace("[postFile|in] => {}", file.getOriginalFilename());
        try {
            storageService.store(file.getInputStream(), StringUtils.cleanPath(file.getOriginalFilename()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("[postFile]", e);
            throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            log.trace("[postFile|out]");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Used to get the list of uploaded files")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = String.class, responseContainer = "List" )
            , @ApiResponse(code = 404, message = "not found")})
    public ResponseEntity<List<String>> listUploadedFiles() throws ApiException {

        log.trace("[listUploadedFiles|in]");
        try {
            return new ResponseEntity<>(storageService.loadAll().map(
                    path -> MvcUriComponentsBuilder.fromMethodName(Files.class,
                            "serveFile", path.getFileName().toString()).build().toString())
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("[listUploadedFiles]", e);
            throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            log.trace("[listUploadedFiles|out]");
        }

    }

    @RequestMapping(value = "/{filename}", method = RequestMethod.GET)
    @ApiOperation(value = "Used to get a specific file")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Resource.class)
            , @ApiResponse(code = 404, message = "not found")})
    public ResponseEntity<Resource> serveFile(@NotNull @ApiParam(required = true) @PathVariable String filename) throws ApiException{
        log.trace("[serveFile|in]({})", filename);
        try {
            InputStreamResource resource = new InputStreamResource(storageService.load(filename));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                    .body(resource);
        } catch (Exception e) {
            log.error("[serveFile]", e);
            throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            log.trace("[serveFile|out]");
        }
    }

    @RequestMapping(value = "/{filename}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Used to get a specific file")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Resource.class)
            , @ApiResponse(code = 404, message = "not found")})
    public ResponseEntity<Void> eraseFile(@NotNull @ApiParam(required = true) @PathVariable String filename) throws ApiException{
        log.trace("[eraseFile|in]({}) =>", filename);

        try {
            storageService.delete(filename);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("[eraseFile]", e);
            throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            log.trace("[eraseFile|out]");
        }

    }




}

