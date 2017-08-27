package org.gonzalad.idp.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    static final String REQUEST_ATTR_ERROR_EXCEPTION = "javax.servlet.error.exception";
    static final String REQUEST_ATTR_ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    private static final Logger LOG = Logger.getLogger(ErrorController.class.getName());

    @GetMapping(value = "error")
    @ExceptionHandler(Exception.class)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        Exception ex = getException(httpRequest);
        LOG.log(Level.SEVERE, "An error occured in Fediz IDP ", ex);
        System.out.println("HERRRRE");
        System.out.println("An error occured in Fediz IDP ");
        ex.printStackTrace();
        ModelAndView errorPage = new ModelAndView("genericerror");
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Your browser sent a request that the server could not understand.";
                break;
            }
            case 401: {
                errorMsg = "There was an authentication failure.";
                break;
            }
            case 403: {
                errorMsg = "It seems that you are not authorized to access the page or directory requested.";
                break;
            }
            case 404: {
                errorMsg = "The page that you are trying to access was moved or does not exist anymore.";
                break;
            }
            case 500: {
                errorMsg = "Due to an internal server error or misconfiguration, your request could not be completed.";
                break;
            }
            default: {
                errorMsg = "Due to an internal server error or misconfiguration, your request could not be completed ("
                        + httpErrorCode + ")";
                break;
            }
        }
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest.getAttribute(REQUEST_ATTR_ERROR_STATUS_CODE);
    }

    private Exception getException(HttpServletRequest httpRequest) {
        return (Exception) httpRequest.getAttribute(REQUEST_ATTR_ERROR_EXCEPTION);
    }
}