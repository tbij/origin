import React from 'react'
import ReactDOM from 'react-dom'
import Page from 'page'
import Menu from '/Menu.js'
import DashboardPage from '/DashboardPage.js'
import EditPage from '/EditPage.js'

export default class Routes {

    static run() {
	const menu = React.createElement(Menu, {})
	Page('/', context => {
            const page = React.createElement(DashboardPage, {})
            ReactDOM.render(React.DOM.div({}, menu, page), document.body)
	})
	Page('/edit/:directory/:file', context => {
            const page = React.createElement(EditPage, context.params)
            ReactDOM.render(React.DOM.div({}, menu, page), document.body)
	})
	Page()
    }

}

Routes.run()
