/*
 * (c)BOC
 */
package net.pis.service;

import net.pis.common.MessageType;
import net.pis.common.TaxInvoiceSignal;
import net.pis.exception.AdaptingException;
import net.pis.exception.SBMSException;
import net.pis.message.MessageMetaInfo;
import net.pis.service.signal.SignalHandlerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class TaxInvoiceAdaptingService implements AdaptingService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String domain = "Adapter";

    @Autowired
    private MessageService messageService;

    @Autowired
    private SignalHandlerMapper signalHandlerMapper;

    /**
     * 인바운드 데이타 처리
     *
     * @param messageMetaInfo
     */
    private void adaptInbound(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("TaxInvoiceAdaptingService adaptInbound =============================================== ");
        // writeActivityLog(messageMetaInfo.getMessageTagId(), MessageStatus.Progress);
        try {
            TaxInvoiceSignal signal = (TaxInvoiceSignal) messageService.getSignal(messageMetaInfo);
            MessageType messageType = signal.toMessageType();

            DefaultAsyncService asyncService
                = signalHandlerMapper.getHandler(signal, messageMetaInfo.getErpSystem());

            if (messageMetaInfo.isAck()) {
                if (!MessageType.RESULT.equals(messageType)) {

                    asyncService.recieveAckAboutRequest(messageMetaInfo);
                } else if (MessageType.RESULT.equals(messageType)) {

                    asyncService.recieveAckAboutResult(messageMetaInfo);
                }
            } else if (!MessageType.RESULT.equals(messageType)) {

                try {

                    asyncService.receiveRequest(messageMetaInfo);
                } catch (SBMSException se) {

                    messageMetaInfo.setError(true);
                    logger.error(se.getMessage());

                    throw se;

                } finally {

                    asyncService.sendResult(messageMetaInfo);
                }

            } else if (MessageType.RESULT.equals(messageType)) {

                asyncService.receiveResult(messageMetaInfo);
            }

            // writeActivityLog(messageMetaInfo.getMessageTagId(), MessageStatus.Complete);

        } catch (SBMSException ae) {
            logger.error(ae.getMessage());

            // errorLogService.createMessageError(messageMetaInfo.getMessageTagId(), ae);

            throw new AdaptingException(ae);

        }

    }

    private void adaptOutbound(MessageMetaInfo messageMetaInfo) throws AdaptingException {

        logger.info("===== 3.TaxInvoiceAdaptingService.adaptOutBound() =====");
        logger.info("= Description : 처리 Handler 서비스 접근을 위해 진입 =");
        logger.info("=======================================================");
        logger.info("===== TaxInvoiceAdaptingService adaptOutbound =============================================== ");

        TaxInvoiceSignal signal = (TaxInvoiceSignal) messageService.getSignal(messageMetaInfo);

        try {

            logger.info("==== signalHandlerMapper.getHandler : " + signalHandlerMapper.getHandler(signal, messageMetaInfo.getErpSystem()));

            signalHandlerMapper.getHandler(signal, messageMetaInfo.getErpSystem())
                .sendRequest(messageMetaInfo);

            logger.info("============== adaptOutbound ====================");
        } catch (SBMSException se) {
            logger.error("{}", se);

            if ("SAP".equals(messageMetaInfo.getErpSystem())) {
                AdaptingException ae = new AdaptingException(se);

                throw ae;
            }

        }
    }

    @Override
    public void adapt(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.debug("START ADAPTING");

        if (null != messageMetaInfo.getDirection()) {

            switch (messageMetaInfo.getDirection()) {
                case Outbound: {
                    this.adaptOutbound(messageMetaInfo);
                    break;
                }
                case Inbound: {

/*                    final RoutingInfoDTO routingInfoDTO = routingNavigator
                        .getRoutingPathInfo(messageMetaInfo);

                    if ("SAP".equals(routingInfoDTO.getAdapter())) {

                        messageMetaInfo.setErpSystem("SAP");
                        messageMetaInfo.setClientCode(routingInfoDTO.getClient());
                        messageMetaInfo.setCompanyCode(routingInfoDTO.getCompany());
                        messageMetaInfo.setTargetKey(routingInfoDTO.getTargetKey());

                    } else if ("NONSAP".equals(routingInfoDTO.getAdapter())) {

                        messageMetaInfo.setErpSystem("NONSAP");
                        messageMetaInfo.setTargetKey(routingInfoDTO.getTargetKey());

                        DynamicDSContextHolder.setDynamicDSType(routingInfoDTO.getTargetKey());

                    } else if ("SBMS".equals(routingInfoDTO.getAdapter())) {

                        messageMetaInfo.setErpSystem("SBMS");
                    }*/

                    messageMetaInfo.setErpSystem("NONSAP");
                    // messageMetaInfo.setTargetKey(routingInfoDTO.getTargetKey());
                    messageMetaInfo.setTargetKey("A0000000000");

                    this.adaptInbound(messageMetaInfo);
                    DynamicDSContextHolder.clearDynamicDSType();

                    break;
                }
            }
        }
        logger.debug("END ADAPTING");

    }

}
