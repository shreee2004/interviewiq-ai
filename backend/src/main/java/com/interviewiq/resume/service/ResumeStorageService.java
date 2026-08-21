package com.interviewiq.resume.service;

import com.interviewiq.common.exception.InvalidFileException;
import com.interviewiq.config.InterviewIqProperties;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Local-disk storage behind a stable, backend-agnostic interface (see
 * {@link InterviewIqProperties.Storage}) — swapping in S3-compatible object storage for
 * production later only touches this class.
 */
@Service
public class ResumeStorageService {

    private static final byte[] PDF_MAGIC_BYTES = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final Path uploadRoot;

    public ResumeStorageService(InterviewIqProperties properties) {
        this.uploadRoot = Path.of(properties.storage().resumeUploadDir());
    }

    /** @return the stored file's path, relative to the storage root — this is what gets persisted as {@code file_url}. */
    public String store(UUID userId, UUID resumeId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileException("Only PDF files are accepted");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to read uploaded resume", ex);
        }
        // The client-supplied Content-Type header is just a claim, not a guarantee — check
        // the actual file signature too, so a mislabeled/malicious upload can't slip through.
        if (bytes.length < PDF_MAGIC_BYTES.length
                || !Arrays.equals(bytes, 0, PDF_MAGIC_BYTES.length, PDF_MAGIC_BYTES, 0, PDF_MAGIC_BYTES.length)) {
            throw new InvalidFileException("File does not look like a valid PDF");
        }

        String relativePath = userId + "/" + resumeId + ".pdf";
        Path destination = uploadRoot.resolve(relativePath);
        try {
            Files.createDirectories(destination.getParent());
            Files.write(destination, bytes);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to store uploaded resume", ex);
        }
        return relativePath;
    }

    public void delete(String relativePath) {
        try {
            Files.deleteIfExists(uploadRoot.resolve(relativePath));
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to delete stored resume", ex);
        }
    }
}
