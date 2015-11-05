import React from 'react'
import HTTP from '/HTTP.js'

export default class FileEditor extends React.Component {

    constructor() {
        super()
        this.state = { data: {}, changing: -1 }
    }

    componentWillMount() {
        HTTP.get('/api/files/' + this.props.location, (e, response) => {
            if (!e) this.setState({ data: response })
        })
    }

    change(key) {
        return event => {
            const data = this.update(key.slice(1), (object, key) => {
                return Object.assign({}, object, { [key]: event.target.value })
            })
            clearTimeout(this.state.changing)
            const timeout = setTimeout(() => { HTTP.put('/api/files/' + this.props.location, data, this.props.onChange) }, 500)
            this.setState({ data: data, changing: timeout })
        }
    }

    up(key) {
        return () => {
            const data = this.update(key.slice(1), (array, key) => {
                key = Number(key)
                if (key === 0) return array
                const temp = array[key - 1]
                array[key - 1] = array[key]
                array[key] = temp
                return array
            })
            HTTP.put('/api/files/' + this.props.location, data, this.props.onChange)
            this.setState({ data: data })
        }
    }

    down(key) {
        return () => {
            const data = this.update(key.slice(1), (array, key) => {
                key = Number(key)
                if (key === array.length) return array
                const temp = array[key + 1]
                array[key + 1] = array[key]
                array[key] = temp
                return array
            })
            HTTP.put('/api/files/' + this.props.location, data, this.props.onChange)
            this.setState({ data: data })
        }
    }

    add(key) {
        return () => {
            const data = this.update(key.slice(1), (array, key) => {
                const element = Object.assign({}, array[key][0]) // a clone
                Object.keys(element).forEach(key => element[key] = '')
                array[key].unshift(element)
                return array
            })
            HTTP.put('/api/files/' + this.props.location, data, this.props.onChange)
            this.setState({ data: data })
        }
    }

    remove(key) {
        return () => {
            const data = this.update(key.slice(1), (array, key) => {
                array.splice(key, 1)
                return array
            })
            HTTP.put('/api/files/' + this.props.location, data, this.props.onChange)
            this.setState({ data: data })
        }
    }

    update(key, fn, object = this.state.data) {
        if (key.indexOf('.') >= 0) {
            const thisKey = key.split('.')[0]
            const nextKey = key.split('.').slice(1).join('.')
            object[thisKey] = this.update(nextKey, fn, object[thisKey])
            return object
        }
        else return fn(object, key)
    }

    fromObject(object, path = '') {
        return Object.keys(object).map(key => {
            if (object[key] instanceof Array) return [
                React.DOM.button({ key: key + '-add', className: 'add', onClick: this.add(path + '.' + key) }, '+'),
                React.DOM.h3({ key: key + '-title' }, key),
                React.DOM.ol({ key: key }, this.fromObject(object[key], path + '.' + key))
            ]
            else if (object[key] instanceof Object && object instanceof Array) {
                const buttons = [
                    React.DOM.button({ onClick: this.up(path + '.' + key) }, '↑'),
                    React.DOM.button({ onClick: this.down(path + '.' + key) }, '↓'),
                    React.DOM.button({ onClick: this.remove(path + '.' + key) }, '×')
                ]
                return React.DOM.li({ key: key }, buttons, this.fromObject(object[key], path + '.' + key))
            }
            else if (object[key] instanceof Object) return [
                React.DOM.h3({ key: key + '-title' }, key),
                React.DOM.div({ key: key }, this.fromObject(object[key], path + '.' + key))
            ]
            else return [
                React.DOM.label({ key: key + '-label' }, key, React.DOM.input({ key: key, value: object[key], onChange: this.change(path + '.' + key) }))
            ]
        })
    }

    render() {
        return React.DOM.div({ className: 'editor' }, this.fromObject(this.state.data))
    }

}
