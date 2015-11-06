import React from 'react'
import HTTP from '/HTTP.js'
import Publishing from '/Publishing.js'
import FileEditor from '/FileEditor.js'

export default class EditPage extends React.Component {

    constructor() {
        super()
        this.state = { publishStatus: '', hasChanged: false }
        this.isChanged = this.isChanged.bind(this)
        this.saved = this.saved.bind(this)
        this.published = this.published.bind(this)
    }

    componentWillMount() {
        this.isChanged()
    }

    isChanged() {
        HTTP.get('/api/changed/' + this.props.directory, (e, response) => {
            if (e) console.log('Could not get change state')
            else if (response.indexOf(this.props.file + '.json') >= 0) this.setState({ hasChanged: true })
            else this.setState({ hasChanged: false })
        })
    }

    saved(e) {
        if (e) this.setState({ publishStatus: 'Failed to save! Check your internet connection.' })
        else {
            const status = 'All changes saved.'
            this.setState({ publishStatus: status })
            setTimeout(() => { if (this.state.publishStatus === status) this.setState({ publishStatus: '' }) }, 10 * 1000)
            this.isChanged()
        }
    }

    published(e) {
        if (e) this.setState({ publishStatus: 'Failed to publish! Check your internet connection.' })
        else {
            const status = 'Published.'
            this.setState({ publishStatus: status })
            setTimeout(() => { if (this.state.publishStatus === status) this.setState({ publishStatus: '' }) }, 10 * 1000)
            this.isChanged()
        }
    }

    render() {
        const publishing = React.createElement(Publishing, {
            status: this.state.publishStatus,
            directory: this.props.directory,
            file: this.props.file + '.json',
            hasChanged: this.state.hasChanged,
            onPublished: this.published
        })
        const title = React.DOM.h2({}, 'Edit ' + this.props.file)
        const hr = React.DOM.hr({})
        const editor = React.createElement(FileEditor, { location: this.props.directory + '/' + this.props.file + '.json', onChange: this.saved })
        return React.DOM.div({ className: 'edit page' }, publishing, title, hr, editor)
    }

}
