package com.fund.common.dto

class Label<V, L> {
    constructor()
    constructor(value:V, label: L) {
        this.value = value
        this.label = label
    }


    var value: V? = null


    var label: L? = null
}