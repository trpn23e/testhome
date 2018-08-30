/*
 * (c)BOC
 */
package net.pis.service.signal;

import net.pis.common.Direction;
import net.pis.common.MessageStatus;
import net.pis.common.TaxInvoiceSignal;
import net.pis.dto.MessageTagDTO;
import net.pis.dto.MetaDocumentDataDTO;
import net.pis.dto.message.TaxInvoiceMessageData;
import net.pis.exception.AdaptingException;
import net.pis.exception.SBMSException;
import net.pis.message.MessageMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.*;
/**
 * 정발행 핸들러
 *
 * @author jh, Seo
 */
@Service
public class TaxInvoicePublicationHandler extends TaxInvoiceNonSapSignalHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // @Autowired
    // private TaxInvoiceMessageDataFactory messageDataFactory;


    // receiveRequest() 에서 사용하는데.. 뭔가 자동승인 task인듯 일단은 안쓰니까 막자
    // @Autowired
    // @Resource(name = "autoApproveThreadPoolTaskExecutor")
    // private TaskExecutor autoApproveThreadPoolTaskExecutor;

    @Override
    public List<TaxInvoiceSignal> getProviders() {

        List<TaxInvoiceSignal> providers = new ArrayList<>();
        providers.add(TaxInvoiceSignal.ARISSUE);
        providers.add(TaxInvoiceSignal.ARISSUE_FINISH);

        return providers;
    }

    @Override
    public void sendRequest(MessageMetaInfo messageMetaInfo) throws AdaptingException {
        logger.info("=== 6.TaxInvoicePublicationHandler.sendRequest() 진입 ====");
        logger.info("=== extend,implement 하는 클래스, 인터페이스 들이 연관관계가 있어");
        logger.info("=== TaxInvoiceAdaptingService에서 이 메소드가 호출되게 된다. ");
        logger.info("==========================================================");
        logger.info("발행 시작!");

        MessageTagDTO messageTagDTO = null;

        try {
            logger.info("원본 생성을 위한 사전 정보 취득");
            // 조낸 복잡함... 가져왔다 치자
            // TaxInvoiceMessageData messageData
            // = messageDataFactory.createTaxInvoiceMessageData(messageMetaInfo);
            logger.info("원본 생성을 위한 사전 정보 취득 완료");

            messageTagDTO = new MessageTagDTO();
            messageTagDTO.setMessageTagId(messageMetaInfo.getMessageTagId());
            messageTagDTO.setMessageId(messageMetaInfo.getMessageId());
            messageTagDTO.setServiceId(SERVICE_ID);
            messageTagDTO.setDirection(Direction.Outbound.getCode());
            messageTagDTO.setFromSystem(messageMetaInfo.getErpSystem());
            messageTagDTO.setMessageStatus(MessageStatus.Recognition.getCode());
            messageTagDTO.setTargetSystemId(messageMetaInfo.getTargetSystemId());
            messageTagDTO.setAuthTicket(messageMetaInfo.getTicket());

            // messageTagDTO.setConversationId(messageData.getMessageHeader().getConversationId());
            // messageTagDTO.setSendRegNo(messageData.getMessageHeader().getSndComregno());
            // messageTagDTO.setReceiveRegNo(messageData.getMessageHeader().getRcvComregno());
            // messageTagDTO.setSignal(messageData.getMessageHeader().getSignal());

            messageTagDTO.setSenderSystemId(messageMetaInfo.getTargetKey());

            //완료됐다 치자..
            // messageTagService.create(messageTagDTO);
            logger.info("MESSAGE TAG 기록 완료");

            //activityLogDTO = writeActivityLog(messageMetaInfo.getMessageTagId());
            // 전자서명 하고, 인증거치고.. 이걸 전달받은 xml읽어서 뭘 하는것 같은데..
            // 너무 복잡함 패스!
            // String signedXML = this.sendPublishRequest(messageMetaInfo, messageData);
            String signedXML = this.sendPublishRequest(messageMetaInfo, null);

            logger.info("인터페이스 상태 갱신 : {}", MessageStatus.Progress);
            logger.info("STATUS 상태 갱신 : {}", MessageStatus.Progress);

            // xml? 뭔가 만들것 같은데 messageData 만드는 과정이 너무 길다.. Pass!
            /*
            String mainDocument = messageData.getMessageMainDocument().get(0).getDocumentData();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(mainDocument.getBytes()));
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//TaxInvoiceDocument/IssueID");
            String issueId = expr.evaluate(doc);
            */

            // mainDTO.setApproveId(issueId);
            // mainDTO.setDtiMsg(signedXML);

            // dTIMainService.update(mainDTO);

            logger.info("MAIN 승인번호 갱신");

            /*activityLogDTO.setActivityStatus(MessageStatus.Complete.getCode());
            activityLogDTO.setUdate(Calendar.getInstance().getTime());
            activityLogService.update(activityLogDTO);
             */
            jmsSender.sendMessage(messageMetaInfo);

        } catch (SBMSException se) {
            logger.error("{}", se.getMessage());

            // handleException(se, messageTagDTO, activityLogDTO, interfaceDTO,
            //    messageMetaInfo.getMessageTagId());

            throw new AdaptingException(se);
        } catch (JMSException je) {
            logger.error("{}", je);

            // FIX ME : JMS 오류코드 만들것
            AdaptingException.AdaptingError ae = AdaptingException.AdaptingError.DEFAULT_ADPATING_EXCEPTION;

            AdaptingException ex = new AdaptingException(ae.getErrorCode(),
                je.getMessage() == null ? ae.getErrorMessage() : je.getMessage());

            // handleException(ex, messageTagDTO, activityLogDTO, interfaceDTO,
            //    messageMetaInfo.getMessageTagId());

            throw ex;
        } catch (Exception e) {
            logger.error("{}", e.getMessage());

            AdaptingException.AdaptingError ae = AdaptingException.AdaptingError.DEFAULT_ADPATING_EXCEPTION;

            AdaptingException ex = new AdaptingException(ae.getErrorCode(),
                e.getMessage() == null ? ae.getErrorMessage() : e.getMessage());

            // handleException(ex, messageTagDTO, activityLogDTO, interfaceDTO,
            //    messageMetaInfo.getMessageTagId());

            throw ex;

        }
    }

    // 전자서명하고 인증 끝난거 뭐를 xml에서 뽑아서 db처리등 하는것 같은데 너무 복잡하다.
    // 했다고 치자
    private String sendPublishRequest(MessageMetaInfo messageMetaInfo,
                                      TaxInvoiceMessageData messageData) throws SBMSException {

        String signedXML = "";
        String RValue = "인증OK";


        MetaDocumentDataDTO metaDocumentDataDTO = new MetaDocumentDataDTO();

        metaDocumentDataDTO.setDocumentDataId(UUID.randomUUID().toString());
        metaDocumentDataDTO.setMessageTagId(messageMetaInfo.getMessageTagId());
        // metaDocumentDataDTO.setSeq(metaSeq);
        metaDocumentDataDTO.setSeq(1);
        metaDocumentDataDTO.setDocumentType("DOC_TYPE_EXAMPLE_01");
        metaDocumentDataDTO.setDocumentData(
                new StringBuilder().append("<RVALUE>").append(RValue)
                        .append("</RVALUE>").toString()
        );
        logger.info("서명과 관련된 META 데이타 생성 완료");

        logger.info("첨부파일 생성 완료");

        return signedXML;
    }
}
