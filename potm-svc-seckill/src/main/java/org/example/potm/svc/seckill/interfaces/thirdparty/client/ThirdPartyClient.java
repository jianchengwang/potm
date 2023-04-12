package org.example.potm.svc.seckill.interfaces.thirdparty.client;

import jakarta.validation.Valid;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.seckill.interfaces.thirdparty.dto.WxPayInfoDTO;
import org.example.potm.svc.seckill.interfaces.thirdparty.dto.WxPayNotifyDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author jianchengwang
 * @date 2023/4/7
 */
public interface ThirdPartyClient {

    @PostExchange("")
    Response<WxPayNotifyDTO> wxpay(@Valid @RequestBody WxPayInfoDTO wxPayInfoDTO);

}
