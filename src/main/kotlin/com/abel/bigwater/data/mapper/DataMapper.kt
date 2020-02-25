package com.abel.bigwater.data.mapper

import com.abel.bigmeter.service.DataParam
import com.abel.bigwater.model.BwData
import com.abel.bigwater.model.DataRange
import org.apache.ibatis.annotations.*

@Mapper
//@Component(value = "dataMapper")
interface DataMapper {

    @Insert("""
        insert into bw_realtime_data (meter_id, sample_time, end_time, duration_second,
        water_v1, avg_flow,
        pressure1, pressure_reading,
        v_q0, v_q1, v_q2, v_q3, v_q4,
        revert_v1,

        v_all, base_all,
        v_revert,
        v_pulse,

        firm_id)

        values(#{extId}, #{sampleTime}, #{endTime}, #{durationSecond},
        #{forwardSum}, #{avgFlow},
        #{pressure}, #{pressureDigits},
        #{q0Sum}, #{q1Sum}, #{q2Sum}, #{q3Sum}, #{q4Sum},
        #{revertSum},

        #{forwardDigits}, #{baseDigits},
        #{revertDigits},
        #{literPulse},

        #{firmId})
        """)
    fun insertRealtime(data: BwData): Int

    @Update("""
        UPDATE bw_realtime_data
        SET
        end_time = #{endTime},
		duration_second = #{durationSecond},
		water_v1 = #{forwardSum},
        avg_flow = #{avgFlow},
		pressure1 = #{pressure},
		pressure_reading = #{pressureDigits},
		v_q0 = #{q0Sum}, v_q1 = #{q1Sum}, v_q2 = #{q2Sum}, v_q3 = #{q3Sum}, v_q4 = #{q4Sum},
		revert_v1 = #{revertSum},

		v_all = #{forwardDigits}, base_all = #{baseDigits},
		v_revert = #{revertDigits},
		v_pulse = #{literPulse},

		firm_id =  #{firmId}
		WHERE
		meter_id = #{extId}
		AND sample_time = #{sampleTime}
        """)
    fun updateRealtime(data: BwData): Int

    @Update("""
        <script>
        UPDATE bw_realtime_data
        <set>
            <if test="endTime != null">
                end_time = #{endTime},
            </if>
            <if test="durationSecond != null">
                duration_second = #{durationSecond},
            </if>
            <if test="forwardSum != null">
                water_v1 = #{forwardSum},
            </if>
            <if test="avgFlow != null">
                avg_flow = #{avgFlow},
            </if>
            <if test="pressure != null">
                pressure1 = #{pressure},
            </if>
            <if test="pressureDigits != null">
                pressure_reading = #{pressureDigits},
            </if>
            <if test="q0Sum != null">
                v_q0 = #{q0Sum},
            </if>
            <if test="q1Sum != null">
                v_q1 = #{q1Sum},
            </if>
            <if test="q2Sum != null">
                v_q2 = #{q2Sum},
            </if>
            <if test="q3Sum != null">
                v_q3 = #{q3Sum},
            </if>
            <if test="q4Sum != null">
                v_q4 = #{q4Sum},
            </if>
            <if test="revertSum != null">
                revert_v1 = #{revertSum},
            </if>
            <if test="forwardReading != null">
                v_all = #{forwardReading},
            </if>
            <if test="baseDigits != null">
                base_all = #{baseDigits},
            </if>
            <if test="revertSum != null">
                v_revert = #{revertSum},
            </if>
            <if test="literPulse != null">
                v_pulse = #{literPulse},
            </if>
            <if test="firmId != null">
                firm_id =  #{firmId}
            </if>
        </set>
        WHERE
        meter_id = #{extId}
        AND sample_time = #{sampleTime}
        </script>
    """)
    fun updateRealtimeByValue(data: BwData): Int

    @Update("""
        <script>
        UPDATE bw_realtime_data
        <set>
            <if test="pressure != null">
                pressure1 = #{pressure},
            </if>
            <if test="pressureDigits != null">
                pressure_reading = #{pressureDigits},
            </if>
            <if test="firmId != null">
                firm_id =  #{firmId}
            </if>
        </set>
        WHERE
        meter_id = #{extId}
        AND sample_time = #{sampleTime}
        </script>
    """)
    fun updatePressureByValue(data: BwData): Int

    /**
     * ORDER BY sample_time [ASC].
     *
     * <pre>
     * <parameterMap type="map" id="realtimeMap">
     * <parameter property="meterId"></parameter>
     * <parameter property="sampleTime1"></parameter>
     * <parameter property="sampleTime2"></parameter>
     * <parameter property="dmaId"></parameter>
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
        SELECT
        m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        r.sample_time as sampleTime, r.end_time as endTime,
        r.duration_second as durationSecond,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressureDigits,
        r.pressure_reading AS pressure,

        r.v_all AS forwardDigits, r.base_all AS baseDigits,
        r.v_revert AS revertDigits,
        r.v_pulse AS literPulse,

        r.v_q0 as q0Sum, r.v_q1 as q1Sum, r.v_q2 as q2Sum, r.v_q3 as q3Sum, r.v_q4 as q4Sum,
        r.revert_v1 as revertSum,
        r.firm_id AS firmId,
        r.flapped

        FROM (
            SELECT * FROM bw_realtime_data
	        <where>
	            <if test="extId != null">
	                meter_id LIKE #{extId}
	            </if>
	            <if test="extIdList != null">
	                AND meter_id IN
	                <foreach collection="extIdList" item="eid" open="(" close=")" separator=",">
	                    #{eid}
	                </foreach>
	            </if>
	            <if test="sampleTime1 != null">
	                AND sample_time &gt;= #{sampleTime1}
	            </if>
	            <if test="sampleTime2 != null">
	                AND sample_time &lt; #{sampleTime2}
	            </if>
	        </where>
        ) r JOIN bw_meter m ON r.meter_id = m.ext_id
        LEFT JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>
            <if test="meterIdList != null">
                AND m.meter_id IN
                <foreach collection="meterIdList" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>

            <if test="dmaIdList != null">
                AND m.dma_id IN
                <foreach collection="dmaIdList" item="did" open="(" close=")" separator=",">
                    #{did}
                </foreach>
            </if>
            <if test="dmaId != null">
                AND m.dma_id LIKE #{dmaId}
            </if>

            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        ORDER BY m.meter_id, r.meter_id, r.sample_time
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectRealtime(dp: DataParam): List<BwData>

    /**
     * 选取水表的最后多行数据
     */
    @Select("""<script>
        SELECT
        r.meter_id as extId,
        m.meter_id AS meterId,
        m.meter_name AS meterName,
        r.sample_time as sampleTime, r.end_time as endTime,
        r.duration_second as durationSecond,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressureDigits,
        r.pressure_reading AS pressure,

        r.v_all AS forwardDigits, r.base_all AS baseDigits,
        r.v_revert AS revertDigits,
        r.v_pulse AS literPulse,

        r.v_q0 as q0Sum, r.v_q1 as q1Sum, r.v_q2 as q2Sum, r.v_q3 as q3Sum, r.v_q4 as q4Sum,
        r.revert_v1 as revertSum,
        r.firm_id AS firmId,
        r.flapped

        FROM bw_realtime_data r
        JOIN (
        SELECT * FROM bw_meter
        <where>
            <if test="meterId != null">
                meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND ext_id LIKE #{extId}
            </if>
            <if test="meterIdList != null">
                AND meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="extIdList != null">
                AND ext_id IN
                <foreach collection="extIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
        </where>
        ) m ON r.meter_id = m.ext_id
        <where>
            1 = 1
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
        </where>
        ORDER BY r.meter_id, r.sample_time
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectMeterRealtime(dp: DataParam): List<BwData>

    /**
     * 选取水表的最后多行数据
     */
    @Select("""<script>
        SELECT
        r.meter_id as extId,
        m.meter_id AS meterId,
        m.meter_name AS meterName,
        r.sample_time as sampleTime, r.end_time as endTime,
        r.duration_second as durationSecond,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressureDigits,
        r.pressure_reading AS pressure,

        r.v_all AS forwardDigits, r.base_all AS baseDigits,
        r.v_revert AS revertDigits,
        r.v_pulse AS literPulse,

        r.v_q0 as q0Sum, r.v_q1 as q1Sum, r.v_q2 as q2Sum, r.v_q3 as q3Sum, r.v_q4 as q4Sum,
        r.revert_v1 as revertSum,
        r.firm_id AS firmId,
        r.flapped

        FROM bw_realtime_data r JOIN (
        SELECT * FROM bw_meter
        <where>
            <if test="meterId != null">
                meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                ext_id LIKE #{extId}
            </if>
            <if test="meterIdList != null">
                AND meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="extIdList != null">
                AND ext_id IN
                <foreach collection="extIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
        </where>
        ) m ON r.meter_id = m.ext_id
        <where>
            1 = 1
            <if test="sampleTime1 != null">
                AND sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND sample_time &lt; #{sampleTime2}
            </if>
        </where>
        ORDER BY r.meter_id, r.sample_time DESC
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectMeterRealtimeReverse(dp: DataParam): List<BwData>

    /**
     * select last 1000 rows from bw_realtime_data and join with other tables.
     *
     * @param map
     * @return
     */
    @Select("""<script>
        select
        m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        r.sample_time as sampleTime, r.end_time as endTime,
        r.duration_second as durationSecond,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressureDigits,
        r.pressure_reading AS pressure,

        r.v_all AS forwardDigits,  r.base_all AS baseDigits,
        r.v_revert AS revertDigits,
        r.v_pulse AS literPulse,

        r.v_q0 AS q0Sum,
        r.v_q1 AS q1Sum,
        r.v_q2 AS q2Sum,
        r.v_q3 AS q3Sum,
        r.v_q4 AS q4Sum,
        r.revert_v1 as revertSum,
        r.firm_id AS firmId,
        r.flapped

        FROM
        (SELECT * FROM bw_realtime_data r
        <where>
            <if test="extId != null">
                r.meter_id like #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
        </where>
        ORDER BY sample_time DESC
        LIMIT 0,1000) r
        JOIN bw_meter m ON r.meter_id = m.ext_id
        LEFT JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            <if test="meterId != null">
                m.meter_id like #{meterId}
            </if>
            <if test="dmaId != null">
                AND m.dma_id LIKE #{dmaId}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        ORDER BY r.sample_time DESC, m.meter_id, r.meter_id
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectRealtimeLast(map: Map<String, Any>): List<BwData>

    /**
     * This method will retrieve realtime data hourly.
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT m.meter_id AS meterId,
        r.meter_id AS extId,
        m.meter_name AS meterName,
        m.meter_location AS location,
        DATE_ADD(DATE(r.sample_time), INTERVAL HOUR(r.sample_time) HOUR) AS sampleTime,
        SUM(r.duration_second) AS durationSecond,
        SUM(r.water_v1) AS forwardSum,
        AVG(r.pressure1) AS pressureDigits,
        AVG(r.pressure_reading) AS pressure,

        MAX(r.v_all) AS forwardDigits, MAX(r.base_all) AS baseDigits,
        MAX(r.v_revert) AS revertDigits,
        r.v_pulse AS literPulse,

        SUM(r.v_q0) AS q0Sum,
        SUM(r.v_q1) AS q1Sum,
        SUM(r.v_q2) AS q2Sum,
        SUM(r.v_q3) AS q3Sum,
        SUM(r.v_q4) AS q4Sum,
        SUM(r.revert_v1) AS revertSum,
        r.firm_id AS firmId

        FROM (
            SELECT * FROM bw_realtime_data
	        <where>
	            <if test="extId != null">
	                meter_id LIKE #{extId}
	            </if>

	            <if test="extIdList != null">
	                AND meter_id IN
	                <foreach collection="extIdList" item="eid" open="(" close=")" separator=",">
	                    #{eid}
	                </foreach>
	            </if>

	            <if test="sampleTime1 != null">
	                AND sample_time &gt;= #{sampleTime1}
	            </if>
	            <if test="sampleTime2 != null">
	                AND sample_time &lt; #{sampleTime2}
	            </if>
	        </where>
        ) r JOIN bw_meter m ON r.meter_id = m.ext_id
        LEFT JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>

            <if test="meterIdList != null">
                AND m.meter_id IN
                <foreach collection="meterIdList" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>

            <if test="dmaIdList != null">
                AND m.dma_id IN
                <foreach collection="dmaIdList" item="did" open="(" close=")" separator=",">
                    #{did}
                </foreach>
            </if>

            <if test="dmaId != null">
                AND m.dma_id LIKE #{dmaId}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        GROUP BY m.meter_id, r.meter_id, sampleTime
        ORDER BY m.meter_id, r.meter_id, sampleTime DESC
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectRealtimeHourly(mp: DataParam): List<BwData>

    /**
     * This method querys data list for specified zone.
     * order by meterId, extId, sampleTime (all ASC)
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        DATE_ADD(DATE(r.sample_time), INTERVAL HOUR(r.sample_time) HOUR) AS sampleTime,
        SUM(r.duration_second) AS durationSecond,
        SUM(r.water_v1) AS forwardSum,
        AVG(r.pressure1) AS pressureDigits,
        AVG(r.pressure_reading) AS pressure,

        MIN(r.water_v1) AS waterMin,
        MAX(r.water_v1) AS waterMax,
        AVG(r.duration_second) AS durationSecondAvg,
        MIN(r.pressure1) AS pressureMin,
        MAX(r.pressure1) AS pressureMax,
        MIN(r.pressure_reading) AS pressureReadingMin,
        MAX(r.pressure_reading) AS pressureReadingMax,

        MAX(r.v_all) AS forwardDigits, MAX(r.base_all) AS baseDigits,
        MAX(r.v_revert) AS revertDigits,
        r.v_pulse AS literPulse,

        SUM(r.v_q0) AS q0Sum,
        SUM(r.v_q1) AS q1Sum,
        SUM(r.v_q2) AS q2Sum,
        SUM(r.v_q3) AS q3Sum,
        SUM(r.v_q4) AS q4Sum,
        SUM(r.revert_v1) as revertDigits,
        r.firm_id AS firmId

        FROM bw_realtime_data r JOIN bw_meter m ON r.meter_id = m.ext_id
        JOIN dm_zone_meter zm ON zm.meter_id = m.meter_id
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>
            <if test="zoneId != null">
                AND zm.z_id LIKE #{zoneId}
            </if>
            <if test="meterIdList != null">
                AND m.meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="extId != null">
                AND r.meter_id like #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        GROUP BY m.meter_id, r.meter_id, sampleTime
        ORDER BY m.meter_id, r.meter_id, sampleTime
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectZoneRealtimeHourly(dp: DataParam): List<BwData>

    /**
     * Almost same as {[.selectRealtimeHourly] but order by 'sampleTime' firstly
     * then meterId.
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        DATE_ADD(DATE(r.sample_time), INTERVAL HOUR(r.sample_time) HOUR) AS sampleTime,
        SUM(r.duration_second) AS durationSecond,
        SUM(r.water_v1) AS forwardSum,
        AVG(r.pressure1) AS pressureDigits,
        AVG(r.pressure_reading) AS pressure,

        MAX(r.v_all) AS forwardDigits, MAX(r.base_all) AS baseDigits,
        MAX(r.v_revert) AS revertDigits,
        r.v_pulse AS literPulse,

        SUM(r.v_q0) AS q0Sum,
        SUM(r.v_q1) AS q1Sum,
        SUM(r.v_q2) AS q2Sum,
        SUM(r.v_q3) AS q3Sum,
        SUM(r.v_q4) AS q4Sum,
        SUM(r.revert_v1) as revertSum,
        r.firm_id AS firmId

        FROM (
            SELECT * FROM bw_realtime_data
            <where>
            <if test="extId != null">
                meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND sample_time &lt; #{sampleTime2}
            </if>
            </where>
        ) r JOIN bw_meter m ON r.meter_id = m.ext_id
        JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>
            <if test="dmaId != null">
                AND m.dma_id LIKE #{dmaId}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        GROUP BY m.meter_id, r.meter_id, sampleTime
        ORDER BY sampleTime, m.meter_id, r.meter_id
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectDmaRealtimeHourly(dp: DataParam): List<BwData>

    /**
     * 选取分区水表的数据，逆排序.
     * ORDER BY sample_time DESC.
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT
        m.meter_id as meterId,
        r.meter_id AS extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        r.sample_time as sampleTime, r.end_time as endTime,
        r.duration_second as durationSecond,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressureDigits,
        r.pressure_reading AS pressure,

        r.v_all AS forwardDigits, r.base_all AS baseDigits,
        r.v_revert AS revertDigits,
        r.v_pulse AS literPulse,

        r.v_q0 AS q0Sum,
        r.v_q1 AS q1Sum,
        r.v_q2 AS q2Sum,
        r.v_q3 AS q3Sum,
        r.v_q4 AS q4Sum,
        r.revert_v1 as revertSum,

        r.firm_id AS firmId,
        r.flapped

        FROM bw_realtime_data r JOIN (
        SELECT * FROM bw_meter
            <where>
            <if test="meterId != null">
                meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND ext_id LIKE #{extId}
            </if>
            <if test="meterIdList != null">
                AND meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="extIdList != null">
                AND ext_id IN
                <foreach collection="extIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="dmaId != null">
                AND dma_id LIKE #{dmaId}
            </if>
            <if test="firmId != null">
                AND firm_id LIKE #{firmId}
            </if>
            </where>
        ) m ON r.meter_id = m.ext_id
        JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            1 = 1
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
        </where>
        ORDER BY r.meter_id, r.sample_time DESC
        LIMIT #{index}, #{rows}
        </script>""")
    fun selectRealtimeReverse(dp: DataParam): List<BwData>

    @Select("""<script>
        SELECT
        m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        ST_AsText(m.meter_loc) AS meterLoc,
        r.sample_time as sampleTime,
        r.duration_second as durationSecond,
        r.v_all AS forwardDigits,
        r.v_revert AS revertDigits,
        r.base_all AS baseDigits,
        r.v_pulse AS literPulse,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressure,
        r.pressure_reading AS pressureDigits,

        r.v_q0 AS q0Sum,
        r.v_q1 AS q1Sum,
        r.v_q2 AS q2Sum,
        r.v_q3 AS q3Sum,
        r.v_q4 AS q4Sum,
        ST_AsText(m.meter_loc) AS meterLoc,
        r.firm_id AS firmId

        FROM bw_meter m LEFT JOIN bw_realtime_data r ON r.meter_id = m.ext_id
        JOIN (
            SELECT meter_id, MAX(sample_time) sample_time
            FROM bw_realtime_data
            GROUP BY meter_id
        ) sd ON r.meter_id = sd.meter_id AND r.sample_time = sd.sample_time
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND r.meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        GROUP BY
        m.meter_id,
        r.meter_id,
        m.meter_name
        ORDER BY
        m.meter_id,
        r.meter_id,
        r.sample_time
        LIMIT #{index}, #{rows}
        </script>""")
    fun scadaMeterList(dp: DataParam): List<BwData>

    @Select("""<script>
        SELECT
        m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        ST_AsText(m.meter_loc) AS meterLoc,
        r.sample_time as sampleTime,
        r.duration_second as durationSecond,
        r.v_all AS forwardDigits,
        r.v_revert AS revertDigits,
        r.base_all AS baseDigits,
        r.v_pulse AS literPulse,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressure,
        r.pressure_reading AS pressureDigits,

        r.v_q0 AS q0Sum,
        r.v_q1 AS q1Sum,
        r.v_q2 AS q2Sum,
        r.v_q3 AS q3Sum,
        r.v_q4 AS q4Sum,
        ST_AsText(m.meter_loc) AS meterLoc,
        r.firm_id AS firmId,
        zm.z_id AS zoneId

        FROM bw_meter m LEFT JOIN bw_realtime_data r ON r.meter_id = m.ext_id
        JOIN (
            SELECT meter_id, MAX(sample_time) sample_time
            FROM bw_realtime_data
            GROUP BY meter_id
        ) sd ON r.meter_id = sd.meter_id AND r.sample_time = sd.sample_time
        JOIN dm_zone_meter zm ON (zm.meter_id = m.meter_id)
        <where>
            m.firm_id LIKE #{firmId}
            AND zm.z_id LIKE #{zoneId}
            <if test="meterId != null">
                AND m.meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND r.meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
        </where>
        GROUP BY
        m.meter_id,
        r.meter_id,
        m.meter_name
        ORDER BY
        m.meter_id,
        r.meter_id,
        r.sample_time
        LIMIT #{index}, #{rows}
        </script>""")
    fun scadaMeterListZone(dp: DataParam): List<BwData>

    @Select("""<script>
        SELECT
        m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        ST_AsText(m.meter_loc) AS meterLoc,
        r.sample_time as sampleTime,
        r.duration_second as durationSecond,
        r.v_all AS forwardDigits,
        r.v_revert AS revertDigits,
        r.base_all AS baseDigits,
        r.v_pulse AS literPulse,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressure,
        r.pressure_reading AS pressureDigits,

        r.v_q0 AS q0Sum,
        r.v_q1 AS q1Sum,
        r.v_q2 AS q2Sum,
        r.v_q3 AS q3Sum,
        r.v_q4 AS q4Sum,
        ST_AsText(m.meter_loc) AS meterLoc,
        r.firm_id AS firmId

        FROM bw_meter m LEFT JOIN bw_realtime_data r ON r.meter_id = m.ext_id
        JOIN (
            SELECT meter_id, MAX(sample_time) sample_time
            FROM bw_realtime_data
            GROUP BY meter_id
        ) sd ON r.meter_id = sd.meter_id AND r.sample_time = sd.sample_time
       LEFT JOIN dm_zone_meter zm ON (zm.meter_id = m.meter_id)
        <where>
            m.firm_id LIKE #{firmId}
            AND zm.z_id IS NULL
            <if test="meterId != null">
                AND m.meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND r.meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
        </where>
        GROUP BY
        m.meter_id,
        r.meter_id,
        m.meter_name
        ORDER BY
        m.meter_id,
        r.meter_id,
        r.sample_time
        LIMIT #{index}, #{rows}
        </script>""")
    fun scadaMeterListZoneNone(dp: DataParam): List<BwData>

    @Select("""<script>
        SELECT
        m.meter_id AS meterId,
        r.meter_id as extId,
        m.meter_name AS meterName,
        m.meter_location as location,
        DATE(r.sample_time) as sampleTime,
        SUM(r.duration_second) as durationSecond,
        SUM(r.water_v1) as forwardSum,
        AVG(r.pressure1) AS pressureDigits,
        AVG(r.pressure_reading) AS pressure,
        MAX(r.v_all) AS forwardDigits, MAX(r.base_all) AS baseDigits,

        SUM(r.v_q0) AS q0Sum,
        SUM(r.v_q1) AS q1Sum,
        SUM(r.v_q2) AS q2Sum,
        SUM(r.v_q3) AS q3Sum,
        SUM(r.v_q4) AS q4Sum,
        r.firm_id AS firmId

        FROM bw_realtime_data r JOIN bw_meter m ON r.meter_id = m.ext_id
        LEFT JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND r.meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
            <if test="dmaId != null">
                AND m.dma_id LIKE #{dmaId}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        GROUP BY
        m.meter_id,
        r.meter_id,
        m.meter_name,
        DATE(r.sample_time)
        ORDER BY
        m.meter_id,
        r.meter_id,
        r.sample_time
        LIMIT #{index}, #{rows}
        </script>""")
    fun statRealtime(dp: DataParam): List<BwData>

    @Select("""<script>
        SELECT d.dma_id AS dmaId, d.dma_name AS dmaName,
        SUM(r.duration_second) as durationSecond,
        SUM(r.water_v1) as forwardSum,
        AVG(r.pressure1) AS pressureDigits,
        AVG(r.pressure_reading) AS pressure,
        MAX(r.v_all) AS forwardDigits, MAX(r.base_all) AS baseDigits,

        SUM(r.v_q0) AS q0Sum,
        SUM(r.v_q1) AS q1Sum,
        SUM(r.v_q2) AS q2Sum,
        SUM(r.v_q3) AS q3Sum,
        SUM(r.v_q4) AS q4Sum,
        r.firm_id AS firmId

        FROM bw_realtime_data r JOIN bw_meter m ON r.meter_id = m.ext_id
        JOIN bw_dma d ON m.dma_id = d.dma_id
        <where>
            m.dma_id LIKE #{dmaId}
            <if test="meterId != null">
                AND m.meter_id LIKE #{meterId}
            </if>
            <if test="extId != null">
                AND r.meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        GROUP BY d.dma_id, d.dma_name
        </script>""")
    fun statRealtimeDma(dp: DataParam): List<BwData>

    @Select("""<script>
        SELECT
        m.meter_id as meterId,
        r.meter_id AS extId,
        r.sample_time as sampleTime,
        end_time as endTime,
        duration_second as durationSecond,
        r.water_v1 as forwardSum,
        r.pressure1 AS pressureDigits,
        r.pressure_reading AS pressure,

        r.v_all AS forwardDigits,
        r.base_all AS baseDigits,
        r.v_revert AS revertDigits,
        r.v_pulse AS literPulse,

        r.v_q0 AS q0Sum,
        r.v_q1 AS q1Sum,
        r.v_q2 AS q2Sum,
        r.v_q3 AS q3Sum,
        r.v_q4 AS q4Sum,

        m.firm_id AS firmId,
        r.flapped

        FROM bw_realtime_data r, bw_meter m

        <where>
            r.meter_id = m.ext_id
            <if test="dmaId != null">
		        AND m.dma_id = #{dmaId}
            </if>
            <if test="meterId != null">
                AND m.meter_id like #{meterId}
            </if>
	        <if test="extId != null">
	            AND r.meter_id like #{extId}
	        </if>
	        <if test="sampleTime1 != null">
	            and r.sample_time &gt;= #{sampleTime1}
	        </if>
	        <if test="sampleTime2 != null">
	            and r.sample_time &lt; #{sampleTime2}
	        </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        order by
        r.sample_time, m.meter_id
        limit #{index}, #{rows}
        </script>""")
    fun selectDmaRealtime(dp: DataParam): List<BwData>

    /**
     * Count the rows before deletion.
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT SUM(r.countRealtime) AS countRealtime
        FROM (
        SELECT meter_id, COUNT(1) AS countRealtime
        FROM bw_realtime_data
        <where>
            <if test="extId != null">
                meter_id like #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND sample_time &lt; #{sampleTime2}
            </if>
        </where>
        GROUP BY meter_id
        ) r JOIN bw_meter m ON r.meter_id = m.ext_id
        <where>
            <if test="firmId != null">
                m.firm_id LIKE #{firmId}
            </if>
        </where>
        </script>""")
    fun countDeleteRealtime(dp: DataParam): Int?

    @Delete("""<script>
        DELETE r
        FROM bw_realtime_data r
        <where>
            <if test="extId != null">
                AND r.meter_id like #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
            <if test="firmId != null">
                AND r.meter_id IN
                (
                    SELECT ext_id FROM bw_meter
                    WHERE firm_id LIKE #{firmId}
                )
            </if>
        </where>
        </script>""")
    fun deleteRealtime(dp: DataParam): Int

    /**
     * <pre>
     * <parameterMap type="map" id="realtimeMap">
     * <parameter property="meterId"></parameter>
     * <parameter property="sampleTime1"></parameter>
     * <parameter property="sampleTime2"></parameter>
     * <parameter property="dmaId"></parameter>
     * <parameter property="firmId"></parameter>
    </parameterMap> *
    </pre> *
     *
     * @param map
     * @return - map: minDate, maxDate
     */
    @Select("""<script>
        SELECT MIN(r.minDate) AS minTime, MAX(r.maxDate) AS maxTime
        FROM (
	        SELECT meter_id, MIN(r.sample_time) AS minDate, MAX(r.sample_time) AS maxDate
	        FROM bw_realtime_data r
	        <where>
	            <if test="extId != null">
	                meter_id LIKE #{extId}
	            </if>
	            <if test="extIdList != null">
	               AND meter_id IN
	               <foreach collection="extIdList" item="eid" open="(" close=")" index="idx" separator=",">
	                   #{eid}
	               </foreach>
	            </if>
	            <if test="sampleTime1 != null">
	                AND sample_time &gt;= #{sampleTime1}
	            </if>
	            <if test="sampleTime2 != null">
	                AND sample_time &lt; #{sampleTime2}
	            </if>
	        </where>
	        GROUP BY meter_id
        ) r LEFT JOIN bw_meter m ON (r.meter_id = m.ext_id)
        <where>
            <if test="meterId != null">
                m.meter_id LIKE #{meterId}
            </if>
            <if test="meterIdList != null">
                AND m.meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        </script>""")
    fun realtimeDateRange(dp: DataParam): DataRange?

    /**
     * check realtime range for specified zone.
     *
     * @param map
     * @return
     */
    @Select("""<script>
        SELECT zm.z_id AS zoneId
        , MIN(r.sample_time) AS minTime
        , MAX(r.sample_time) AS maxTime
        FROM bw_realtime_data r JOIN bw_meter m ON (r.meter_id = m.ext_id)
        JOIN dm_zone_meter zm ON m.meter_id = zm.meter_id
        <where>
            <if test="extId != null">
                r.meter_id LIKE #{extId}
            </if>
            <if test="sampleTime1 != null">
                AND r.sample_time &gt;= #{sampleTime1}
            </if>
            <if test="sampleTime2 != null">
                AND r.sample_time &lt; #{sampleTime2}
            </if>
            <if test="meterId != null">
                AND m.meter_id LIKE #{meterId}
            </if>
            <if test="zoneId != null">
                AND zm.z_id LIKE #{zoneId}
            </if>
            <if test="meterIdList != null">
                AND m.meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        </script>""")
    fun realtimeDateRangeZone(dp: DataParam): List<DataRange>

    /**
     * Gets date range for specified DMA.
     *
     * @param map
     * - must contain key 'dmaId' but no 'meterId'.
     * @return - map: minDate, maxDate
     */
    @Select("""<script>
        SELECT m.meter_id AS meterId, m.ext_id AS extId,
        r.minDate AS minTime, r.maxDate AS maxTime
        FROM (
	        SELECT meter_id, MIN(sample_time) AS minDate, MAX(sample_time) AS maxDate
	        FROM bw_realtime_data
	        <where>
	            <if test="extId != null">
	                meter_id LIKE #{extId}
	            </if>
                <if test="extIdList != null">
                   AND meter_id IN
                   <foreach collection="extIdList" item="eid" open="(" close=")" index="idx" separator=",">
                       #{eid}
                   </foreach>
                </if>
	            <if test="sampleTime1 != null">
	                AND sample_time &gt;= #{sampleTime1}
	            </if>
	            <if test="sampleTime2 != null">
	                AND sample_time &lt; #{sampleTime2}
	            </if>
	        </where>
	        GROUP BY meter_id
        ) r JOIN bw_meter m ON (r.meter_id = m.ext_id)
        <where>
            <if test="meterId != null">
                AND m.meter_id LIKE #{meterId}
            </if>
            <if test="meterIdList != null">
                AND m.meter_id IN
                <foreach collection="meterIdList" index="idx" item="mid" open="(" close=")" separator=",">
                    #{mid}
                </foreach>
            </if>
            <if test="dmaId != null">
                m.dma_id LIKE #{dmaId}
            </if>
            <if test="dmaIdList != null">
                AND m.dma_id IN
                <foreach collection="dmaIdList" item="did" index="idx" open="(" close=")" separator=",">
                    #{did}
                </foreach>
            </if>
            <if test="firmId != null">
                AND m.firm_id LIKE #{firmId}
            </if>
        </where>
        </script>""")
    fun realtimeDateRangeDma(dp: DataParam): List<DataRange>
}
