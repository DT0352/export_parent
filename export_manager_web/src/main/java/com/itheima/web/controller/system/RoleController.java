package com.itheima.web.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.Role;
import com.itheima.service.system.ModuleService;
import com.itheima.service.system.RoleService;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    @Reference
    private RoleService roleService;

    @Reference
    private ModuleService moduleService;

    @RequestMapping(value = "/list", name = "角色列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo pageInfo = roleService.findByPage(getCompanyId(), pageNum, pageSize);
        request.setAttribute("page", pageInfo);

        return "/system/role/role-list";
    }


    @RequestMapping(value = "/toAdd", name = "跳转角色新增页面")
    public String toAdd() {
        return "/system/role/role-add";
    }


    @RequestMapping(value = "/toUpdate", name = "跳转角色编辑页面")
    public String toUpdate(String id) {
        //1. 根据id查询当前角色信息
        Role role = roleService.findById(id);
        request.setAttribute("role", role);

        //2. 转发到修改页面
        return "/system/role/role-update";
    }

    //新增和修改统一用一个方法处理
    @RequestMapping(value = "/edit", name = "角色新增")
    public String edit(Role role) {
        if (StringUtils.isEmpty(role.getId())) {
            //1. 设置主键
            role.setId(UUID.randomUUID().toString());

            //2. 设置企业信息
            role.setCompanyId(getCompanyId());
            role.setCompanyName(getCompanyName());

            roleService.save(role);
        } else {
            roleService.update(role);
        }

        //重定向到list方法
        return "redirect:/system/role/list.do";
    }

    @RequestMapping(value = "/delete", name = "角色删除")
    public String delete(String id) {
        //调用service删除
        roleService.delete(id);

        //重定向到list方法
        return "redirect:/system/role/list.do";
    }

    @RequestMapping(value = "/roleModule", name = "进入角色分配权限的页面")
    public String roleModule(@RequestParam("roleid") String roleId) throws JsonProcessingException {
        //1. 显示出角色名称(查询角色信息)
        Role role = roleService.findById(roleId);
        request.setAttribute("role", role);

        //2. 显示出所有的权限, 等待勾选( 查询所有权限 )
        List<Module> moduleList = moduleService.findAll();

        //3. 回显当前角色已经分配了的权限( 查询中间表 )
        List<String> moduleIds = roleService.findModuleIdsByRoleId(roleId);

        //4. 将上面两部分数据做成ZTree需要的json格式
        List<Map> list = new ArrayList<>();
        for (Module module : moduleList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", module.getId());
            map.put("pId", module.getParentId());
            map.put("name", module.getName());

            //默认勾选节点
            if (moduleIds.contains(module.getId())) {
                map.put("checked", true);
            }
            list.add(map);
        }
        String value = new ObjectMapper().writeValueAsString(list);
        //System.out.println(value);
        request.setAttribute("data", value);


        //5. 跳转到给角色分配权限页面
        return "/system/role/role-module";
    }


    @RequestMapping(value = "/changeModule", name = "角色删除")
    public String changeModule(@RequestParam("roleid") String roleId, String[] moduleIds) {
        //调用service进行角色分配权限
        roleService.changeModule(roleId,moduleIds);

        //重定向到list方法
        return "redirect:/system/role/list.do";
    }


}
