package com.springapp.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class TestController {

    private String TEMP_KEY="mydata";

	@RequestMapping(method = RequestMethod.GET)
	public String select(ModelMap model, HttpServletRequest request) {
		return "test";
	}


    @RequestMapping(value="add", method = {RequestMethod.GET})
    public String add( @RequestParam("key") String key, @RequestParam("value") String value, ModelMap model, HttpServletRequest request){
        request.getSession(false).setAttribute(key,value );
        model.addAttribute("message", "added key:"+key+" value:"+value);
        return "test";
    }
}