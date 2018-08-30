/*
 * (c)BOC
 */
package net.pis.service.signal;

import net.pis.common.*;
import net.pis.dao.MessageTagService;
import net.pis.dto.MessageTagDTO;
import net.pis.exception.AdaptingException;
import net.pis.message.MessageMetaInfo;
import net.pis.service.DefaultAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
/**
 * 전자 세금계산서 비즈니스 시그널 핸들러
 *
 * @author jh,Seo
 */
public abstract class TaxInvoiceSignalHandler implements DefaultAsyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final String domain = "Adapter";

    protected final String SERVICE_ID = "DTI";

    protected final String SYSTEM_NAME = "SBMS";

    protected final String SUCCESS_CODE = "30000";

    /**
     * 매입
     */
    protected final String BUYER = "AP";
    /**
     * 매출
     */
    protected final String SUPPLIER = "AR";

    /**
     * 정방향
     */
    protected final String FORWARD = "2";
    /**
     * 역방향
     */
    protected final String REVERSE = "1";

    @Autowired
    protected MessageTagService messageTagService;

    @Autowired
    protected JmsSender jmsSender;

    public abstract List<TaxInvoiceSignal> getProviders();

    public abstract Adapter getAdpater();

    @Override
    public void recieveAckAboutRequest(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("=== TaxInvoiceSignalHandler.recieveAckAboutRequest() ==================== ");
    }

    @Override
    public void receiveResult(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("=== TaxInvoiceSignalHandler.receiveResult() ==================== ");
    }

    @Override
    public void receiveRequest(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("=== TaxInvoiceSignalHandler.receiveRequest() ==================== ");
    }

    @Override
    public void recieveAckAboutResult(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("=== TaxInvoiceSignalHandler.recieveAckAboutResult() ==================== ");
    }


    public void sendResult(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("=== TaxInvoiceSignalHandler.sendResult() ==================== ");
    }
}