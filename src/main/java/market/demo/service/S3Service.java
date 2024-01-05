package market.demo.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

//    private final AmazonS3 amazonS3;
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String fileName = generateFileName(file);
                s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null));
                String fileUrl = s3Client.getUrl(bucket, fileName).toString();
                uploadedUrls.add(fileUrl);
            } catch (AmazonClientException | IOException e) {
                log.error("File upload failed", e);
                throw e;
            }
        }
        return uploadedUrls;
    }
    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-" + file.getOriginalFilename().replace(" ", "_");
    }
}
