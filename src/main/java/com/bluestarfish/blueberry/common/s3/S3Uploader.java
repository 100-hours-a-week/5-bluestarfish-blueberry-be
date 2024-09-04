package com.bluestarfish.blueberry.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public String upload(MultipartFile multipartFile, String dirName) {
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");
            String fileName = dirName + "/" + uniqueFileName;
            File uploadFile = convert(multipartFile);
            String uploadImageUrl = putS3(uploadFile, fileName);

            removeNewFile(uploadFile);

            return uploadImageUrl;

        } catch (IOException e) {
            return null;
        }
    }

    public void deleteFile(String fileName) {
        String fileNames = fileName.substring(fileName.indexOf("com/") + 4);

        amazonS3.deleteObject(bucket, fileNames);
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName) throws IOException {
        deleteFile(oldFileName);

        return upload(newFile, dirName);
    }


    private File convert(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }


}
