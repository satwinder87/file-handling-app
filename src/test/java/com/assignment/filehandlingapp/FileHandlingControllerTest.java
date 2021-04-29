package com.assignment.filehandlingapp;


import com.assignment.filehandlingapp.api.FileHandlingController;
import com.assignment.filehandlingapp.domain.FileMetaData;
import com.assignment.filehandlingapp.repository.FileRepository;
import com.assignment.filehandlingapp.service.FileHandlingService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@RunWith(SpringRunner.class)
@WebMvcTest(FileHandlingController.class)
public class FileHandlingControllerTest {
    //Holds test data for mocking
    private Map<String,FileMetaData> files = new Hashtable<>();

    @MockBean
    private FileHandlingService service;
    @MockBean
    private FileRepository repository;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void createTestData(){
        files.put("test.jpg",new FileMetaData("test.jpg",new String[]{"Accounts","Contracts"}));
        files.put("sample.pdf",new FileMetaData("sample.pdf",new String[]{"Accounts"}));
        files.put("resume.doc",new FileMetaData("resume.doc",new String[]{"Accounts","Logistics"}));
    }

    @Test
    public void testGetAllFiles() throws Exception{
        when(repository.getAllFiles()).thenReturn(getAllFiles());
        mockMvc.perform(get("/files")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",Matchers.hasSize(3)));
    }

    @Test
    public void testGetFilesWithCategory() throws Exception{
        when(repository.getAllFilesWithGivenCategory(eq("Contracts"))).thenReturn(getAllFilesWithGivenCategory("Contracts"));
        mockMvc.perform(get("/files/Contracts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fileName",Matchers.is(files.get("test.jpg").getFileName())));
    }

    @Test
    public void testUploadFile() throws Exception{
        String fileName = "car.jpg";
        InputStream inputStream = this.getClass().getResourceAsStream(fileName);
        MockMultipartFile multipartFile = new MockMultipartFile("file",inputStream);

        when(service.storeFile(any(MultipartFile.class))).thenReturn(fileName);

        mockMvc.perform(multipart("/files/upload")
                .file(multipartFile)
                .param("fileCategory","Accounts")
                .param("fileCategory","Contracts")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(multipartFile.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(service,atMost(1)).storeFile(any(MultipartFile.class));
        verify(repository).addFile(eq(fileName), any(FileMetaData.class));
    }

    @Test
    public void testDownloadFile()throws Exception{
        String fileName = "car.jpg";
        Path path = Paths.get("./src/test/resources/test-files/" + fileName) .toAbsolutePath().normalize();
        UrlResource resource = new UrlResource(path.toUri());

        when(service.loadFileAsResource(eq(fileName))).thenReturn(resource);

        mockMvc.perform(get("/files/{fileName}/download",fileName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG));

    }

    private Collection<FileMetaData> getAllFiles() {
        return files.values();
    }

    private Collection<FileMetaData> getAllFilesWithGivenCategory(String fileCategory) {
        return files.values().stream()
                .filter(fileMetaData -> Arrays.stream(fileMetaData.getFileCategories()).anyMatch(category -> category.equalsIgnoreCase(fileCategory)))
                .collect(Collectors.toList());
    }

}
