import request from '@/utils/request'
// 查询任务列表
export function jobPageList(query) {
  return request({
    url: '/xxl/jobinfo/pageList',
    method: 'get',
    params: query
  })
}
// 批量启动任务
export function batchStart(data) {
  return request({
    url: '/xxl/jobinfo/batchstart',
    method: 'post',
    data: data
  })
}
// 批量停止任务
export function batchStop(data) {
  return request({
    url: '/xxl/jobinfo/batchstop',
    method: 'post',
    data: data
  })
}
// 新增任务
export function saveTask(data) {
  return request({
    url: '/xxl/jobinfo/save',
    method: 'post',
    data: data
  })
}
// 执行一次任务
export function triggerTask(data) {
  return request({
    url: '/xxl/jobinfo/trigger',
    method: 'post',
    params: data
  })
}
// 下次执行时间
export function nextTriggerTime(query) {
  return request({
    url: '/xxl/jobinfo/nextTriggerTime',
    method: 'get',
    params: query
  })
}
//删除任务
export function delTask(dictId) {
  return request({
    url: '/xxl/jobinfo/remove/' + dictId,
    method: 'delete'
  })
}
// 启动任务
export function startTask(data) {
  return request({
    url: '/xxl/jobinfo/start',
    method: 'post',
    params: data
  })
}
// 停止任务
export function stopTask(data) {
  return request({
    url: '/xxl/jobinfo/stop',
    method: 'post',
    params: data
  })
}
// 查询日志列表
export function jobLogList(query) {
  return request({
    url: '/xxl/joblog/pageList',
    method: 'get',
    params: query
  })
}
//删除日志
export function deleteLog(data) {
  return request({
    url: '/xxl/joblog/deleteLog',
    method: 'post',
    params: data
  })
}

//日志清理
export function clearLog(data) {
  return request({
    url: '/xxl/joblog/clearLog',
    method: 'post',
    params: data
  })
}
//执行子任务日志
export function pageSonTasksList(data) {
  return request({
    url: '/xxl/joblog/pageSonTasksList',
    method: 'post',
    params: data
  })
}
//子任务重做
export function redo(data) {
  return request({
    url: '/xxl/jobinfo/redo',
    method: 'post',
    data: data
  })
}
//执行日志
export function logDetailCat(data) {
  return request({
    url: '/xxl/joblog/logDetailCat',
    method: 'post',
    params: data
  })
}

//终止日志
export function logKill(data) {
  return request({
    url: '/xxl/joblog/logKill',
    method: 'get',
    params: data
  })
}
