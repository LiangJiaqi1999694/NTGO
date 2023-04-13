package com.xxl.job.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.dao.JobParamDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.algomg.AlgoMgDao;
import com.xxl.job.admin.dao.algomg.AlgoParamDao;
import com.xxl.job.admin.service.AlgoMgService;
import com.xxl.job.admin.service.CategoryMgService;
import com.xxl.job.core.biz.model.Algorithm;
import com.xxl.job.core.biz.model.AlgorithmParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class AlgoMgServiceImpl implements AlgoMgService {

	@Autowired
	private AlgoMgDao algoMgDao;

	@Autowired
	private AlgoParamDao algoParamDao;
	
	@Autowired
	private JobParamDao jobParamDao;
	 
	@Autowired
	private CategoryMgService categoryMgService;
	
	@Autowired
	private XxlJobInfoDao xxlJobInfoDao;
	
	// 需要新增参数标识
	private final String ALGOPARAM_INSET_MARK = "INSERT";

	// 需要更新参数标识
	private final String ALGOPARAM_UPDATE_MARK = "UPDATE";

	// 需要删除标识
	private final String ALGOPARAM_DELETE_MARK = "DELETE";

	/**
	 * 创建算法
	 */
	@Override
	@Transactional
	public ReturnT<Algorithm> saveAlgo(Algorithm algorithm) {

		if (ObjectUtils.isEmpty(algorithm)) {
			return new ReturnT(ReturnT.FAIL_CODE, null, "ERROR:参数为空！");
		}

		if (StringUtils.isEmpty(algorithm.getName())) {
			return new ReturnT(ReturnT.FAIL_CODE, null, "ERROR:算法名称为空！");
		}
		
		if (StringUtils.isEmpty(algorithm.getJobhandler())) {
			return new ReturnT(ReturnT.FAIL_CODE, null, "ERROR:jobhandler为空！");
		}
		
		if (ObjectUtils.isEmpty(algorithm.getBlockStrategy())) {
			return new ReturnT(ReturnT.FAIL_CODE, null, "ERROR:阻塞策略为空！");
		}
		
		if (StringUtils.isEmpty(algorithm.getCategoryId())) {
			return new ReturnT(ReturnT.FAIL_CODE, null, "ERROR:categoryId为空！");
		}

		Date now = new Date();
		algorithm.setUpdatetime(now);
		if (ObjectUtils.isEmpty(algorithm.getId())) {// 新增
			algorithm.setCreatetime(now);
			if (!algoMgDao.createAlgo(algorithm)) {
				return new ReturnT(ReturnT.FAIL_CODE, algorithm, "保存失败！");
			}
		} else {// 保存
			if (!algoMgDao.updateAlgosById(algorithm)) {
				return new ReturnT(ReturnT.FAIL_CODE, algorithm, "保存失败！");
			}
		}
		
		//参数
		List<AlgorithmParam> params = algorithm.getParams();
		if(!CollectionUtils.isEmpty(params)) {
			for(int i=0;i<params.size();i++) {
				params.get(i).setAlgoId(algorithm.getId());
			}
			// 保存任务参数
			List<AlgorithmParam> algorithmParams = algoParamDao.selectParamsByAlgoId(algorithm.getId());
			Map<String, List<AlgorithmParam>> maps = getNeedInsertAlgorithmParams(algorithmParams, algorithm);
			// 新增参数保存
			List<AlgorithmParam> insertAlgorithmParams = maps.get(ALGOPARAM_INSET_MARK);
			if(!CollectionUtils.isEmpty(insertAlgorithmParams)) {
//				for(int i=0;i<insertAlgorithmParams.size();i++) {
//					insertAlgorithmParams.get(i).setId(uidGenerator.nextValue("XXL:ALGORITHMPARAMS"));
//				}
				algoParamDao.batchaddParam(insertAlgorithmParams);
			}
			// 更新参数保存
			List<AlgorithmParam> updateAlgorithmParams = maps.get(ALGOPARAM_UPDATE_MARK);
			if(!CollectionUtils.isEmpty(updateAlgorithmParams)) {
				for(int i=0;i<updateAlgorithmParams.size();i++) {
					algoParamDao.updateParam(updateAlgorithmParams.get(i));
				}
			}
			// 缺失参数删除
			if(!CollectionUtils.isEmpty(algorithmParams)) {
				Long[] paramIds = new Long[algorithmParams.size()];
				for(int i=0;i<algorithmParams.size();i++) {
					paramIds[i] = algorithmParams.get(i).getId();
				}
				algoParamDao.batchDeleteParamByIds(paramIds);
				jobParamDao.deleteByParamIds(paramIds);
			}
		}
		return new ReturnT<Algorithm>(ReturnT.SUCCESS_CODE, null, "保存成功！");
	}

	/**
	 * 
	 * @param existAlgorithmParams
	 * @param algorithm
	 * @return
	 */
	private Map<String, List<AlgorithmParam>> getNeedInsertAlgorithmParams(List<AlgorithmParam> existAlgorithmParams,
			Algorithm algorithm) {
		Map<String, List<AlgorithmParam>> maps = new HashMap<String, List<AlgorithmParam>>();
		List<AlgorithmParam> params = algorithm.getParams();
		if (!CollectionUtils.isEmpty(params)) {
			List<AlgorithmParam> insertAlgorithmParams = new ArrayList<AlgorithmParam>();
			List<AlgorithmParam> updateAlgorithmParams = new ArrayList<AlgorithmParam>();
			for (int i = 0; i < params.size(); i++) {
				if (ObjectUtils.isEmpty(params.get(i).getId())) {// 新增
					insertAlgorithmParams.add(params.get(i));
				} else {// 更新
					updateAlgorithmParams.add(params.get(i));
					Iterator<AlgorithmParam> iterator = existAlgorithmParams.iterator();
					while (iterator.hasNext()) {
						if (params.get(i).getId().equals(iterator.next().getId())) {
							iterator.remove();
						}
					}
				}
			}
			maps.put(ALGOPARAM_INSET_MARK, insertAlgorithmParams);
			maps.put(ALGOPARAM_UPDATE_MARK, updateAlgorithmParams);
			maps.put(ALGOPARAM_DELETE_MARK, existAlgorithmParams);
		}
		return maps;
	}

	@Override
	public ReturnT<PageInfo<Algorithm>> selectAlgos(Long categoryId, String fuzzySearchName, Integer pageNum, Integer pageSize) {
		List<Long> ids = null;
		if (!ObjectUtils.isEmpty(categoryId)) {
			ids = categoryMgService.selectcategoryById(categoryId);
		}
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<Algorithm> pageInfo = new PageInfo<Algorithm>(algoMgDao.selectAlgos(ids,fuzzySearchName));
		return new ReturnT(pageInfo);
	}

	@Override
	@Transactional
	public ReturnT<String> deleteAlgo(Long id) {
		List<XxlJobInfo> xxlJobInfos = xxlJobInfoDao.loadByAlgoId(id);
		if(!CollectionUtils.isEmpty(xxlJobInfos)) {
			return new ReturnT(ReturnT.FAIL_CODE, "已经绑定任务，解绑后重试，删除失败！");
		}else{
			
			if (algoMgDao.deleteAlgo(id)) {
				algoParamDao.deleteParamByAlgoId(id);
				return new ReturnT(ReturnT.SUCCESS_CODE, "删除成功！");
			}
		}
		return new ReturnT(ReturnT.FAIL_CODE, "删除失败！");
	}

	@Override
	public ReturnT<String> batchDeleteAlgo(String[] ids) {
		if (ids == null || ids.length == 0) {
			return new ReturnT(ReturnT.FAIL_CODE, "ids 为空，删除失败！");
		}
		Long[] idsinl = new Long[ids.length];
		for (int i = 0; i < idsinl.length; i++) {
			idsinl[i] = Long.parseLong(ids[i]);
		}
		if (algoMgDao.batchDeleteAlgo(idsinl)) {
			algoParamDao.batchDeleteParamByAlgoId(idsinl);
			return new ReturnT(ReturnT.SUCCESS_CODE, "删除成功！");
		}
		return new ReturnT(ReturnT.FAIL_CODE, "删除失败！");
	}

	@Override
	public ReturnT<String> updateAlgo(Algorithm algorithm) {

		if (ObjectUtils.isEmpty(algorithm) || ObjectUtils.isEmpty(algorithm.getId())) {
			return new ReturnT(ReturnT.FAIL_CODE, "更新失败！");
		}

		Algorithm algo = algoMgDao.selectAlgosById(algorithm.getId());
		algo.setUpdatetime(new Date());
		if (!StringUtils.isEmpty(algorithm.getName())) {
			algo.setName(algorithm.getName());
		}

		if (!StringUtils.isEmpty(algorithm.getDescription())) {
			algo.setDescription(algorithm.getDescription());
		}

		if (!StringUtils.isEmpty(algorithm.getJobhandler())) {
			algo.setJobhandler(algorithm.getJobhandler());
		}

		if (!ObjectUtils.isEmpty(algorithm.getCategoryId())){
			algo.setCategoryId(algorithm.getCategoryId());;
		}


		if (!StringUtils.isEmpty(algorithm.getRegistryId())) {
			algo.setRegistryid(algorithm.getRegistryId());
		}

		if (algoMgDao.updateAlgosById(algo)) {
			return new ReturnT(ReturnT.SUCCESS_CODE, "更新成功！");
		} else {
			return new ReturnT(ReturnT.FAIL_CODE, "更新失败！");
		}

	}

	@Override
	public ReturnT<Algorithm> selectAlgosById(Long id) {
		Algorithm algorithm = algoMgDao.selectAlgosById(id);
		algorithm.setParams(algoParamDao.selectParamsByAlgoId(id));
		return new ReturnT(algorithm);
	}
}
