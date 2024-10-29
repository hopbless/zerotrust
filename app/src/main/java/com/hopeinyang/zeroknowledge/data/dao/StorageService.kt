package com.hopeinyang.zeroknowledge.data.dao

import com.hopeinyang.zeroknowledge.data.dto.ArticleContent
import com.hopeinyang.zeroknowledge.data.dto.HospitalInfo
import com.hopeinyang.zeroknowledge.data.dto.NewTreatment
import com.hopeinyang.zeroknowledge.data.dto.PatientInfo
import com.hopeinyang.zeroknowledge.data.dto.Response
import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import com.hopeinyang.zeroknowledge.data.dto.ViewPagerContents
import kotlinx.coroutines.flow.Flow

interface StorageService {

    fun getAllPatient(date:String, department:String):Flow<Response<*>>
    fun getUserInfoFlow(userId: String):Flow<Response<UserInfo?>>
    fun getHospitalLocation():Flow<Response<HospitalInfo>>
    fun getDepartmentList(): Flow<Response<List<String>>>

    fun getAllPatientTreatments(patientId: String, onSuccess: (Boolean, List<NewTreatment>) -> Unit)
    fun getGuidelines(category:String, role:String):Flow<Response<List<ArticleContent>>>

    fun getBaseDocs():Flow<Response<List<ArticleContent>>>
    fun getOperationalDocs():Flow<Response<List<ArticleContent>>>

    fun getHomePagerContents():Flow<Response<List<ViewPagerContents>?>>
    fun getPatient(name:String, department: String, patientInfo:(Response<*>)->Unit)
    fun getAllUsers():Flow<Response<List<UserInfo>>>

    suspend fun saveNewUser(
        userInfo: UserInfo,
        clearAndNavigate:(String)->Unit
    )

    suspend fun updateUserRecord(userId: String,  userInfoMap:Map<String, Any>, isSuccessful: (Boolean) -> Unit)

    suspend fun addNewPatient(patientRecord: PatientInfo, isSuccessful: (Boolean) -> Unit)
    suspend fun addTreatment(
        newTreatment: NewTreatment,
        onSuccess: (Boolean) -> Unit
    )

    suspend fun updateHospiceLocation(hospitalInfo: HospitalInfo, isSuccessful: (Boolean) -> Unit)





}