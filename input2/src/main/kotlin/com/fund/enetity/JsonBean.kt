package com.fund.enetity

import com.alibaba.fastjson.annotation.JSONField

 class JsonBean {
     @JSONField(name = "State")
     var state: Int? = null
     @JSONField(name = "Msg")
     var msg: String? = null
     @JSONField(name = "Code")
     var code: String? = null
     @JSONField(name = "Cmd")
     var cmd: String? = null
 }
