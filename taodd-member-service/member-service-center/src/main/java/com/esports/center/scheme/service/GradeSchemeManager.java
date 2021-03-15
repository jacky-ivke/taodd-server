package com.esports.center.scheme.service;

import com.esports.center.scheme.bean.db1.GradeScheme;
import com.esports.center.scheme.dao.db1.GradeSchemeDao;
import com.esports.constant.GlobalCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeSchemeManager {

    @Autowired
    private GradeSchemeDao gradeSchemeDao;

    public String getDefaultGradeCode() {
        String gradeCode = GlobalCode._DEFAULT_GRADE.getMessage();
        Integer okStatus = GlobalCode._ENABLE.getCode();
        Boolean setDefault = Boolean.TRUE;
        GradeScheme scheme = gradeSchemeDao.findByOkStatusAndSetDefault(okStatus, setDefault);
        if (null != scheme) {
            gradeCode = scheme.getSchemeCode();
        }
        return gradeCode;
    }
}
