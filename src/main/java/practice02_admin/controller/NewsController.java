package practice02_admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import practice02_admin.model.News;
import practice02_admin.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class NewsController {
  @Autowired private RestTemplate restTemplate;

  @Value("${service.provider.url}")
  private String url;

  // 增
  @RequestMapping("/insertNews")
  public String insertNews(
      @RequestParam(name = "id", defaultValue = "-1") Integer id,
      @RequestParam("title") String title,
      @RequestParam("content") String content,
      HttpServletRequest request) {
    User user = (User) request.getSession().getAttribute("user");
    restTemplate.postForObject(
        url + "/insertNews?id=" + id + "&title=" + title + "&content=" + content,
        user,
        String.class);
    return "redirect:/";
  }

  // 查
  @RequestMapping("/selectNews")
  public String selectNews(Model model) {
    List<News> allNews = restTemplate.getForObject(url + "/selectNews", List.class);

    model.addAttribute("allNews", allNews);
    return "index";
  }

  // 改
  @RequestMapping("/updateNews/{id}")
  public String updateNews(@PathVariable("id") Integer id, Model model) {
    News news = restTemplate.getForObject(url + "/findNewsById/{id}", News.class, id);
    //    model.addAttribute("news",news);
    Integer newsId = news.getId();
    String title = news.getTitle();
    String content = news.getContent();
    model.addAttribute("id", newsId);
    model.addAttribute("title", title);
    model.addAttribute("content", content);
    return "publish";
  }

  // 删
  @RequestMapping("/deleteNews/{id}")
  public String deleteNews(@PathVariable("id") Integer id) {
    restTemplate.delete(url + "/deleteNews/{id}", id);
    return "redirect:/";
  }

  // excel数据导入数据库
  @PostMapping("/impNews")
  public String importNews(
      @RequestParam(value = "file") MultipartFile file, Map<String, Object> map) {
    Resource invoicesResource = file.getResource();
    String filename = invoicesResource.getFilename();

    if (filename.isEmpty()) {
      return "redirect:/";
    } else {
      LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
      parts.add("file", invoicesResource);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity =
          new HttpEntity<>(parts, httpHeaders);
      String response = restTemplate.postForObject(url + "/impNews", httpEntity, String.class);
      if ("success".equals(response)) {
        return "redirect:/";
      } else {
        map.put("msg", "导入出错，请检查后重试");
        return "redirect:/";
      }
    }
  }

  // 数据库导出到excel
//  @RequestMapping("/expNews")
//  public void expNews(HttpServletResponse response) {
//    restTemplate.getForObject(url + "/expNews", String.class, response);
//  }

}
