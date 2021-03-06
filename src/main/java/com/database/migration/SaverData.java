package com.database.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static com.sysaid.Manager.issueIdToServReq;


/**
 * Created by Shirel Azulay on 23/08/2016.
 */


public class SaverData {
    private static Logger LOGGER = LoggerFactory.getLogger(SaverData.class);

    /**
     *
     * @param dsTarget
     * @param sqlInsertToServiceReqTable
     * @param records
     */
    public static void updateServiceReqTable(DataSource dsTarget, String sqlInsertToServiceReqTable, List<ServiceRequest> records, boolean isHistory) {
        try (Connection dbConn = DataSourceUtils.getConnection(dsTarget);
             PreparedStatement ps = dbConn.prepareStatement(sqlInsertToServiceReqTable)) {

             records.forEach(serviceReq -> {
                 try {
                     serviceReq.save(ps, isHistory);
                 } catch (SQLException e) {
                     System.out.println(ps);
                     throw new RuntimeException(e);
                 }
             });
        } catch (Exception e) {
            LOGGER.error("error while saving table",e);
            System.err.println("error while saving table: " + e);
        }

    }

    /**
     *
     * @param dsTarget
     * @param sqlInsertToServiceReqHistoryTable
     * @param queryIdOfServiceRec
     * @param records
     */
    public static void updateServiceReqHistoryTable(DataSource dsTarget, String sqlInsertToServiceReqHistoryTable, String queryIdOfServiceRec, List<ServiceRequest> records) {
        try (
             Connection dbConn = DataSourceUtils.getConnection(dsTarget);
             PreparedStatement ps = dbConn.prepareStatement(queryIdOfServiceRec)) {

                try {//TODO:NULLLLLLLL LINE 60
                    ResultSet resultSet = records.iterator().next().getAllRecordThatWasInsertedToServiceReq(ps);
                    while (resultSet.next()) {

                      int idOfServiceReqToSaveInSerReqHistory =  Integer.parseInt(resultSet.getString(1));
                      int issueIdAsKey =  Integer.parseInt(resultSet.getString(2));
                      ServiceRequest currentServiceRequest = issueIdToServReq.get(issueIdAsKey);
                      currentServiceRequest.setId(idOfServiceReqToSaveInSerReqHistory);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        } catch (Exception e) {
            LOGGER.error("error while saving table",e);
            System.err.println("error while saving table: " + e);
        }

        updateServiceReqTable(dsTarget, sqlInsertToServiceReqHistoryTable, records, true);

    }


}
