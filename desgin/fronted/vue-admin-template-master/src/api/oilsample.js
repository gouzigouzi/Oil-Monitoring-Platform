import request from '@/utils/request'

export function pageQuery(data) {
    return request({
        url: '/oilsample/page',
        method: 'get',
        params: {
            'information': data.info,
            'region': data.region,
            'startDateTime': data.date1,
            'endDateTime': data.date2,
            'page': data.page,
            'pageSize': data.pageSize
        }
    })
}

export function deleteByIds(data) {
    return request({
        url: '/oilsample/delete',
        method: 'delete',
        data
    })
}

export function queryOilSample(data) {
    return request({
        url: '/oilsample/query',
        method: 'get',
        params: {
            'oilSampleId': data
        }
    })
}

export function addOilSample(data) {
    return request({
        url: '/oilsample/add',
        method: 'post',
        data
    })
}