import React from 'react'
import HTTP from '/HTTP.js'

export default class MenuFiles extends React.Component {

    componentWillMount() {
        HTTP.get('/api/files/' + this.props.name, (e, response) => {
            if (e) console.log('Could not load data')
            else this.setState({ values: response })
        })
    }

    render() {
        this.state = this.state || { values: [] }
        const files = this.state.values.map(file => {
            const filename = file.replace('.json', '')
            const link = React.DOM.a({ href: '/edit/' + this.props.name + '/' + filename }, 'Edit ' + filename)
            return React.DOM.li({ key: file }, link)
        })
        return React.DOM.ul({}, files)
    }

}
