package com.yonyou.iuap.baseservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yonyou.iuap.baseservice.controller.util.BaseController;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.mvc.type.SearchParams;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：基础Controller——提供数据增、删、改、查
 * @author houlf
 * 2018年6月13日
 */
@SuppressWarnings("all")
public abstract class GenericController<T extends Model> extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(GenericController.class);

	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(PageRequest pageRequest, SearchParams searchParams) {
		Page<T> page = this.service.selectAllByPage(pageRequest, searchParams);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", page);
		return this.buildMapSuccess(map);
	}
	
	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(PageRequest pageRequest, SearchParams searchParams) {
		String id = MapUtils.getString(searchParams.getSearchMap(), "id");
		if (id==null){
            return this.buildSuccess();//前端约定传空id则拿到空对象
        }
		if(StrUtil.isBlank(id)) {
			return this.buildError("msg", "主键id参数为空!", RequestStatusEnum.FAIL_FIELD);
		}else {
			T entity = this.service.findById(id);
			return this.buildSuccess(entity);
		}
	}
	
	
	@RequestMapping(value = "/save")
	@ResponseBody
	public Object save(@RequestBody T entity) {
		JsonResponse jsonResp;
		try {
			this.service.save(entity);
			jsonResp = this.buildSuccess(entity);
		}catch(Exception exp) {
			jsonResp = this.buildError("msg", exp.getMessage(), RequestStatusEnum.FAIL_FIELD);
		}
		return jsonResp;
	}
	
	@RequestMapping(value = "/saveBatch")
	@ResponseBody
	public Object saveBatch(@RequestBody List<T> listData) {
		this.service.saveBatch(listData);
		return this.buildSuccess();
	}
	
	
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(@RequestBody T entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.service.delete(entity);
		return super.buildSuccess();
	}
	
	@RequestMapping(value = "/deleteBatch")
	@ResponseBody
	public Object deleteBatch(@RequestBody List<T> listData, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.service.deleteBatch(listData);
		return super.buildSuccess();
	}
	
	/************************************************************/
	private GenericService<T> service;

	public void setService(GenericService<T> genericService) {
		this.service = genericService;
	}

}