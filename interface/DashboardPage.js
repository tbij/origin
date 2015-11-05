import React from 'react'

export default class DashboardPage extends React.Component {

    render() {
        const title = React.DOM.h2({}, 'Dashboard')
        const hr = React.DOM.hr({})
        const text = React.DOM.p({}, 'Welcome...')
        return React.DOM.div({ className: 'dashboard page' }, title, hr, text)
    }

}
