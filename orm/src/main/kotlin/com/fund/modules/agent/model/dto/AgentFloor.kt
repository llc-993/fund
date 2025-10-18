package com.fund.modules.agent.model.dto

import cn.hutool.core.collection.CollUtil
import com.fund.modules.agent.model.AppAgentRelation
import java.util.*
import java.util.stream.Collectors

class AgentFloor(val floor:Int, val agent: AppAgentRelation) {

    companion object {
        fun findAgentFloors(pid: Long, agentList: List<AppAgentRelation>): LinkedList<AgentFloor> {
            val agentFloors = LinkedList<AgentFloor>()
            _floor(pid, 1, agentList, agentFloors)
            return agentFloors
        }

        /**
         * 获取下级
         * @param pid
         * @param floorLevel
         * @param agentList
         * @param agentFloors
         */
        private fun _floor(
            pid: Long,
            floorLevel: Int,
            agentList: List<AppAgentRelation>,
            agentFloors: MutableList<AgentFloor>
        ) {
            var floorLevel = floorLevel
            val finalFloorLevel = floorLevel
            val list = agentList
                .stream()
                .filter { a: AppAgentRelation -> a.p1Id != null }
                .filter { a: AppAgentRelation -> a.p1Id == pid }
                .map { a: AppAgentRelation? ->
                    AgentFloor(
                        finalFloorLevel,
                        a!!
                    )
                }
                .collect(Collectors.toList())
            if (CollUtil.isNotEmpty(list)) {
                agentFloors.addAll(list)
                floorLevel++
                for (f in list) {
                    _floor(f.agent.oriUserId!!, floorLevel, agentList, agentFloors)
                }
            }
        }
    }


}
