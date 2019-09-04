package com.yonyou.iuap.baseservice.support.excel;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelWriter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class SimpleExcelExporter {
	
	public static final String splitSign = ":";
	
	public static SimpleExcelExporter inst() {
		return Inner.inst;
	}
	
	/**
	 * 导出文件
	 * @param listHeader	格式:{"code:编码","name:名称",...}
	 * @param listData
	 * @param output
	 */
	public void export(String[] listHeader, List listData, String output) {
		ExcelWriter writer = new ExcelWriter(output);
		//创建sheet
        writer.setSheet("sheet1");
		//写入Header
		List<String> listKey = this.writeHeader(writer, listHeader);
		//写入Body数据
		this.writeBody(writer, listData, listKey);
		//写入磁盘文件
		writer.flush();
		writer.close();
	}
	
	/**
	 * 导出到输出流
	 * @param listHeader	格式:{"code:编码","name:名称",...}
	 * @param listData
	 * @param os
	 */
	public void export(String[] listHeader, List listData, OutputStream os) {
		ExcelWriter writer = new ExcelWriter(false);
		//创建sheet
		writer.setSheet("sheet1");
		//写入Header
		List<String> listKey = this.writeHeader(writer, listHeader);
		//写入Body数据
		this.writeBody(writer, listData, listKey);
		//写入输出流
		writer.flush(os);
		writer.close();
	}
	
	/**
	 * 返回Header Code List
	 * @param listHeader
	 * @return
	 */
	private List<String> writeHeader(ExcelWriter writer, String[] listHeader) {
		List<String> listCode = new ArrayList<String>();
		for(int i=0; i<listHeader.length; i++) {
			String[] header = listHeader[i].split(":");
			if(header.length==2) {
				listCode.add(header[0]);
				writer.writeCellValue(i, 0, header[1]);
			}else {
				throw new RuntimeException("Excel Header信息格式不正确，请检查:"+listHeader.toString());
			}
		}
		return listCode;
	}
	
	private void writeBody(ExcelWriter writer, List<Object> listData, List<String> listHeader) {
		for(int row=0; row<listData.size(); row++) {
			if(listData.get(row) instanceof Map) {
				this.writeBodyByMap(writer, row+1, (Map)listData.get(row), listHeader);
			}else {
				this.writeBodyByVo(writer, row+1, listData.get(row), listHeader);
			}
		}
	}
	
	private void writeBodyByMap(ExcelWriter writer, int row,  Map dataMap, List<String> listHeader) {
		for(int col=0; col<listHeader.size(); col++) {
			writer.writeCellValue(col, row, dataMap.get(listHeader.get(col)));
		}
	}

	private void writeBodyByVo(ExcelWriter writer, int row, Object data, List<String> listHeader) {
		for(int col=0; col<listHeader.size(); col++) {
			Object value = ReflectUtil.getFieldValue(data, listHeader.get(col));
			writer.writeCellValue(col, row, value);
		}
	}
	
	/********************************************************/
	private static class Inner{
		private static SimpleExcelExporter inst = new SimpleExcelExporter();
	}

}