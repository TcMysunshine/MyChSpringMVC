package com.nju.chenhao.ChSpring.Servlet;

import com.nju.chenhao.ChSpring.Annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID=1L;
    List<String> classNames=new ArrayList<String>();
    Map<String,Object> beansMap=new HashMap<String,Object>();
    Map<String,Object> handlerMap=new HashMap<String,Object>();
    public void init(){
        //扫描所有的bean
        scanPackage("com.nju.chenhao");
        //根据扫描出的className实例化创建对象
        doInstance();
        //依赖注入 Service 注入Controller,Autowired
        doIoc();
        //将url与方法建立映射关系
        buildUrlMapping();

    }
    private void scanPackage(String basePackage){
        URL url=this.getClass().getClassLoader().getResource("/"+basePackage.
                replaceAll("\\.","/"));
        String fileStr=url.getFile();
        File file=new File(fileStr);
        String[] filepaths= file.list();
        System.out.println("filepaths:"+filepaths);
        System.out.println("fileStr:"+fileStr);
        for (String path:filepaths){
            File fileTemp=new File(fileStr+path);
            if(fileTemp.isDirectory()){
                scanPackage(basePackage+"."+path);
            }
            else{
                classNames.add(basePackage+"."+fileTemp.getName());
            }
        }
    }
    private void doInstance(){
        if(classNames.size()<=0){
            System.out.println("扫描失败");
        }
        else{
            for(String className:classNames){
                String cn=className.replace(".class","");
                try {
                    Class<?> clazz = Class.forName(cn);
                    if(clazz.isAnnotationPresent(ChController.class)){
                        Object instance=clazz.newInstance();
                        ChRequestMapping chRequestMapping=clazz.getAnnotation(ChRequestMapping.class);
                        String key=chRequestMapping.value();
                        beansMap.put(key,instance);
                    }
                    else if(clazz.isAnnotationPresent(ChService.class)){
                        Object instance=clazz.newInstance();
                        ChService chService=clazz.getAnnotation(ChService.class);
                        String key=chService.value();
                        beansMap.put(key,instance);
                    }
                    else{
                        continue;
                    }

                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }catch(InstantiationException e){

                }catch (IllegalAccessException e){

                }
            }
        }
    }
    private void doIoc(){
        if(beansMap.entrySet().size()<=0){
            System.out.println("没有实例化的类");
        }
        for(Map.Entry<String,Object> entry:beansMap.entrySet()){
            Object instance=entry.getValue();
            Class<?> clazz=instance.getClass();
            Field[] fields=clazz.getDeclaredFields();
            for(Field field:fields){
                if(field.isAnnotationPresent(ChAutowired.class)){
                    ChAutowired chAutowired=field.getAnnotation(ChAutowired.class);
//                    private设置属性 权限打开
                    field.setAccessible(true);
                    try {
//                        将实例化后的对象注入到属性中
                        field.set(instance, beansMap.get(chAutowired.value()));
                    }catch (IllegalAccessException e){

                    }
                }else{
                    continue;
                }
            }
        }
    }
    private void buildUrlMapping(){
        if(beansMap.entrySet().size()<=0){
            System.out.println("没有实例化的类");
        }else{
            for(Map.Entry<String,Object> entry:beansMap.entrySet()){
                Object instance = entry.getValue();
                Class<?> clazz=instance.getClass();
                if(clazz.isAnnotationPresent(ChController.class)){
                    ChRequestMapping chRequestMapping=clazz.getAnnotation(ChRequestMapping.class);
                    String classpath=chRequestMapping.value();
                    Method[] methods=clazz.getMethods();
                    for(Method method:methods){
                        if(method.isAnnotationPresent(ChRequestMapping.class)){
                            ChRequestMapping methodRequestMapping=method.getAnnotation(ChRequestMapping.class);
                            String methodMappingPath=methodRequestMapping.value();
                            handlerMap.put(classpath+methodMappingPath,method);
                        }else{
                            continue;
                        }
                    }
                }else{
                    continue;
                }
            }
        }
    }
    private static Object[] getArgs(HttpServletRequest req, HttpServletResponse resp,Method method){
        Class<?>[] paramClazzs=method.getParameterTypes();
        Object[] args=new Object[paramClazzs.length];
        int args_i=0;
        int index=0;
        for(Class<?> paramClazz:paramClazzs){
            if(ServletRequest.class.isAssignableFrom(paramClazz)){
                args[args_i++]=req;
            }
            if(ServletResponse.class.isAssignableFrom(paramClazz)){
                args[args_i++]=resp ;
            }
            Annotation[][] debugAnnotation=method.getParameterAnnotations();
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if(paramAns.length>0){
                for(Annotation paramAn:paramAns){
                    if(ChRequestParam.class.isAssignableFrom(paramAn.getClass())){
                        ChRequestParam chRequestParam=(ChRequestParam)paramAn;
                        args[args_i++]=req.getParameter(chRequestParam.value());
                    }
                }

            }
            index++;
        }
        return args;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException{
        String uri=req.getRequestURI();
        String context=req.getContextPath();
        String url=uri.replace(context,"");
        Method method=(Method)handlerMap.get(url);
//        ChenhaoController chenhaoController=(ChenhaoController)beansMap.get("/chenhao");
        Object instance=beansMap.get("/"+uri.split("/")[1]);
        System.out.println("开始Post");
        Object[] args=getArgs(req,resp,method);
        try {
            method.invoke(instance, args);
        }catch (IllegalAccessException e){

        }catch (InvocationTargetException e){
        }

//        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }
}
