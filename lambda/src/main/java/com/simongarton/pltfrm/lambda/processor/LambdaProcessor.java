package com.simongarton.pltfrm.lambda.processor;


import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(LambdaProcessor.class);

    private final PltfrmCloudwatchService cloudwatchService;
    private final PltfrmDynamoDBService dynamoDBService;
    private final PltfrmS3Service s3Service;
    private final PltfrmSNSService snsService;
    private final PltfrmSSMService ssmService;

    public LambdaProcessor(
    ) {
        this.cloudwatchService = PltfrmCommonFactory.getPltfrmCloudwatchService();
        this.dynamoDBService = PltfrmCommonFactory.getPltfrmDynamoDBService();
        this.s3Service = PltfrmCommonFactory.getPltfrmS3Service();
        this.snsService = PltfrmCommonFactory.getPltfrmSNSService();
        this.ssmService = PltfrmCommonFactory.getPltfrmSSMService();
    }

    public String process() {
        return "Hello world ! I'm the LambdaProcessor.";
    }
}
