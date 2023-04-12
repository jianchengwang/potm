package org.example.potm.svc.seckill.interfaces.client.api;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.cdc.NotLog;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.seckill.application.SkPayApplication;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.example.potm.svc.seckill.interfaces.thirdparty.dto.WxPayNotifyDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@RestController
@RequestMapping("/api/client/wxpay/notify")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "客户端-微信支付回调")
public class WxPayNotifyController {

    private final SkPayApplication skPayApplication;

    @PostMapping("")
    @SaIgnore
    @NotLog
    public Response<String> notify(@Valid @RequestBody WxPayNotifyDTO wxPayNotifyDTO) {
        log.info("receive pay backend notify, wxPayNotifyDTO: {}", wxPayNotifyDTO);
        PayNotifyDTO payNotify = new PayNotifyDTO();
        BeanUtil.copyProperties(wxPayNotifyDTO, payNotify, false);
        payNotify.setPayMethod(PayMethodEnum.WECHAT);
        if(skPayApplication.payNotify(payNotify)) {
            log.info("pay success: {}", payNotify);
            return Response.ok("SUCCESS");
        }
        log.info("pay fail: {}", payNotify);
        return Response.ok("FAIL");
    }
}
