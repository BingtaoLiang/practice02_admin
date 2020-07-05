package practice02_admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import practice02_admin.model.News;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.provider.url}")
    private String url;

    @RequestMapping("/")
    public String index(Model model){
        List<News> allNews = restTemplate.getForObject(url + "/selectNews", List.class);
        model.addAttribute("allNews",allNews);
        return "index";
    }

    @RequestMapping("/publish")
    public String publish(){
        return "publish";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/denglu")
    public String denglu(){
        return "denglu";
    }

}
