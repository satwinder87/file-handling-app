package com.assignment.filehandlingapp.repository;


import com.assignment.filehandlingapp.domain.FileMetaData;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FileRepository {
    // Collect all files being uploaded, also saves metaData of each file like fileName and Category .
    private Map<String, FileMetaData> files = new Hashtable<>();

    public void addFile(String fileName, FileMetaData metaData){
        // fileName is taken as Key so as to avoid duplicate entries in the map if the same file is uploaded again.
        files.put(fileName,metaData);
    }

    public Collection<FileMetaData> getAllFiles() {
        return files.values();
    }

    public Collection<FileMetaData> getAllFilesWithGivenCategory(String fileCategory) {
        return files.values().stream()
                .filter(fileMetaData -> Arrays.stream(fileMetaData.getFileCategories()).anyMatch(category -> category.equalsIgnoreCase(fileCategory)))
                .collect(Collectors.toList());
    }
}
