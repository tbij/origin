import React from 'react'

export default class Publishing extends React.Component {

    constructor() {
        super()
        this.publish = this.publish.bind(this)
    }

    publish() {
        HTTP.post('/api/files/' + this.props.directory + '/' + this.props.file, null, this.props.onPublished)
    }

    render() {
        const statusMessage = React.DOM.p({}, this.props.status)
        const publishButton = React.DOM.button({ onClick: this.publish, disabled: !this.props.hasChanged }, 'Publish')
        return React.DOM.div({ className: 'publishing' }, statusMessage, publishButton)
    }

}
