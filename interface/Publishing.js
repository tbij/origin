import React from 'react'
import HTTP from '/HTTP.js'
import Dialog from '/Dialog.js'

export default class Publishing extends React.Component {

    constructor() {
        super()
        this.publish = this.publish.bind(this)
        this.state = { confirming: null }
    }

    publish(confirm) {
        return () => {
            if (confirm) {
                HTTP.post('/api/files/' + this.props.directory + '/' + this.props.file, null, this.props.onPublished)
                this.setState({ confirming: null })
            }
            else if (confirm === false) this.setState({ confirming: null })
            else if (confirm === undefined) this.setState({ confirming: true })
        }
    }

    render() {
        const statusMessage = React.DOM.p({}, this.props.status)
        const publishButton = React.DOM.button({ onClick: this.publish(), disabled: !this.props.hasChanged }, 'Publish')
        const publishing = React.DOM.div({ className: 'publishing' }, statusMessage, publishButton)
        const dialog = React.createElement(Dialog, {
            text: 'Are you sure you want to publish?',
            acceptText: 'Publish',
            accept: this.publish(true),
            cancel: this.publish(false)
        })
        if (this.state.confirming) return React.DOM.div({}, publishing, dialog)
        else return React.DOM.div({}, publishing)
    }

}
