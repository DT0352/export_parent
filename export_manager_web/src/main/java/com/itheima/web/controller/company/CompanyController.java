package com.itheima.web.controller.company;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Reference
    private CompanyService companyService;

//    @RequestMapping(value = "/list", name = "企业列表查询")
//    public String list() {
//        List<Company> companies = companyService.findAll();
//        request.setAttribute("list", companies);
//
//        // /WEB-INF/pages/company/company-list.jsp
//        return "/company/company-list";
//    }


    //@RequiresPermissions("企业管理")  代表只有用户有企业管理的权限,才能访问当前方法
    //相当于XML中的   /company/list.do = perms["企业管理"]
    //@RequiresPermissions("企业管理")
    @RequestMapping(value = "/list", name = "企业列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "1") Integer pageSize) {

        PageInfo pageInfo = companyService.findByPage(pageNum, pageSize);
        request.setAttribute("page", pageInfo);

        return "/company/company-list";
    }


    @RequestMapping(value = "/toAdd", name = "跳转企业新增页面")
    public String toAdd() {
        return "/company/company-add";
    }

    @RequestMapping(value = "/toUpdate", name = "跳转企业编辑页面")
    public String toUpdate(String id) {
        //1. 根据id查询当前企业信息
        Company company = companyService.findById(id);
        request.setAttribute("company", company);

        //2. 转发到修改页面
        return "/company/company-update";
    }

    //新增和修改统一用一个方法处理
    @RequestMapping(value = "/edit", name = "企业新增")
    public String edit(Company company) {
        //根据id判断此次请求是新增还是修改
        if (StringUtils.isEmpty(company.getId())) {//新增
            //设置id
            company.setId(UUID.randomUUID().toString());

            //调用service保存
            companyService.save(company);
        } else {//编辑
            //调用service修改
            companyService.update(company);
        }

        //重定向到list方法
        return "redirect:/company/list.do";
    }

    @RequestMapping(value = "/delete", name = "企业删除")
    public String delete(String id) {
        //调用service删除
        companyService.deleteById(id);

        //重定向到list方法
        return "redirect:/company/list.do";
    }
}
