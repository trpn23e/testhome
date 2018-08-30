/**
 * (c)BOC
 */
package net.pis.service;

import net.pis.common.MessageStatus;
import net.pis.common.Signal;
import net.pis.common.TaxInvoiceSignal;
import net.pis.dao.MessageTagMapper;
import net.pis.dto.MessageTagDTO;
import net.pis.message.MessageMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * Created by achiz on 14. 6. 11.
 */
// FIX ME : 클래스 이름 바꿉시다... 메세지 서비스와 어울리지 않음 (SignalService?)

@Service
public class MessageServiceImpl implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private MessageTagMapper messageTagMapper;

    @Override
    public Signal getSignal(MessageMetaInfo messageMetaInfo) {
        //logger.trace("메타에서 시그널 뽑기!");
        Signal signal = null;

        if ("NONSAP".equals(messageMetaInfo.getErpSystem())) {
            MessageTagDTO param = new MessageTagDTO();
            param.setMessageTagId(messageMetaInfo.getMessageTagId());
            // 가져왔다 치자
            // List<MessageTagDTO> messageTagList = messageTagMapper.read(param);
            List<MessageTagDTO> messageTagList = new ArrayList<MessageTagDTO>();
            for(int i=0; i<=1; i++) {
                MessageTagDTO messageTagDTO = new MessageTagDTO();
                messageTagDTO.setSignal("ARISSUE");
                messageTagList.add(messageTagDTO);
            }
            logger.info("===== 4.MessageServiceImpl.getSignal() ================");
            logger.info("= Description : Signal을 셋팅해준다. 이 예제에서는 =");
            logger.info("= erpSystem : NONSAP, signal : ARISSUE 로 고정함");
            logger.info("=======================================================");
            logger.info("==== MessageServiceImpl getSignal =====");

            signal = TaxInvoiceSignal.valueOf(messageTagList.get(0).getSignal());

            logger.info("signal : " + signal);

        }

        return signal;
    }

    @Override
    public Object getInterfaceDTO(MessageMetaInfo messageMetaInfo) {
        return null;
    }

    @Override
    public void updateMessageTag(String messageTagId, MessageStatus messageStatus) {

    }
}
