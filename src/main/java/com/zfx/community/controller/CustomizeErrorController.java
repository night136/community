package com.zfx.community.controller;

import com.zfx.community.exception.CustomizeErrorCode;
import com.zfx.community.exception.CustomizeException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义错误页面控制器
 */
@Controller
public class CustomizeErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = null;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
        }

        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception instanceof CustomizeException) {
            CustomizeException customizeException = (CustomizeException) exception;
            model.addAttribute("errorCode", customizeException.getCode());
            model.addAttribute("errorMessage", customizeException.getMessage());
        } else if (statusCode != null) {
            // 通用 HTTP 错误
            if (statusCode == 404) {
                model.addAttribute("errorCode", 404);
                model.addAttribute("errorMessage", "页面未找到");
            } else if (statusCode == 500) {
                model.addAttribute("errorCode", CustomizeErrorCode.SYS_ERROR.getCode());
                model.addAttribute("errorMessage", CustomizeErrorCode.SYS_ERROR.getMessage());
            } else {
                model.addAttribute("errorCode", statusCode);
                model.addAttribute("errorMessage", "请求错误");
            }
        }

        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
