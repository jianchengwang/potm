package org.example.potm.svc.seckill.interfaces.thirdparty.api;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.lang.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.potm.framework.config.cdc.NotLog;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.seckill.interfaces.thirdparty.dto.WxPayInfoDTO;
import org.example.potm.svc.seckill.interfaces.thirdparty.dto.WxPayNotifyDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@RestController
@RequestMapping("/api/thirdparty/wxpay")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "第三方服务-微信支付中心")
public class WxPayController {

    private final RestTemplate restTemplate;
    private final ExecutorService notifyExecutor = Executors.newFixedThreadPool(5);

    @PostMapping("")
    @SaIgnore
    @NotLog
    public Response<Void> pay(@Valid @RequestBody WxPayInfoDTO wxPayInfoParam) {
        log.info("receive pay request, wxPayInfoParam: {}", wxPayInfoParam);
        LocalDateTime payTime = LocalDateTime.now();
        // 校验签名
        String sign = wxPayInfoParam.getSign();
        // 校验是否超时
        LocalDateTime expireTime = wxPayInfoParam.getExpireTime();
        if(expireTime!=null && expireTime.isBefore(payTime)) {
            return Response.fail(new ClientException("订单已超时", FrameworkErrorCode.PARAM_VALIDATE_ERROR));
        }
        WxPayNotifyDTO wxPayNotify = new WxPayNotifyDTO();
        wxPayNotify.setOutTradeNo(wxPayInfoParam.getOutTradeNo());
        wxPayNotify.setPayMoney(wxPayInfoParam.getPayMoney());
        wxPayNotify.setPayTransactionId(UUID.fastUUID().toString(true));
        wxPayNotify.setPayTime(LocalDateTime.now());
        wxPayNotify.setSuccess(true);
        wxPayNotify.setSign("sign");
        // 通知业务方
        log.info("notify business, wxPayNotify: {}", wxPayNotify);
        notifyExecutor.execute(new NotifyBackendRunnable(wxPayInfoParam.getBackendNotifyUrl(), wxPayNotify));
        return Response.ok();
    }

    public class NotifyBackendRunnable implements Runnable {
        private final String backendNotifyUrl;
        private final WxPayNotifyDTO wxPayNotify;
        public NotifyBackendRunnable(String backendNotifyUrl, WxPayNotifyDTO wxPayNotify) {
            this.backendNotifyUrl = backendNotifyUrl;
            this.wxPayNotify = wxPayNotify;
        }
        @Override
        public void run() {
            int tryTimes = 0;
            try {
                // 重试三次后丢弃
                while (tryTimes < 3) {
                    Response<String> result = restTemplate.postForObject(backendNotifyUrl, wxPayNotify, Response.class);
                    log.info("business notify result: {}", result);
                    if(result!=null && result.getStatus() == 200 && StringUtils.equals(result.getData(), "SUCCESS")) {
                        break;
                    }
                    tryTimes++;
                    Thread.sleep(5000L * tryTimes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
