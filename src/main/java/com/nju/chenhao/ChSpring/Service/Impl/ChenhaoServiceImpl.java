package com.nju.chenhao.ChSpring.Service.Impl;

import com.nju.chenhao.ChSpring.Annotation.ChService;
import com.nju.chenhao.ChSpring.Service.ChenhaoService;
@ChService("ChenhaoServiceImpl")
public class ChenhaoServiceImpl implements ChenhaoService {
    public String query(String name, String age) {
        return "name==="+name+"----age==="+age;
    }
}
