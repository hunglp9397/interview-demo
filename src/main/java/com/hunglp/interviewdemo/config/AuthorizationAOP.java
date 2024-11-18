package com.hunglp.interviewdemo.config;

import com.hunglp.interviewdemo.annotations.AuthorizedFor;
import com.hunglp.interviewdemo.annotations.CustomPermission;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Permission;

@Aspect
@Component
public class AuthorizationAOP {

    @Autowired
    private HttpServletRequest request;

    @Before("@annotation(authorizedFor)")
    private void checkPermissionBeforeExecuteMethod(AuthorizedFor authorizedFor) {
        String permissionHeader = request.getHeader("permission");
        if (permissionHeader == null) {
            throw new RuntimeException("Permission required!");
        }

        if(!permissionHeader.equals(CustomPermission.WRITE) || !permissionHeader.equals(CustomPermission.READ)){
            throw new RuntimeException("Permission denied!");
        }
    }
}
