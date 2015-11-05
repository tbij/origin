System.transpiler = 'babel'

System.config({
    map: {
        page: '//cdnjs.cloudflare.com/ajax/libs/page.js/1.6.4/page.min.js',
        react: '//cdnjs.cloudflare.com/ajax/libs/react/0.14.2/react.min.js'
    }
})

System.import('/Routes.js')
