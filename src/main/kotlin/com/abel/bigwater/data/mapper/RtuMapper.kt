package com.abel.bigwater.data.mapper

import com.abel.bigmeter.service.DataParam
import com.abel.bigmeter.service.MeterParam
import com.abel.bigwater.model.BwRtu
import com.abel.bigwater.model.BwRtuLog
import org.apache.ibatis.annotations.*

@Mapper
interface RtuMapper {

    /**
     * <pre>
     * <parameterMap type="map" id="rtuLogMap">
     * <parameter property="logTime1"></parameter>
     * <parameter property="logTime2"></parameter>
     * <parameter property="rtuId"></parameter>
     * <parameter property="meterId"></parameter>
     * <parameter property="index"></parameter>
     * <parameter property="rows"></parameter>
     * <parameter property="firmId"></parameter>
    </parameterMap> *
    </pre> *
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT `log_id` AS logId,
	    `log_time` AS logTime,
	    `log_cmd` AS logCmd,
	    `log_len` AS logLen,
	    r.`rtu_id` AS rtuId,
	    r.`meter_id` AS meterId,
	    `log_text` AS logText,
	    `log_resp` AS logResp,
	    `log_comment` AS logComment,

        r.v_all AS forwardReading,
        r.v_revert AS revertReading,
        r.v_pulse AS literPulse,

	    rssi AS rssi,
        reverse_warn AS reverseWarn,
        max_warn AS maxWarn,
        cut_warn AS cutWarn,
        volt_warn AS voltWarn,
        rssi_warn AS rssiWarn,
        r.remote_server AS remoteServer

        , m.firm_id AS firmId

	    FROM `bw_rtu_log` r LEFT JOIN bw_meter m ON r.meter_id = m.meter_id
	    <where>
	        <if test="logTime1 != null">
	            r.log_time &gt;= #{logTime1}
	        </if>
	        <if test="logTime2 != null">
	            AND r.log_time &lt; #{logTime2}
	        </if>
	        <if test="rtuId != null">
	            AND r.rtu_id LIKE #{rtuId}
	        </if>
	        <if test="meterId != null">
	            AND r.meter_id LIKE #{meterId}
	        </if>
	        <if test="firmId != null">
	           AND (m.meter_id IS NULL OR m.firm_id LIKE #{firmId})
	        </if>
	        <if test="firmId != null">
	           AND m.firm_id LIKE #{firmId}
	        </if>
	    </where>
	    ORDER BY r.log_time DESC
	    LIMIT #{index}, #{rows}
        </script>""")
    fun selectRtuLog(dp: DataParam): List<BwRtuLog>

    @Insert("""<script>
        INSERT INTO `bw_rtu_log`
		(
		`log_time`,
		`log_cmd`,
		`log_len`,
		`rtu_id`,
		`meter_id`,
		`log_text`,
		`log_resp`,
		`log_comment`,

        v_all,
        v_revert,
        v_pulse,

	    rssi ,
        reverse_warn ,
        max_warn ,
        cut_warn ,
        volt_warn ,
        rssi_warn,
        remote_server

        , firm_id
		)
		VALUES
		(
		#{logTime},
		#{logCmd},
		#{logLen},
		#{rtuId},
		#{meterId},
		#{logText},
		#{logResp},
		#{logComment},

        #{forwardReading},
        #{revertReading},
        #{literPulse},

        #{rssi},
        #{reverseWarn},
        #{maxWarn},
        #{cutWarn},
        #{voltWarn},
        #{rssiWarn},
        #{remoteServer}

        , #{firmId}
		)
        </script>""")
    @Options(useGeneratedKeys = true, keyColumn = "log_id", keyProperty = "logId")
    fun addRtuLog(rlog: BwRtuLog): Int

    @Delete("""<script>
        DELETE FROM bw_rtu_log
        <where>
            <if test="logTime1 != null">
                log_time &gt;= #{logTime1}
            </if>
            <if test="logTime2 != null">
                AND log_time &lt; #{logTime2}
            </if>
            <if test="rtuId != null">
                AND rtu_id LIKE #{rtuId}
            </if>
            <if test="meterId != null">
                AND meter_id LIKE #{meterId}
            </if>
            <if test="firmId != null">
                AND firm_id LIKE #{firmId}
            </if>
        </where>
        </script>""")
    fun deleteRtuLog(dp: DataParam): Int

    @Select("""<script>
        SELECT r.`rtu_id` AS rtuId,
		    r.`meter_id` AS meterId,
            m.meter_name AS meterName,
		    m.meter_location AS meterAddr,
		    r.`last_time` AS lastTime,
		    r.`last_cmd` AS lastCmd,
		    r.last_data_time AS lastDataTime,
		    r.state_desc AS stateDesc,
		    r.`last_text` AS lastText,
		    r.`last_resp` AS lastResp,
		    r.first_time AS firstTime,
		    r.first_cmd AS firstCmd,
		    r.first_text AS firstText,
		    r.first_resp AS firstResp,
		    ST_AsText(r.rtu_loc) AS rtuLoc,

	        r.v_all AS forwardReading,
	        r.v_revert AS revertReading,
	        r.v_pulse AS literPulse,

            r.rssi AS rssi,
            r.reverse_warn AS reverseWarn,
            r.max_warn AS maxWarn,
            r.cut_warn AS cutWarn,
            r.volt_warn AS voltWarn,
            r.rssi_warn AS rssiWarn,
            r.remote_server AS remoteServer

            , m.firm_id AS firmId

		FROM `bw_rtu` r LEFT JOIN bw_meter m
		ON r.meter_id = m.ext_id
		<where>
		  <if test="rtuId != null">
		      r.rtu_id LIKE #{rtuId}
		  </if>
          <if test="meterId != null">
              AND r.meter_id LIKE #{meterId}
          </if>
          <if test="firmId != null">
            AND (m.meter_id IS NULL OR m.firm_id LIKE #{firmId})
          </if>
          <if test="firmId != null">
            AND m.firm_id LIKE #{firmId}
          </if>
		</where>
        ORDER BY r.rtu_id
        </script>""")
    fun selectRtuList(mp: MeterParam): List<BwRtu>

    @Insert("""<script>
        INSERT INTO `bw_rtu`
		(`rtu_id`,
		`meter_id`,
		`last_time`,
		`last_cmd`,
		`last_text`,
		`last_resp`,
		first_time,
		first_cmd,
		first_text,
		first_resp,
		rtu_loc,
		meter_name,

        v_all,
        v_revert,
        v_pulse,

        last_data_time,

        rssi ,
        reverse_warn ,
        max_warn ,
        cut_warn ,
        volt_warn ,
        rssi_warn,
        remote_server

        , firm_id
		)
		VALUES
		(#{rtuId},
		#{meterId},
		#{lastTime},
		#{lastCmd},
		#{lastText},
		#{lastResp},
		#{firstTime},
		#{firstCmd},
		#{firstText},
		#{firstResp},
		ST_GeomFromText(#{rtuLoc}),
		#{meterName},

        #{forwardReading},
        #{revertReading},
        #{literPulse},

        #{lastDataTime},

        #{rssi},
        #{reverseWarn},
        #{maxWarn},
        #{cutWarn},
        #{voltWarn},
        #{rssiWarn},
        #{remoteServer}

        , #{firmId}
		);
        </script>""")
    fun addRtu(rtu: BwRtu): Int

    @Update("""<script>
        UPDATE bw_rtu
        <set>
			`last_time` = #{lastTime},
			`last_cmd` = #{lastCmd},
			`last_text` = #{lastText},
			`last_resp` = #{lastResp},

			<if test="lastDataTime != null">
			  last_data_time = #{lastDataTime},
			</if>

			state_desc = #{stateDesc},
			meter_name = #{meterName},

	        <if test="forwardReading != null">
	            v_all = #{forwardReading},
	        </if>

	        <if test="revertReading != null">
	            v_revert = #{revertReading},
	        </if>

	        v_pulse = #{literPulse},

			rssi = #{rssi},
			reverse_warn = #{reverseWarn},
			max_warn = #{maxWarn},
			cut_warn = #{cutWarn},
			volt_warn = #{voltWarn},
			rssi_warn = #{rssiWarn},

	        remote_server = #{remoteServer}

	        <if test="firmId != null">
                , firm_id = #{firmId}
	        </if>
		</set>

		WHERE `rtu_id` = #{rtuId}
		AND `meter_id` = #{meterId}
        </script>""")
    fun updateRtu(rtu: BwRtu): Int

    @Delete("""<script>
        DELETE FROM bw_rtu
        <where>
          <if test="rtuId != null">
              rtu_id LIKE #{rtuId}
          </if>
          <if test="meterId != null">
              AND meter_id LIKE #{meterId}
          </if>
          <if test="firmId != null">
            AND firm_id LIKE #{firmId}
          </if>
        </where>
        </script>""")
    fun deleteRtu(mp: MeterParam): Int

}