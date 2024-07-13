package com.simongarton.platform.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.factory.AWSFactory;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PltfrmS3Service {

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmS3Service.class);

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter dateTimeFormatter;

    public PltfrmS3Service() {
        this.s3Client = AWSFactory.getS3Client();
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
        this.dateTimeFormatter = PltfrmCommonFactory.getDateFormatter();
    }

    public Object load(
            final String key,
            final String bucketName,
            final Class clazz) throws IOException {
        final GetObjectRequest getObjectRequest = new GetObjectRequest(
                bucketName,
                key);
        final S3Object s3Object = this.s3Client.getObject(getObjectRequest);
        LOG.info("read " + key + " from " + bucketName);
        final S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            final Object object = this.objectMapper.readValue(inputStream, clazz);
            LOG.info("loaded " + object.toString());
            return object;
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    public List<String> loadLines(final String key, final String bucketName) throws IOException {
        final GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        final S3Object s3Object = this.s3Client.getObject(getObjectRequest);
        LOG.info("read " + key + " from " + bucketName);

        final S3ObjectInputStream inputStream = s3Object.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    public void save(final Object object,
                     final String key,
                     final String bucketName) throws IOException {
        final String tempKey = UUID.randomUUID().toString();
        final File file = Path.of("/tmp", tempKey).toFile();
        this.objectMapper.writeValue(file, object);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                key,
                file);
        this.s3Client.putObject(putObjectRequest);
        LOG.info("uploaded " + key + " to " + bucketName);
        file.delete();
    }

    public void moveObjectToFolder(final String key, final String bucketName, final String newPrefix) {
        final String[] parts = key.split("/");
        final String fileName = parts[parts.length - 1];
        final String newKey = newPrefix + "/" + fileName;
        final CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, key, bucketName, newKey);
        this.s3Client.copyObject(copyObjectRequest);
        LOG.info("copied " + key + " to " + newKey + " in " + bucketName);

        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        LOG.info("deleted " + key + " from " + bucketName);
        this.s3Client.deleteObject(deleteObjectRequest);
    }

    public void moveObjectToBucket(final String key, final String oldBucketName, final String newBucketName) {
        final CopyObjectRequest copyObjectRequest = new CopyObjectRequest(oldBucketName, key, newBucketName, key);
        this.s3Client.copyObject(copyObjectRequest);
        LOG.info("copied " + key + " to " + key + " in " + newBucketName);

        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(oldBucketName, key);
        LOG.info("deleted " + key + " from " + oldBucketName);
        this.s3Client.deleteObject(deleteObjectRequest);
    }

    public void deleteObject(final S3ObjectSummary s3ObjectSummary) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3ObjectSummary.getBucketName(),
                s3ObjectSummary.getKey());
        this.s3Client.deleteObject(deleteObjectRequest);
        LOG.info("deleted " + s3ObjectSummary.getKey() + " from " + s3ObjectSummary.getBucketName());
    }

    public List<String> listKeysInBucket(final String bucketName) {
        final List<String> keys = new ArrayList<>();

        // TODO I need to check for continuation tokens if I get too many
        final ListObjectsV2Result result = this.s3Client.listObjectsV2(bucketName);
        for (final S3ObjectSummary summary : result.getObjectSummaries()) {
            keys.add(summary.getKey());
        }

        return keys;
    }

    public boolean objectExists(final String bucketName, final String key) {
        return this.s3Client.doesObjectExist(bucketName, key);
    }
}
