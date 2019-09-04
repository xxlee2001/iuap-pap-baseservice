package com.yonyou.iuap.baseservice.statistics.service;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import com.yonyou.iuap.baseservice.statistics.dao.StatCommonMapper;
import com.yonyou.iuap.baseservice.statistics.support.ParamProcessResult;
import com.yonyou.iuap.baseservice.statistics.util.SearchParamUtil;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("ALL")
@Service
public class StatCommonService {
    private static Logger logger = LoggerFactory.getLogger(StatCommonService.class);

    @Autowired
    private StatCommonMapper statCommonMapper;

    @Autowired
    RefCommonMapper mapper;

    /**
     * 分页查询,聚合统计结果
     *
     * @param pageRequest
     * @param searchParams
     * @return 某一页数据
     */
    public Page<Map> selectAllByPage(PageRequest pageRequest, SearchParams searchParams, String modelCode) {

        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        if (ppr.getSort() != null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), ppr.getSort());
        }
        Page  page = statCommonMapper.selectAllByPage(pageRequest, searchParams, ppr.getTableName(), ppr.getStatStatements(),ppr.getGroupStatements(), ppr.getWhereStatements()).getPage();
        SearchParamUtil.processSelectList(page.getContent(),ppr,mapper);

        return page;


    }


    /**
     * 分页查询,单表动态条件
     *
     * @param pageRequest
     * @param searchParams
     * @return 某一页数据
     */
    public Page<Map> selectFieldsByPage(PageRequest pageRequest, SearchParams searchParams, String modelCode) {

        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        if (ppr.getSort() != null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), ppr.getSort());
        }
        SearchParams useless = new SearchParams();//创建无用挂件，保证mapper解析不失败
        useless.setSearchMap(new HashMap<String,Object>());
        if (  ppr.getGroupStatements()==null ||ppr.getGroupStatements().size()==0  ){//兼容传空条件，解释为全文检索
            Set allFields = new HashSet();
            List groupFields = new ArrayList();
            Class entityClazz =ppr.getStateModel().getmClass();
            for (Field f : EntityUtil.getFields(entityClazz)){
                if (f.getAnnotation(Transient.class)==null){
                    allFields.add( FieldUtil.getColumnName(f)+" as \""+f.getName()+"\"");
                    groupFields.add( f.getName());
                }
            };
            ppr.setGroupFields(groupFields);
            ppr.setGroupStatements(allFields);
        }
        Page  page = statCommonMapper.selectAllByPage(pageRequest,  useless, ppr.getTableName(), null,ppr.getGroupStatements(), ppr.getWhereStatements()).getPage();
        SearchParamUtil.processSelectList(page.getContent(),ppr,mapper);

        return page;


    }

    /**
     * 统计结果全集查询
     *
     * @param searchParams
     * @param modelCode
     * @return 集合函数统计结果
     */
    public List<Map> findAll(  SearchParams searchParams, String modelCode) {
        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        PageRequest pageRequest =null;
        if (ppr.getSort() != null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), ppr.getSort());
        }
        SearchParams useless = new SearchParams();//创建无用挂件，保证mapper解析不失败
        useless.setSearchMap(new HashMap<String,Object>());
        if (  ppr.getGroupStatements()==null ||ppr.getGroupStatements().size()==0  ){//兼容传空条件，解释为全文检索
            Set allFields = new HashSet();
            List groupFields = new ArrayList();
            Class entityClazz =ppr.getStateModel().getmClass();
            for (Field f : EntityUtil.getFields(entityClazz)){
                if (f.getAnnotation(Transient.class)==null){
                    allFields.add( FieldUtil.getColumnName(f)+" as \""+f.getName()+"\"");
                    groupFields.add( f.getName());
                }
            };
            ppr.setGroupFields(groupFields);
            ppr.setGroupStatements(allFields);
        }
        List<Map> list = statCommonMapper.findAll(pageRequest, useless, ppr.getTableName(), ppr.getStatStatements(),ppr.getGroupStatements(), ppr.getWhereStatements());
        SearchParamUtil.processSelectList(list,ppr,mapper);
        return list;

    }

    public List<Map> findDistinct(SearchParams searchParams, String modelCode){
        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        List<Map> list = statCommonMapper.findDistinct( ppr.getStatStatements(), ppr.getTableName(), ppr.getWhereStatements());
        SearchParamUtil.processSelectList(list,ppr,mapper);
        return  list;
    }


}
