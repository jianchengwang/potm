package org.example.potm.svc.seckill.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Data
@NoArgsConstructor
public class SkPayDomain {
    private PayNotifyDTO payNotify;
    private AtomicInteger tryTimes = new AtomicInteger(0);

    public SkPayDomain(PayNotifyDTO payNotifyDTO) {
        this.payNotify = payNotifyDTO;
    }

    public PayNotifyDTO getPayNotify() {
        return payNotify;
    }

    public void verifySign() {
        if(!"sign".equals(payNotify.getSign())) {
            throw new ClientException("签名错误", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }

    public int incrTryTimes() {
        return tryTimes.decrementAndGet();
    }
}
