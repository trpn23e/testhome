/*
 * (c)BOC
 */
package net.pis.service.signal;

import net.pis.common.Adapter;
import net.pis.common.MessageStatus;
import net.pis.common.NonSapAdapter;
import net.pis.exception.AdaptingException;
import net.pis.exception.SBMSException;
import net.pis.message.MessageMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 * NonSAP 시그널 핸들러
 *
 * @author jh,Seo
 */
public abstract class TaxInvoiceNonSapSignalHandler extends TaxInvoiceSignalHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Adapter getAdpater() {
        return new NonSapAdapter();
    }
}
