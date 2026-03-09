package io.github.tawdi.jobboard.candidate_profile.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CvStorageService Tests")
class CvStorageServiceTest {

    @TempDir
    Path tempDir;

    private CvStorageService cvStorageService;
    private final String TEST_USER_ID = "user-123-456";

    @BeforeEach
    void setUp() {
        cvStorageService = new CvStorageService(tempDir.toString());
    }

    @Nested
    @DisplayName("File Storage Operations")
    class FileStorageOperations {

        @Test
        @DisplayName("Should store file successfully")
        void shouldStoreFile_WhenValidFile() throws IOException {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-cv.pdf",
                    "application/pdf",
                    "PDF content for testing".getBytes()
            );

            // When
            String storedPath = cvStorageService.storeFile(mockFile, TEST_USER_ID);

            // Then
            assertThat(storedPath).isNotNull();
            assertThat(storedPath).contains(TEST_USER_ID);
            assertThat(storedPath).endsWith(".pdf");

            // Verify file exists on filesystem
            Path actualPath = Paths.get(storedPath);
            assertThat(Files.exists(actualPath)).isTrue();

            // Verify file content
            byte[] storedContent = Files.readAllBytes(actualPath);
            assertThat(storedContent).isEqualTo("PDF content for testing".getBytes());
        }

        @Test
        @DisplayName("Should handle file with no extension")
        void shouldStoreFile_WhenFileHasNoExtension() {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-cv",  // No extension
                    "application/pdf",
                    "PDF content".getBytes()
            );

            // When
            String storedPath = cvStorageService.storeFile(mockFile, TEST_USER_ID);

            // Then
            assertThat(storedPath).isNotNull();
            assertThat(storedPath).contains(TEST_USER_ID);
            // Should use UUID format when no extension
            assertThat(Paths.get(storedPath)).exists();
        }

        @Test
        @DisplayName("Should throw exception when file storage fails")
        void shouldThrowException_WhenFileStorageFails() {
            // Given
            MultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-cv.pdf",
                    "application/pdf",
                    "PDF content".getBytes()
            ) {
                @Override
                public java.io.InputStream getInputStream() throws IOException {
                    throw new IOException("Simulated IO exception");
                }
            };

            // When & Then
            assertThatThrownBy(() -> cvStorageService.storeFile(mockFile, TEST_USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Could not store file");
        }

        @Test
        @DisplayName("Should load file successfully")
        void shouldLoadFile_WhenFileExists() throws IOException {
            // Given - First store a file
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-cv.pdf",
                    "application/pdf",
                    "PDF content for loading test".getBytes()
            );
            String storedPath = cvStorageService.storeFile(mockFile, TEST_USER_ID);

            // When
            byte[] loadedContent = cvStorageService.loadFile(storedPath);

            // Then
            assertThat(loadedContent).isNotNull();
            assertThat(loadedContent).isEqualTo("PDF content for loading test".getBytes());
        }

        @Test
        @DisplayName("Should throw exception when loading non-existent file")
        void shouldThrowException_WhenLoadingNonExistentFile() {
            // Given
            String nonExistentPath = tempDir.resolve("non-existent-file.pdf").toString();

            // When & Then
            assertThatThrownBy(() -> cvStorageService.loadFile(nonExistentPath))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Could not load file");
        }

        @Test
        @DisplayName("Should delete file successfully")
        void shouldDeleteFile_WhenFileExists() throws IOException {
            // Given - First store a file
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-cv.pdf",
                    "application/pdf",
                    "PDF content for deletion test".getBytes()
            );
            String storedPath = cvStorageService.storeFile(mockFile, TEST_USER_ID);

            // Verify file exists before deletion
            assertThat(Files.exists(Paths.get(storedPath))).isTrue();

            // When
            cvStorageService.deleteFile(storedPath);

            // Then
            assertThat(Files.exists(Paths.get(storedPath))).isFalse();
        }

        @Test
        @DisplayName("Should handle deletion of non-existent file gracefully")
        void shouldHandleGracefully_WhenDeletingNonExistentFile() {
            // Given
            String nonExistentPath = tempDir.resolve("non-existent-file.pdf").toString();

            // When & Then - Should not throw exception
            assertThatCode(() -> cvStorageService.deleteFile(nonExistentPath))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should generate unique filenames for same user")
        void shouldGenerateUniqueFilenames_WhenSameUserUploadsMultipleTimes() {
            // Given
            MockMultipartFile mockFile1 = new MockMultipartFile(
                    "file",
                    "cv.pdf",
                    "application/pdf",
                    "First PDF content".getBytes()
            );
            MockMultipartFile mockFile2 = new MockMultipartFile(
                    "file",
                    "cv.pdf",
                    "application/pdf",
                    "Second PDF content".getBytes()
            );

            // When
            String path1 = cvStorageService.storeFile(mockFile1, TEST_USER_ID);
            String path2 = cvStorageService.storeFile(mockFile2, TEST_USER_ID);

            // Then
            assertThat(path1).isNotEqualTo(path2);
            assertThat(Files.exists(Paths.get(path1))).isTrue();
            assertThat(Files.exists(Paths.get(path2))).isTrue();
        }
    }

    @Nested
    @DisplayName("Directory Management")
    class DirectoryManagement {

        @Test
        @DisplayName("Should create upload directory on initialization")
        void shouldCreateUploadDirectory_OnInitialization() {
            // Given
            Path newTempDir = tempDir.resolve("new-upload-dir");
            assertThat(Files.exists(newTempDir)).isFalse();

            // When
            CvStorageService newService = new CvStorageService(newTempDir.toString());

            // Then
            assertThat(Files.exists(newTempDir)).isTrue();
            assertThat(Files.isDirectory(newTempDir)).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when directory creation fails")
        void shouldThrowException_WhenDirectoryCreationFails() {
            // Given - Try to create directory in a non-existent parent that can't be created
            // This simulates permission issues or other filesystem problems
            String invalidPath = "/invalid/path/that/cannot/be/created";

            // When & Then
            assertThatThrownBy(() -> new CvStorageService(invalidPath))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Could not create upload directory");
        }
    }

    @Nested
    @DisplayName("File Type Validation")
    class FileTypeValidation {

        @Test
        @DisplayName("Should save all files with .pdf extension")
        void shouldSaveAllFilesWithPdfExtension() {
            // Given
            MockMultipartFile pdfFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "content".getBytes());
            MockMultipartFile docFile = new MockMultipartFile(
                    "file", "test.doc", "application/msword", "content".getBytes());
            MockMultipartFile docxFile = new MockMultipartFile(
                    "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "content".getBytes());

            // When
            String pdfPath = cvStorageService.storeFile(pdfFile, TEST_USER_ID);
            String docPath = cvStorageService.storeFile(docFile, TEST_USER_ID);
            String docxPath = cvStorageService.storeFile(docxFile, TEST_USER_ID);

            // Then - All files should be saved with .pdf extension
            assertThat(pdfPath).endsWith(".pdf");
            assertThat(docPath).endsWith(".pdf");
            assertThat(docxPath).endsWith(".pdf");

            // All files should exist
            assertThat(Files.exists(Paths.get(pdfPath))).isTrue();
            assertThat(Files.exists(Paths.get(docPath))).isTrue();
            assertThat(Files.exists(Paths.get(docxPath))).isTrue();
        }

        @Test
        @DisplayName("Should preserve original filename information in path")
        void shouldPreserveOriginalFilenameInfo_InStoredPath() {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "john_doe_resume_2024.pdf",
                    "application/pdf",
                    "PDF content".getBytes()
            );

            // When
            String storedPath = cvStorageService.storeFile(mockFile, TEST_USER_ID);

            // Then
            assertThat(storedPath).contains(TEST_USER_ID);
            assertThat(storedPath).endsWith(".pdf");
            // Should contain some unique identifier (UUID-like pattern)
            assertThat(storedPath).matches(".*[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}.*");
        }
    }
}
