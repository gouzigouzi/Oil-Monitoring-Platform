import request from '@/utils/request'

export function addVehicle(data) {
    return request({
        url: '/vehicle/add',
        method: 'post',
        data
    })
}

export function pageQuery(data) {
    return request({
        url: '/vehicle/page',
        method: 'get',
        params: {
            'information': data.info,
            'region': data.region,
            'startDateTime': data.startDateTime,
            'endDateTime': data.endDateTime,
            'page': data.page,
            'pageSize': data.pageSize
        }
    })
}

export function region() {
    return request({
        url: '/vehicle/region',
        method: 'get',
    })
}

export function deviceNoList() {
    return request({
        url: '/vehicle/list',
        method: 'get',
    })
}

export function queryVehicle(data) {
    return request({
        url: '/vehicle/query',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function deleteByIds(data) {
    return request({
        url: '/vehicle/delete',
        method: 'delete',
        data
    })
}

export function edit(data) {
    return request({
        url: '/vehicle/edit',
        method: 'post',
        data
    })
}

export function detail(data) {
    return request({
        url: '/vehicle/detail',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function rank() {
    return request({
        url: '/vehicle/rank',
        method: 'get',
    })
}

export function accumulated() {
    return request({
        url: '/vehicle/accumulated',
        method: 'get',
    })
}

export function vehicleScatter(data) {
    return request({
        url: '/vehicle/vehicleScatter',
        method: 'get',
        params: {
            'deviceLocationProvince': data
        }
    })
}