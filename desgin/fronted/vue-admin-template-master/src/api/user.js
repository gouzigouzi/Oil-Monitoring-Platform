import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function pageQuery(data) {
  return request({
    url: '/user/page',
    method: 'get',
    params: { 
      'information': data.info,
      'company': data.company,
      'page': data.page,
      'pageSize': data.pageSize
    }
  })
}

export function company() {
  return request({
    url: '/user/company',
    method: 'get',
  })
}

export function addUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

export function deleteByIds(data) {
  return request({
      url: '/user/delete',
      method: 'delete',
      data
  })
}

export function enable(status, username) {
  return request({
      url: '/user/enable'+'/'+status,
      method: 'post',
      params: {username}
  })
}

export function resetPassword(username) {
  return request({
      url: '/user/reset',
      method: 'post',
      params: {username}
  })
}

export function changePassword(username, oldPassword, newPassword) {
  return request({
      url: '/user/change',
      method: 'post',
      params: {username, oldPassword, newPassword}
  })
}
