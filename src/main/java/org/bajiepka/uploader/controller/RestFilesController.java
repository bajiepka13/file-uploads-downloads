package org.bajiepka.uploader.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bajiepka.uploader.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class RestFilesController {

    private final Logger logger = LoggerFactory.getLogger(RestFilesController.class);

    @Autowired
    private FileService service;

//  ~ Upload ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @PostMapping("/upload")
    @ApiOperation(
            value = "Сохраняет переданный файл и возвращает его имя на сервере",
            notes = "Файлы могут иметь различное расширения, поэтому указание расширения является обязательным.\n" +
                    "При загрузке файла его имя подменяется уникальным идентификатором, который генерируется на осно" +
                    "вании его содержимого. Файл сохраняется с тем же расширением.",
            response = ResponseEntity.class)
    public ResponseEntity<?> uploadFile(
            @ApiParam(value = "Файл картинки или другой документ, который предполагается сохранить", required = true)
            @RequestParam("file") MultipartFile uploadFile){

        logger.debug("Загрузка файла.");

        if (uploadFile.isEmpty()) return new ResponseEntity("Файл для загрузки отсутствует.", HttpStatus.OK);

        return new ResponseEntity(service.saveUploadedFiles(uploadFile), HttpStatus.OK);
    }

//  ~ Download ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    @ApiOperation(
            value = "Возвращает файл по его имени",
            notes = "В качестве имени файла, который предполагается скачать используется значение (уникальный идентифи" +
                    "катор + расширение) полученное с помощью POST метода /upload",
            response = ResponseEntity.class)
    public ResponseEntity<Resource> download(
            @ApiParam(value = "Идентификатор запрашиваемого файла с расширением", required = true)
            @RequestParam("fileName") String identifier) throws IOException {


        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        File file = service.getFileByUuid(identifier);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
