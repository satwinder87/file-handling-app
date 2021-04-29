package com.assignment.filehandlingapp.api;


import com.assignment.filehandlingapp.domain.FileMetaData;
import com.assignment.filehandlingapp.repository.FileRepository;
import com.assignment.filehandlingapp.service.FileHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Collection;

@RestController
public class FileHandlingController {
    @Autowired
    private FileRepository repository;

    @Autowired
    private FileHandlingService service;

    @Autowired
    private ServletContext context;

    @PostMapping("/files/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileCategory") String[] fileCategories) {
        String savedFileName = service.storeFile(file);
        FileMetaData fileMetaData = new FileMetaData(savedFileName, fileCategories);
        repository.addFile(savedFileName, fileMetaData);
    }

    @GetMapping("/files")
    public Collection<FileMetaData> getAllFiles() {
        return repository.getAllFiles();
    }

    @GetMapping("/files/{fileCategory}")
    public Collection<FileMetaData> getAllFilesWithGivenCategory(@PathVariable("fileCategory") String fileCategory) {
        return repository.getAllFilesWithGivenCategory(fileCategory);
    }

    @GetMapping("/files/{fileName}/download")
    public ResponseEntity<Resource> getFile(@PathVariable("fileName") String fileName) {
        Resource resource = service.loadFileAsResource(fileName);

        // Try to determine file's content type based on file extension.
        // One can use request.getHeader("Accept") to see what the client is expecting, but this varies a lot and it's hard to deduce the right MIME type of the file to send.
        String contentType;
        try {
            contentType = context.getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";     // Set the default content type if type could not be determined
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")    // inline here means that the browser would try to open the file if possible otherwise just download it.
                .body(resource);
    }

}
