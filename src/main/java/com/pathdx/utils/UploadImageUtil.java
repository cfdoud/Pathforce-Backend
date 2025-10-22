package com.pathdx.utils;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class UploadImageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadImageUtil.class);
    private static final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Async
    public String uploadUserProfileImage(
            String projectId, String bucketName, String objectName, String contents,String userName) throws IOException {
        String imageProfileFileName=objectName + userName + ".png";
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, imageProfileFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
        String formatedContent=contents.replaceAll("data:image/png;base64,","");
        byte[] content = Base64.getDecoder().decode(formatedContent);
        storage.createFrom(blobInfo, new ByteArrayInputStream(content));

        LOGGER.info(
                "Object "
                        + imageProfileFileName
                        + " uploaded to bucket "
                        + bucketName
                        + " with contents "
                        + contents);
        return imageProfileFileName;
    }


}
