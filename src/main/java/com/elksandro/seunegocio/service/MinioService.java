package com.elksandro.seunegocio.service;

import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;

@Service
public class MinioService {

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-url}")
    private String publicUrl;

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!isExist) {
                logger.info("Bucket '{}' não existe. Criando...", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("Bucket '{}' criado com sucesso.", bucketName);
            } else {
                logger.info("Bucket '{}' já existe.", bucketName);
            }

            String policyJson = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"AWS": ["*"]},
                        "Action": ["s3:GetObject"],
                        "Resource": ["arn:aws:s3:::%s/*"]
                    }
                ]
            }
            """.formatted(bucketName);

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policyJson).build());
            logger.info("Política de leitura pública definida para o bucket '{}'.", bucketName);
        } catch (Exception e) {
            logger.error("Erro durante a inicialização do MinIO: {}", e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String uniqueName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(uniqueName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()   
            );

            logger.info("Arquivo '{}' enviado com sucesso.", uniqueName);
            return uniqueName;
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao fazer upload da imagem", e); 
        }
    }

    public void deleteObject(String objectName) {
        if (objectName == null || objectName.isBlank()) return;

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            logger.info("Arquivo removido com sucesso: {}", objectName);
        } catch (ErrorResponseException e) {
            if (e.errorResponse() != null && "NoSuchKey".equals(e.errorResponse().code())) {
                logger.warn("Arquivo '{}' já não existe no MinIO", objectName);
                return;
            }
            logger.error("Erro ao remover o arquivo (resposta de erro MinIO): {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao remover o arquivo do MinIO", e);
        } catch (Exception e) {
            logger.error("Erro ao remover o arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao remover o arquivo do MinIO", e);
        }
    }

    public String getObjectUrl(String objectName) {
        if (objectName == null || objectName.isBlank()) return null;
        
        return publicUrl + "/" + bucketName + "/" + objectName;
    }
}