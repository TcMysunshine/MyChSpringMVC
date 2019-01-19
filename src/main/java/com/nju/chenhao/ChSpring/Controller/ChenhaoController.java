package com.nju.chenhao.ChSpring.Controller;

import com.nju.chenhao.ChSpring.Annotation.*;
import com.nju.chenhao.ChSpring.Service.ChenhaoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ChController
@ChRequestMapping("/chenhao")
public class ChenhaoController {

    @ChAutowired("ChenhaoServiceImpl")
    private ChenhaoService chenhaoService;

    @ChRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @ChRequestParam("name") String name,
                      @ChRequestParam("age") String age){
        try {
            PrintWriter pw = response.getWriter();
            String result=chenhaoService.query(name,age);
            pw.write(result);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
