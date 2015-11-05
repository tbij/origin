export default {
    get(location, callback) {
        const request = new XMLHttpRequest()
        request.open('GET', location)
        request.addEventListener('load', event => {
            if (request.status < 400) callback(null, JSON.parse(request.responseText))
            else callback(new Error(request.response), null)
        })
        request.send()
    },
    post(location, data, callback) {
        const request = new XMLHttpRequest()
        request.open('POST', location)
        request.setRequestHeader('Authorization', 'Bearer ' + document.cookie.match(/token=(.*?)(;|$)/)[1])
        request.addEventListener('load', event => {
            if (request.status < 400) callback(null)
            else callback(new Error(request.response))
        })
        request.send(JSON.stringify(data, null, 4) + '\n')
    },
    put(location, data, callback) {
        const request = new XMLHttpRequest()
        request.open('PUT', location)
        request.setRequestHeader('Authorization', 'Bearer ' + document.cookie.match(/token=(.*?)(;|$)/)[1])
        request.addEventListener('load', event => {
            if (request.status < 400) callback(null)
            else callback(new Error(request.response))
        })
        request.send(JSON.stringify(data, null, 4) + '\n')
    }
}
