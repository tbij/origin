const HTTP = {
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

class MenuFiles extends React.Component {
    componentWillMount() {
        HTTP.get('http://localhost:8000/api/' + this.props.name, (e, response) => {
            if (e) console.log('Could not load data')
            else this.setState({ values: response })
        })
    }
    render() {
        this.state = this.state || { values: [] }
        const files = this.state.values.map(file => {
            const filename = file.replace('.json', '')
            const link = React.DOM.a({ href: 'edit/' + this.props.name + '/' + filename }, 'Edit ' + filename)
            return React.DOM.li({ key: file }, link)
        })
        return React.DOM.ul({}, files)
    }
}

class Publishing extends React.Component {
    constructor() {
        super()
        this.publish = this.publish.bind(this)
    }
    publish() {
        HTTP.post('/api/' + this.props.location, null, this.props.onPublish)
    }
    render() {
        const statusMessage = React.DOM.p({}, this.props.status)
        const publishButton = React.DOM.button({ onClick: this.publish }, 'Publish')
        return React.DOM.div({ className: 'publishing' }, statusMessage, publishButton)
    }
}

class Menu extends React.Component {
    render() {
        const titleLink = React.DOM.a({ href: '/' }, 'Origin')
        const title = React.DOM.h1({}, titleLink)
        const dataFiles = React.createElement(MenuFiles, { name: 'data' })
        return React.DOM.nav({}, title, dataFiles)
    }
}

class DashboardPage extends React.Component {
    render() {
        const title = React.DOM.h2({}, 'Dashboard')
        const hr = React.DOM.hr({})
        const text = React.DOM.p({}, 'Welcome...')
        return React.DOM.div({ className: 'dashboard page' }, title, hr, text)
    }
}

class FileEditor extends React.Component {
    componentWillMount() {
        HTTP.get('/api/' + this.props.location, (e, response) => {
            if (!e) this.setState(response)
        })
    }
    change(key) {
        return event => {
            const state = this.update(key, event.target.value)
            HTTP.put('/api/' + this.props.location, state, this.props.onChange)
            return this.setState(state)
        }
    }
    update(key, value, object = this.state) {
        if (key.indexOf('.') >= 0) {
            const thisKey = key.split('.')[0]
            const nextKey = key.split('.').slice(1).join('.')
            object[thisKey] = this.update(nextKey, value, object[thisKey])
            return object
        }
        else return Object.assign({}, object, { [key]: value })
    }
    fromObject(object, path = '') {
        return Object.keys(object).map(key => {
            if (object[key] instanceof Array) return [
                React.DOM.h3({ key: key + '-title' }, key),
                React.DOM.ol({ key: key }, this.fromObject(object[key], path + '.' + key))
            ]
            else if (object[key] instanceof Object && object instanceof Array) return [
                React.DOM.li({ key: key }, this.fromObject(object[key], path + '.' + key))
            ]
            else if (object[key] instanceof Object) return [
                React.DOM.h3({ key: key + '-title' }, key),
                React.DOM.div({ key: key }, this.fromObject(object[key], path + '.' + key))
            ]
            else return [
                React.DOM.label({ key: key + '-label' }, key, React.DOM.input({ key: key, value: object[key], onChange: this.change((path + '.' + key).substr(1)) }))
            ]
        })
    }
    render() {
        this.state = this.state || {}
        return React.DOM.div({ className: 'editor' }, this.fromObject(this.state))
    }
}

class EditPage extends React.Component {
    constructor() {
        super()
        this.state = { publishStatus: '' }
        this.saved = this.saved.bind(this)
        this.published = this.published.bind(this)
    }
    saved(e) {
        if (e) this.setState({ publishStatus: 'Failed to save! Check your internet connection.' })
        else this.setState({ publishStatus: 'All changes saved.' })
    }
    published(e) {
        if (e) this.setState({ publishStatus: 'Failed to publish! Check your internet connection.' })
        else this.setState({ publishStatus: 'Published.' })
    }
    render() {
        const publishing = React.createElement(Publishing, { status: this.state.publishStatus, location: this.props.directory + '/' + this.props.file + '.json', onPublish: this.published })
        const title = React.DOM.h2({}, 'Edit ' + this.props.file.replace('.json', ''))
        const hr = React.DOM.hr({})
        const editor = React.createElement(FileEditor, { location: this.props.directory + '/' + this.props.file + '.json', onChange: this.saved })
        return React.DOM.div({ className: 'edit page' }, publishing, title, hr, editor)
    }
}

function routes() {
    const menu = React.createElement(Menu, {})
    page('/', context => {
        const page = React.createElement(DashboardPage, {})
        React.render(React.DOM.div({}, menu, page), document.body)
    })
    page('/edit/:directory/:file', context => {
        const page = React.createElement(EditPage, context.params)
        React.render(React.DOM.div({}, menu, page), document.body)
    })
    page()
}

addEventListener('DOMContentLoaded', routes())