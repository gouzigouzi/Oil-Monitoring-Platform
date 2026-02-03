import request from '@/utils/request'

export function life(data) {
    return request({
        url: '/oillife/life',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function engineLine(data) {
    return request({
        url: '/oillife/engineline',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function engineBar(data) {
    return request({
        url: '/oillife/enginebar',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function enginePie(data) {
    return request({
        url: '/oillife/enginepie',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function changeHistory(data) {
    return request({
        url: '/oillife/changehistory',
        method: 'get',
        params: {
            'deviceNo': data
        }
    })
}

export function changeOil(data) {
    return request({
        url: '/oillife/changeoil',
        method: 'post',
        data
    })
}