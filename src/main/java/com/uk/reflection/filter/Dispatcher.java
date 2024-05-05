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
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Dispatcher implements Filter {

    private boolean matching = false;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {
//        System.out.println("디스패쳐 진입");

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
            // 이 어노테이션에
            Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
            RequestMapping requestMapping = (RequestMapping) annotation;

            if (endPoint.equals(requestMapping.value())) {
                matching = true;
                try {
                    Parameter[] parameters = method.getParameters();
                    String path = null;
                    if (parameters.length != 0) {
                        // 해당 dtoInstance를 리플렉션해서 set함수 호출(username, password)
                        // 파라미터가 여러개면 for문 돌려서 모든 파라미터를 확인해 넣어줘야함.
                        Object dtoInstance = parameters[0].getType().newInstance();
                        // Enumeration<String> keys = request.getParameterNames(); // username, password
                        setData(dtoInstance, request);
                        // 받은 keys 값을 변형 username => setUsername
                        // 받은 keys 값을 변형 password => setPassword
//                        String keyMethodName = "set" + "Username";
//                        System.out.println("parameters[0].getType(): " + parameters[0].getType());
//                        String username = request.getParameter("username");
//                        String password = request.getParameter("password");
//                        System.out.println("username: " + par);
//                        System.out.println("password: " + password);
                        path = (String) method.invoke(userController, dtoInstance);
                    } else {
                        path = (String) method.invoke(userController);
                    }
//                    System.out.println("path: " + path);
                    // RequestDispatcher는 필터를 다시 안탐!!, request들고 파일을 찾아서 응답하는 일을 함.
                    RequestDispatcher dispatcher = request.getRequestDispatcher(path);
                    dispatcher.forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (!matching) {
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("잘못된 주소 요청입니다. 404 에러");
            out.flush();
        }
    }

    private <T> void setData(T instance, HttpServletRequest request) {
        Enumeration<String> keys = request.getParameterNames(); // 크기: 2 (username, password)
        String key;
        while (keys.hasMoreElements()) {
            key = keys.nextElement().toString();
            System.out.println("key: " + key);
            String methodKey = keyToMethodKey(key);
            String value = request.getParameter(key);

            Method[] methods = instance.getClass()
                .getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodKey)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        try {
                            Object typedValue = convertStringToType(value, parameterTypes[0]);
                            method.invoke(instance, typedValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private String keyToMethodKey (String key){
        String firstKey = "set";
        String upperKey = key.substring(0, 1).toUpperCase() + key.substring(1);

        return firstKey + upperKey;
    }

    private Object convertStringToType(String value, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // 기본적으로 String 반환
    }
}