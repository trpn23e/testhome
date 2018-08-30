/*
 * (c)BOC
 */
package net.pis.service;

import net.pis.common.Direction;
import net.pis.common.Listener;
import net.pis.common.SqlSessionHandler;
import net.pis.controller.AdaptingController;
import net.pis.dto.ContractDTO;
import net.pis.exception.SBMSException;
import net.pis.message.MessageMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
/**
 * 잡 클래스
 * <p>
 * NON-SAP 설정 파일( $SBMS_HOME/conf/adpater/dti.datasource*.xml )이 없으면 빈에 등록되지
 * 않는다.
 *
 * @author jh,Seo
 */
@Service
@ManagedResource
// @Conditional(PollingCondition.class)
public class PollingTaskService implements PollingService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SqlSessionHandler sqlSessionHandler;

    @Autowired
    private AdaptingController taxInvoiceAdaptingController;

    @Autowired
    private DemoService demoService;

    private Integer callCount = 0;

    @ManagedAttribute(description = "hello")
    public Integer getCallCount() {
        return callCount;
    }


    private List<MessageMetaInfo> grapTargets() {

        List<ContractDTO> targetList = demoService.read();

        List<MessageMetaInfo> messageMetaInfoList = new ArrayList<>();

        for (ContractDTO interfaceDTO : targetList) {

            String uuid = UUID.randomUUID().toString();
            MessageMetaInfo metaInfo = new MessageMetaInfo();
            metaInfo.setMessageTagId(uuid);
            metaInfo.setMessageId("MESSAGE_ID_EXAMPLE_01");
            metaInfo.setDirection(Direction.Outbound);
            metaInfo.setDestination(Listener.Router);
            metaInfo.setAck(false);
            metaInfo.setError(false);
            metaInfo.setTargetSystemId("SYSTEM_ID_EXAMPLE_01");

            metaInfo.setTicket("TICKET_EXAMPLE_01");

            metaInfo.setErpSystem("NONSAP"); // NONSAP으로 고정하자 signal 셋팅

            messageMetaInfoList.add(metaInfo);
        }

        return messageMetaInfoList;
    }

    @Override
    public List<?> execute() {
        return null;
    }

    @Override
    @ManagedAttribute
    @ManagedOperation(description = "헬로")
    public List<MessageMetaInfo> execute2() {
        callCount++;

        List<MessageMetaInfo> metaInfoList = new ArrayList<>();


        try {
            DynamicDSContextHolder.clearDynamicDSType();
            DynamicDSContextHolder.setDynamicDSType("SYSTEM_ID_EXAMPLE_01");
            metaInfoList.addAll(this.grapTargets());
        } catch (Exception e) {
            logger.error("{}", e.toString());
        }

        return metaInfoList;

    }

    /*
    @Override
    @ManagedAttribute
    @ManagedOperation(description = "헬로")
    public List<MessageMetaInfo> execute2() {
        callCount++;

        List<MessageMetaInfo> metaInfoList = new ArrayList<>();
        Iterator<String> it = sqlSessionHandler.iterator();

        while (it.hasNext()) {
            String systemId = it.next();

            try {
                DynamicDSContextHolder.clearDynamicDSType();
                DynamicDSContextHolder.setDynamicDSType(systemId);
                logger.debug("SYSTEM TARGET = {}", systemId);
                metaInfoList.addAll(this.grapTargets());
                //DynamicDSContextHolder.clearDynamicDSType();
            } catch (Exception e) {
                logger.error("{}", e.toString());
            }
        }

        return metaInfoList;

    }
    */

    @Override
    public void execute3() {

    }

    /**
     * Quartz 스케쥴러 시작 매서드
     */
    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void poll() {

        logger.info("=============================================");
        logger.info("= 1.polling 시작(PollingTaskService.poll()");
        logger.info("=============================================");

        List<MessageMetaInfo> metaInfoList = this.execute2();
        logger.info("1.PollingTaskService.poll() metaInfoList : " + metaInfoList.toString());

        for (MessageMetaInfo messageMetaInfo : metaInfoList) {

            try {
                taxInvoiceAdaptingController.handleOutbound(messageMetaInfo);
            } catch (SBMSException se) {
                logger.error(se.getMessage());
            }

        }
        logger.info("*****========================================*****");
        logger.info("= 1.polling 종료(PollingTaskService.poll()");
        logger.info("*****========================================*****");

    }

}
