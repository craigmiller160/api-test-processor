package io.craigmiller160.apitestprocessor.body

class Form<K,V> : HashMap<K,V>()

fun <K,V> formOf(vararg pairs: Pair<K,V>): Form<K,V> = pairs.toMap(Form())
