imp = {
    $s.imports << 'import '+(it instanceof Class?it.name : it)
    null
}

imps = {
    $s.imports << 'import static '+(it.hasProperty('name')?it['name']:it)
    null
}

$c.colors['*'] = [
    '*':[
        background:'white',
        foreground:'black',
        caretColor:'black'
    ]
]
