package com.xxl.job.admin.controller.algormg;

import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.service.AlgoMgService;
import com.xxl.job.core.biz.model.Algorithm;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @describe 算法controller
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月19日 下午2:22:13
 *
 */
@Controller
@RequestMapping("/algomg")
public class AlgoMgController {

	@Autowired
	private AlgoMgService algoMgService;
	
	/**
	 *
	 * @catalog 业务支撑平台/插件管理
	 * @title 保存模型
	 * @description 保存模型
	 * @method POST|GET
	 */
	@RequestMapping("/savealgo")
	@ResponseBody
	public ReturnT<Algorithm> saveAlgo(@RequestBody Algorithm algorithm) {
		ReturnT<Algorithm> algorithmReturnT = algoMgService.saveAlgo(algorithm);
		return algorithmReturnT;
	}

	/**
	 * showdoc
	 * 
	 * @catalog 业务支撑平台/插件管理
	 * @title 删除模型
	 * @description 删除模型
	 * @method POST|GET
	 * @url http://192.168.11.92:9090/xxl-job-admin/algomg/deletealgo
	 * @param id 必选 long 算法id
	 * @return {"code":200,"msg":null,"content":"更新成功!"}
	 * @return_param code int 状态码200:成功500：异常
	 * @remark
	 */
	@RequestMapping("/deletealgo")
	@ResponseBody
	public ReturnT<String> deleteAlgo(Long id) {
		return algoMgService.deleteAlgo(id);
	}

	/**
	 * @catalog 业务支撑平台/插件管理/
	 * @title 批量删除算法
	 * @description 批量删除算法
	 * @method POST|GET
	 * @url http://192.168.11.92:9090/xxl-job-admin/algomg/batchdeletealgo
	 * @param ids 必选 string数组 算法id
	 * @return {"code":200,"msg":null,"content":"更新成功!"}
	 * @return_param code int 状态码200:成功500：异常
	 * @remark
	 */
	@RequestMapping("/batchdeletealgo")
	@ResponseBody
	public ReturnT<String> batchDeleteAlgo(@RequestParam(value = "ids[]") String[] ids) {
		return algoMgService.batchDeleteAlgo(ids);
	}

	/**
	 * showdoc
	 * 
	 * @catalog 业务支撑平台/插件管理
	 * @title 算法列表
	 * @description 算法列表
	 * @method POST|GET
	 * @url http://192.168.1.237:9090/xxl-job-admin/algomg/selectalgos
	 * @param categoryId   非必选 int 分组id
	 * @param fuzzySearchName   非必选 string 模糊搜索名称
	 * @param pageNum  非必选 int 起始页数，从1开始，后台默认值1
	 * @param pageSize 非必选 int 每页条数，后台默认值10
	 * @return {"code":200,"msg":null,"content":"更新成功!"}
	 * @remark
	 */
	@RequestMapping("/selectalgos")
	@ResponseBody
	public ReturnT<PageInfo<Algorithm>> selectAlgos(@RequestParam(required = false) Long categoryId, @RequestParam(required = false)String fuzzySearchName,
													@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
		return algoMgService.selectAlgos(categoryId, fuzzySearchName,pageNum, pageSize);
	}

	/**
	 * showdoc
	 * 
	 * @catalog 业务支撑平台/插件管理
	 * @title 根据id查询算法信息
	 * @description 根据id查询算法信息
	 * @method POST|GET
	 * @url http://192.168.1.237:9090/xxl-job-admin/algomg/selectalgosbyid
	 * @param id 必选 long 算法id
	 * @return {"code":200,"msg":null,"content":"更新成功!"}
	 * @return_param code int 状态码200:成功500：异常
	 * @return_param id int 算法id
	 * @return_param name String 算法名称
	 * @return_param description String 算法描述
	 * @return_param jobhandler String 算法处理类
	 * @return_param treeid int 所属编目id
	 * @return_param state int 状态值
	 * @return_param principal String 算法负责人
	 * @return_param email String 算法负责人邮箱
	 * @return_param registryid int 节点id
	 * @remark
	 */
	@RequestMapping("/selectalgosbyid")
	@ResponseBody
	public ReturnT<Algorithm> selectAlgosById(Long id) {
		return algoMgService.selectAlgosById(id);
	}
}
