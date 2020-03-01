package com.abel.bigwater.data.mapper

import com.abel.bigmeter.service.MeterParam
import com.abel.bigwater.model.BwDma
import com.abel.bigwater.model.BwMeterBrand
import com.abel.bigwater.model.BwRemoteBrand
import com.abel.bigwater.model.BwUser
import com.abel.bigwater.model.zone.ZoneMeter
import org.apache.ibatis.annotations.*

@Mapper
//@Component(value = "meterMapper")
interface MeterMapper {

    fun selectMeterBrand(): List<BwMeterBrand>

    fun selectRemoteBrand(): List<BwRemoteBrand>

    fun insertMeter(meter: ZoneMeter): Int

    fun deleteMeter(@Param("midList") midList: List<String>): Int

    fun updateMeter(meter: ZoneMeter): Int

    fun updateMeterLoc(meter: ZoneMeter): Int

    /**
     * <pre>
     * <parameterMap type="map" id="meterWhere">
     * <parameter property="id"></parameter>
     * <parameter property="name"></parameter>
     * <parameter property="dmaId"></parameter>
     * <parameter property="typeId"></parameter>
     * <parameter property="location"></parameter>
     * <parameter property="installDate1"></parameter>
     * <parameter property="installDate2"></parameter>
     * <parameter property="meterBrandId"></parameter>
     * <parameter property="remoteBrandId"></parameter>
     * <parameter property="firmId"></parameter>
    </parameterMap> *
     * typeId can be: CHECK/CHARGE/FIRE
    </pre> *
     *
     * @param map
     * @return
     */
    fun selectMeter(mp: MeterParam): List<ZoneMeter>

    fun selectMeterText(mp: MeterParam): List<ZoneMeter>

    /**
     * The meter where type = 'CHECK'.
     *
     * @return
     */
    fun selectParentMeter(map: Map<String, Any>): List<ZoneMeter>

    /**
     * The meter with type = 'CHECK' for specified DMA.
     *
     * @param dmaId
     * @return
     */
    fun selectParentMeterByDmaId(dmaId: String): List<ZoneMeter>

    fun selectDma(@Param("firmId") firmId: String?, @Param("dmaId") dmaId: String?,
                  @Param("keywords") keywords: String?): List<BwDma>

    fun selectDmaById(@Param("dmaId") dmaId: String): BwDma

    fun insertDma(dma: BwDma): Int

    fun updateDma(dma: BwDma): Int

    fun detachDmaUser(user: BwUser): Int

    /**
     * Attach the user to the DMA list.
     *
     * @param map
     * - userId, dmaIds - can be like "'DMA01', 'DMA02'".
     */
    fun attachDmaUser(user: BwUser): Int

    fun deleteDma(@Param("dmaId") dmaId: String, @Param("firmId") firmId: String?): Int

    /**
     * <pre>
     * <parameterMap type="map" id="linkMeterDmaMap">
     * <parameter property="dmaId"></parameter>
     * <parameter property="meterIds"></parameter>
    </parameterMap> *
    </pre> *
     */
    fun linkMeterDma(dmaId: String, meterIdList: List<String>): Int

    fun detachMeterDma(dmaId: String, meterIdList: List<String>): Int

}