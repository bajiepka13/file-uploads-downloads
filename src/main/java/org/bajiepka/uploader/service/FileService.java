package org.bajiepka.uploader.service;

import io.swagger.annotations.Api;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Api(value = "files")
public class FileService {

    Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${upload-folder}")
    private String UPLOAD_FOLDER;

    public String saveUploadedFiles(MultipartFile file) {

        if (file.isEmpty()) throw new IllegalStateException("Загрузка пустых файлов запрещена.");

        try {

            byte[] bytes = file.getBytes();
            String fileName = UUID.nameUUIDFromBytes(bytes) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

            Path path = Path.of(UPLOAD_FOLDER, fileName);

            if (!Files.exists(path)) {
                Files.write(path, bytes);
            } else {
                logger.info("Файл с таким содержимым уже существует. Возвращаю UUID имеющегося файла.");
            }

            return fileName;

        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сгенерировать уникальный идентификатор файла.");
        }
    }

    public File getFileByUuid(String identifier) throws FileNotFoundException {
        File file = new File(UPLOAD_FOLDER + identifier);
        if (file.exists()) {
            return file;
        } else {
            throw new FileNotFoundException("Запраиваемый файл не найден");
        }
    }
}
