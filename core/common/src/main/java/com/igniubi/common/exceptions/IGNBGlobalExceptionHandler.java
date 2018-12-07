package com.igniubi.common.exceptions;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.igniubi.model.enums.common.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 服务的全局异常处理
 * 1. 返回200，并且用json作为消息体，并且设置消息头（方便resttemplate处理）
 * 2. 返回500，设置消息头
 */
@Component
public class IGNBGlobalExceptionHandler implements HandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());



    //如果服务异常，需要把异常信息加入到头中
    public static String HEADER_ERROR_CODE = "x-service-error-code";
    public static String HEADER_ERROR_MESSAGE = "x-service-error-message";



    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception exception) {


        //publish event

        //如果不是服务异常，直接返回500，并且打印异常
        if (!(exception instanceof IGNBException)) {
            logger.error("unknown exception happened at:{}", httpServletRequest.getRequestURI());
            logger.error("unknown exception is ", exception);
            httpServletResponse.setStatus(500);
            httpServletResponse.addHeader(HEADER_ERROR_MESSAGE, exception.getMessage());
            return new ModelAndView();
        }


        //处理服务异常。 需要添加异常信息到头中，并且返回json
        IGNBException se =  (IGNBException) exception;
        int code = se.getCode();
        String message = se.getMessage();

        //如果是服务不可用，直接返回500，并且打印异常
        if ( code == ResultEnum.SERVICE_NOT_AVAILABLE.getCode()) {
            logger.error("service not available exception happened at:{}", httpServletRequest.getRequestURI());
            httpServletResponse.setStatus(500);
            httpServletResponse.addHeader(HEADER_ERROR_MESSAGE, exception.getMessage());
            return new ModelAndView();
        }

        //add header
        httpServletResponse.addHeader(HEADER_ERROR_CODE, String.valueOf(code));
        httpServletResponse.addHeader(HEADER_ERROR_MESSAGE, message);

        ModelAndView mv = getErrorJsonView(code, message);
        return mv;
    }



    /**
     * 使用FastJson提供的FastJsonJsonView视图返回，不需要捕获异常
     */
    public static ModelAndView getErrorJsonView(int code, String message) {
        ModelAndView modelAndView = new ModelAndView();
        FastJsonJsonView jsonView = new FastJsonJsonView();
        Map<String, Object> errorInfoMap = new HashMap<>();
        errorInfoMap.put("code", code);
        errorInfoMap.put("message", message);
        jsonView.setAttributesMap(errorInfoMap);
        modelAndView.setView(jsonView);
        return modelAndView;
    }



}
