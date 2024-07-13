package com.simongarton.pltfrm.service;

import com.simongarton.pltfrm.factory.AWSFactory;
import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.Record;
import software.amazon.awssdk.services.timestreamwrite.model.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PltfrmTimestreamService {

    protected static final Logger LOG = LoggerFactory.getLogger(PltfrmTimestreamService.class);

    protected final TimestreamWriteClient timestreamWriteClient;
    protected final TimestreamQueryClient timestreamQueryClient;
    protected final DateTimeFormatter dateTimeFormatter;

    public PltfrmTimestreamService() {
        this.timestreamWriteClient = AWSFactory.getTimestreamWriteClient();
        this.timestreamQueryClient = AWSFactory.getTimestreamQueryClient();
        this.dateTimeFormatter = PltfrmCommonFactory.getOffsetDateTimeFormatter();
    }

    protected int writeRecordsInBatchesOf100(final Record commonAttributes,
                                             final List<Record> records,
                                             final String databaseName,
                                             final String tableName) {

        int recordsWritten = 0;

        final List<Record> batch = new ArrayList<>();
        for (
                final Record record : records) {
            batch.add(record);
            if (batch.size() == 100) {
                recordsWritten += this.writeBatch(commonAttributes, batch, databaseName, tableName);
                batch.clear();
            }
        }

        recordsWritten += this.writeBatch(commonAttributes, batch, databaseName, tableName);
        return recordsWritten;
    }

    private int writeBatch(final Record commonAttributes, final List<Record> records, final String databaseName, final String tableName) {

        final WriteRecordsRequest writeRecordsRequest = WriteRecordsRequest.builder()
                .databaseName(databaseName)
                .tableName(tableName)
                .commonAttributes(commonAttributes)
                .records(records).build();

        int recordsWritten = records.size();
        LOG.info("Writing " + recordsWritten + " to Timestream at " + OffsetDateTime.now() + " ...");
        try {
            final WriteRecordsResponse writeRecordsResponse = this.timestreamWriteClient.writeRecords(writeRecordsRequest);
            LOG.info(writeRecordsResponse.hashCode() + " : " + writeRecordsResponse.recordsIngested().total());
        } catch (final RejectedRecordsException e) {
            LOG.error(e.getClass().getSimpleName() + " : " + e.getMessage());
            for (final RejectedRecord rejectedRecord : e.rejectedRecords()) {
                final Record rejectedOriginalRecord = records.get(rejectedRecord.recordIndex());
                LOG.warn("Rejected index " + rejectedRecord.recordIndex() +
                        " at " + OffsetDateTime.now() +
                        " for " + rejectedOriginalRecord.time() +
                        " : " + rejectedRecord.reason());
            }
            recordsWritten = recordsWritten - e.rejectedRecords().size();
        } catch (final Exception e) {
            // this is swallowing the error : it should be thrown back to the caller
            LOG.error(e.getClass().getSimpleName() + " : " + e.getMessage(), e);
        }

        return recordsWritten;
    }

    protected long getCorrectUTCSecond(final OffsetDateTime timestamp) {
        final ZonedDateTime zonedDateTime = timestamp.atZoneSameInstant(ZoneOffset.UTC);
        return zonedDateTime.toEpochSecond();
    }
}
