package com.esports.column.service;


import com.esports.column.bean.db1.Navigation;
import com.esports.column.dao.db1.NavigationDao;
import com.esports.column.dto.NavigationDto;
import com.esports.constant.GlobalCode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NavigationManager {

    @Autowired
    private NavigationDao navigationDao;

    public List<NavigationDto> getSubNavs(String navCode, Integer source) {
        List<NavigationDto> dtos = null;
        Navigation navigation = navigationDao.findByNavCode(navCode);
        if (null == navigation) {
            return dtos;
        }
        List<Navigation> list = navigation.getChildren();
        dtos = this.assembleNavData(list, source);
        return dtos;
    }

    public List<NavigationDto> getTopNavs(Integer source) {
        List<NavigationDto> dtos = null;
        List<Navigation> list = navigationDao.getTopNavs();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        dtos = this.assembleNavData(list, source);
        return dtos;
    }

    protected List<NavigationDto> assembleNavData(List<Navigation> list, Integer source) {
        List<NavigationDto> dtos = null;
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<Navigation> iterator = list.iterator();
        dtos = new ArrayList<NavigationDto>();
        NavigationDto dto = null;
        while (iterator.hasNext()) {
            Navigation nav = iterator.next();
            Integer okStatus = nav.getOkStatus();
            if (!GlobalCode._ENABLE.getCode().equals(okStatus)) {
                continue;
            }
            dto = new NavigationDto();
            String gameCode = GlobalCode._PC.getCode().equals(source) ? nav.getPcGameCode() : nav.getH5GameCode();
            List<Navigation> childres = nav.getChildren();
            Boolean hasChild = !CollectionUtils.isEmpty(childres);
            dto.setHasChild(hasChild);
            dto.setApiCode(nav.getApiCode());
            dto.setGameCode(gameCode);
            dto.setCode(nav.getNavCode());
            dto.setIcon(nav.getNavIcon());
            dto.setTitle(nav.getNavName());
            dtos.add(dto);
        }
        return dtos;
    }

    public JSONArray getNavTree(Integer source) {
        JSONArray jsonArr = new JSONArray();
        List<NavigationDto> roots = this.getTopNavs(source);
        JSONObject object = null;
        for (NavigationDto root : roots) {
            object = this.buildChildNodes(root, source);
            jsonArr.add(object);
        }
        return jsonArr;
    }

    private JSONObject buildChildNodes(NavigationDto root, Integer source) {
        JSONObject json = null;
        String parentCode = root.getCode();
        json = new JSONObject();
        List<NavigationDto> children = this.getSubNavs(parentCode, source);
        json.put("code", root.getCode());
        json.put("icon", root.getIcon());
        json.put("title", root.getTitle());
        json.put("hasChild", CollectionUtils.isEmpty(children) ? Boolean.FALSE : Boolean.TRUE);
        JSONArray jsonArr = new JSONArray();
        if(!CollectionUtils.isEmpty(children)){
            JSONObject childObj = null;
            for (NavigationDto child : children) {
                childObj = buildChildNodes(child, source);
                jsonArr.add(childObj);
            }
        }
        json.put("children", jsonArr);
        return json;
    }
}
