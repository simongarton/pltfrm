package com.simongarton.pltfrm.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SQSMessageBody {

    @JsonProperty("Type")
    private String type;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("TopicArn")
    private String topicArn;
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("SignatureVersion")
    private String signatureVersion;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("SigningCertURL")
    private String signingCertURL;
    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;
}
