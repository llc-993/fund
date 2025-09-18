package com.fund.enetity

import com.alibaba.fastjson.annotation.JSONField

 class JsonBean<T> {
     @JSONField(name = "State")
     var state: Int? = null
     @JSONField(name = "Msg")
     var msg: T? = null
     @JSONField(name = "Code")
     var code: String? = null
     @JSONField(name = "Cmd")
     var cmd: String? = null
 }
