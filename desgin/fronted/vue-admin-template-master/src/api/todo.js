import request from '@/utils/request'

export function pageQuery(data) {
    return request({
        url: '/todo/page',
        method: 'get',
        params: { 
        'information': data.info,
        'company': data.company,
        'submitType': data.submitType,
        'todoStatus': data.todoStatus,
        'page': data.page,
        'pageSize': data.pageSize
        }
    })
}

export function todoCount() {
    return request({
        url: '/todo/count',
        method: 'get',
    })
}

export function deleteByIds(data) {
    return request({
        url: '/todo/delete',
        method: 'delete',
        data
    })
}

export function agreeByIds(data) {
    return request({
        url: '/todo/agree',
        method: 'post',
        data
    })
}

export function reject(data) {
    return request({
        url: '/todo/reject',
        method: 'post',
        params: { 
            'rejectIdList': data.rejectIdList,
            'rejectReason': data.rejectReason,
            'detailReason': data.detailReason,
            // data
        }
    })
}

export function viewVehicle(todoId) {
    return request({
        url: '/todo/viewVehicle/' + todoId,
        method: 'get',
    })
}

export function viewSample(todoId) {
    return request({
        url: '/todo/viewSample/' + todoId,
        method: 'get',
    })
}

export function viewDeleteVehicle(todoId) {
    return request({
        url: '/todo/viewDeleteVehicle/' + todoId,
        method: 'get',
    })
}