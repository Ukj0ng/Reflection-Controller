package com.uk.reflection.filter;

import com.uk.reflection.anno.RequestMapping;
import com.uk.reflection.controller.UserController;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Dispatcher implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        System.out.println("디스패쳐 진입");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

//        System.out.println("컨텍스트패스: " + request.getContextPath());
//        System.out.println("식별자주소: " + request.getRequestURI());
//        System.out.println("컨텍스트패스: " + request.getRequestURL());

        // /user 파싱하기
        String endPoint = request.getRequestURI().replace(request.getContextPath(), "");
        System.out.println("endPoint: " + endPoint);

        UserController userController = new UserController();
//        if (endPoint.equals("/join")) {
//            userController.join();
//        } else if (endPoint.equals("/login")) {
//            userController.login();
//        } else if (endPoint.equals("/user")) {
//            userController.user();
//        }

        // reflection -> method를 런타임 시점에서 찾아내 실행
        Method[] methods = userController.getClass()
            .getDeclaredMethods(); // 해당 클래스의 메서드만!, 상속, 구현할 인터페이스 혹은 클래스의 메서드는 X
//        for (Method method : methods) {
//            System.out.println(method.getName());
//            if (endPoint.equals("/" + method.getName())) {
//                try {
//                    method.invoke(userController);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }

        for (Method method : methods) {
            Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
            RequestMapping requestMapping = (RequestMapping) annotation;
            if (endPoint.equals(requestMapping.value())) {
                try {
                    String path = (String) method.invoke(userController);

                    System.out.println("path: " + path);

                    RequestDispatcher dispatcher = request.getRequestDispatcher(path);
                    System.out.println(dispatcher.toString());
                    dispatcher.forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
