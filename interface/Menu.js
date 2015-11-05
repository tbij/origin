import React from 'react'
import MenuFiles from '/MenuFiles.js'

export default class Menu extends React.Component {

    render() {
        const titleLink = React.DOM.a({ href: '/' }, 'Origin')
        const title = React.DOM.h1({}, titleLink)
        const previewLink = React.DOM.a({ href: '/preview/', target: '_blank' }, 'Open site preview')
        const hr = React.DOM.hr({})
        const dataFiles = React.createElement(MenuFiles, { name: 'data' })
        return React.DOM.nav({}, title, previewLink, hr, dataFiles)
    }

}
