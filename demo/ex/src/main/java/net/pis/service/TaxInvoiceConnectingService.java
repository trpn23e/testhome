/*
 * (c)BOC
 */
package net.pis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.pis.common.JmsSender;
import net.pis.exception.ConnectingException;
import net.pis.message.MessageMetaInfo;
import net.pis.wsserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.ws.rs.WebApplicationException;
import javax.xml.ws.WebServiceException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 컨넥팅 서비스
 * <p>
 * 스마트빌과 중계서버간 송,수신 데이타를 처리한다.
 * </p>
 *
 * @author jh, Seo
 */
@Service
//@Scope(scopeName = "prototype")
@ManagedResource
public class TaxInvoiceConnectingService implements ConnectingService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // @Value("${sbms.oapi.endpoint}")
    private String oapiEndpoint;

    @Autowired
    // @Resource(name = "neobillClient")
    private IRequest neobillClient;

    /**
    @Autowired
    @Resource(name = "retryMap")
    private Map<String, Integer> retryMap;

    @ManagedAttribute(description = "재시도 맵에 담겨있는 친구들")
    public int getRetryCount() {
        return retryMap.size();
    }
     **/

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private MessageService messageService;

    private final String[] retryExceptionMessage = new String[]{
            "Marshalling Error",
            "Failed to create service"
    };


    @Override
    public Object connectToMessageServer(Object object) throws ConnectingException {
        ItgBillACKResponse ack = new ItgBillACKResponse();

        if (object instanceof ItgBillRequest) {
            this.connectToMessageServerForRequest(object, ack);
        } else if (object instanceof ItgBillResponse) {
            // this.connectToMessageServerForResult(object, ack);
        }
        return ack;
    }

    private ItgBillACKResponse connectToMessageServerForRequest(Object object, ItgBillACKResponse ack) {
        ItgBillRequest request = (ItgBillRequest) object;
        for (String mainDoc : request.getMSGDOC().getString()) {

        }
        for (String metaDoc : request.getREQDOC().getString()) {

        }
        for (AttachFile attachFile : request.getFILES().getAttachFile()) {

        }
        return ack;
    }

    public void connectToSmartBill(MessageMetaInfo messageMetaInfo) throws ConnectingException {

        switch (messageService.getSignal(messageMetaInfo).toMessageType()) {
            case RESULT:
                logger.info(">> 스마트빌에 결과 메세시 송신");
                // this.connectToSmartBillForResult(messageMetaInfo);
                break;

            case OpenAPI: {
                logger.info(">> 스마트빌에 oAPI 메세시 송신");
                // this.connectToSmartBillByOpenAPI(messageMetaInfo);
                break;
            }
            case InnerService: {
                logger.info(">> 내부 루프백 서비스");
                // this.connectToSelf(messageMetaInfo);
                break;
            }
            case REQUEST:
            case UTIL:
            case STATUS: {
                logger.info(">> 스마트빌에 요청 메세시 송신");
                // this.connectToSmartBillForRequest(messageMetaInfo);
                break;
            }

        }

    }

}
