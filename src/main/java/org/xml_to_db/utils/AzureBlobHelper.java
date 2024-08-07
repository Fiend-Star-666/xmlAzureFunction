package org.xml_to_db.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml_to_db.core.handlers.ErrorHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AzureBlobHelper {
    private static final Logger logger = LoggerFactory.getLogger(AzureBlobHelper.class);
    private final BlobServiceClient blobServiceClient;

    public AzureBlobHelper(String connectionString) {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    public void uploadBlob(String containerName, String blobName, String content) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try (InputStream dataStream = new ByteArrayInputStream(content.getBytes())) {
            blobClient.upload(dataStream, content.length());
            logger.info("Uploaded blob {} to container {}", blobName, containerName);
        } catch (Exception e) {
            ErrorHandler.handleException("Error uploading blob" + blobName + "to container + containerName", e);
        }
    }

    public String downloadBlob(String containerName, String blobName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobClient.downloadStream(outputStream);
            String content = outputStream.toString();
            logger.info("Downloaded blob {} from container {}", blobName, containerName);
            return content;
        } catch (Exception e) {
            ErrorHandler.handleException("Error uploading blob" + blobName + "to container + containerName", e);
        }
        return "Failed";
    }

    public List<String> listBlobs(String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        List<String> blobNames = new ArrayList<>();

        try {
            for (BlobItem blobItem : containerClient.listBlobs()) {
                blobNames.add(blobItem.getName());
            }
            logger.info("Listed {} blobs in container {}", blobNames.size(), containerName);
            return blobNames;
        } catch (Exception e) {
            ErrorHandler.handleException("Error listing blobs in container" + containerName, e);
        }
        return blobNames;
    }

    public void deleteBlob(String containerName, String blobName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try {
            blobClient.delete();
            logger.info("Deleted blob {} from container {}", blobName, containerName);
        } catch (Exception e) {
            ErrorHandler.handleException("Error deleting blob " + blobName + " from container " + containerName, e);
        }
    }

    public boolean blobExists(String containerName, String blobName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try {
            boolean exists = blobClient.exists();
            logger.info("Checked existence of blob {} in container {}. Exists: {}", blobName, containerName, exists);
            return exists;
        } catch (Exception e) {
            ErrorHandler.handleException("Error checking existence of blob " + blobName + " in container" + containerName, e);
        }
        return false;
    }

    public String getBlobUrl(String containerName, String blobName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        String blobUrl = blobClient.getBlobUrl();
        logger.info("Got URL for blob {} in container {}: {}", blobName, containerName, blobUrl);
        return blobUrl;
    }
}
