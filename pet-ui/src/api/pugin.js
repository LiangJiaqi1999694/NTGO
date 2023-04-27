import request from '@/utils/request'
// 查询字典数据列表
export function selectallcategorys(data) {
    return request({
      url: '/xxl/categorymg/selectallcategorys',
      method: 'get',
      params: data
    })
  }
/***左侧编辑 */
export function updatecategory(data) {
    return request({
      url: '/xxl/categorymg/updatecategory',
      method: 'post',
      headers: {"Content-Type":"multipart/form-data"},
      data: data
    })
  }
/*** table 表格 */
export function selectalgos(data) {
    return request({
      url: '/xxl/algomg/selectalgos',
      method: 'get',
      params: data
    })
  }
/*** 左侧删除 */
export function deletecategory(data) {
    return request({
      url: '/xxl/categorymg/deletecategory?id=' + data,
      method: 'delete',
    })
  }
/*** table删除 */
export function deletealgo(data) {
    return request({
      url: '/xxl/algomg/batchdeletealgo?ids='+ data,
      method: 'delete',
    })
  }
/*** 左侧添加 */
export function createcategory(data) {
    return request({
      url: '/xxl/categorymg/createcategory',
      method: 'post',
      headers: {"Content-Type":"multipart/form-data"},
      data: data
    })
  }
/*** 左侧添加 */
export function savealgo(data) {
    return request({
      url: '/xxl/algomg/savealgo',
      method: 'post',
      data: data
    })
  }
/*** 左侧添加 */
export function pageList(data) {
    return request({
      url: '/xxl/jobgroup/pageList',
      method: 'post',
      data: data
    })
  }
/*** 左侧添加 */
export function selectDataDictionary(data) {
    return request({
      url: '/xxl/dataDictionary/selectDataDictionary',
      method: 'post',
      data: data
    })
  }
/*** 右侧编辑 */
export function selectalgosbyid(data) {
    return request({
      url: '/xxl/algomg/selectalgosbyid',
      method: 'get',
      params: data
    })
  }
/*** 右侧编辑 */
export function selectbyid(data) {
    return request({
      url: '/xxl/jobinfo/selectbyid',
      method: 'post',
      headers: {"Content-Type":"multipart/form-data"},
      data: data
    })
  }


