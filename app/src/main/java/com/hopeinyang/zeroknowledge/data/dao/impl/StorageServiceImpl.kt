package com.hopeinyang.zeroknowledge.data.dao.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.data.dto.Response
import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dto.ArticleContent
import com.hopeinyang.zeroknowledge.data.dto.HospitalInfo
import com.hopeinyang.zeroknowledge.data.dto.NewTreatment
import com.hopeinyang.zeroknowledge.data.dto.PatientInfo
import com.hopeinyang.zeroknowledge.data.dto.ViewPagerContents

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

import timber.log.Timber
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,


): StorageService {
    override fun getAllPatient(date: String, department:String):Flow<Response<*>> = callbackFlow {
        Timber.e("date is $date and dept is $department")
        val patients = firestore.collection(PATIENTS)
            .whereEqualTo("department", department)
            .whereEqualTo("nextDateOfAppointment", date)


        val snapShotListener = patients.addSnapshotListener { value, error ->
            val response = if (error == null && value != null){
                val data = value.documents.map { doc->
                  Timber.e("return docs is ${doc.id}")
                    doc.toObject<PatientInfo>()
                }
                Response.success(data)
            }else{
               Timber.e("failed to return doc with ${error.toString()}")
                Response.error(error.toString(), null)
            }

            trySend(response)

        }

        awaitClose{snapShotListener.remove()}

    }

    override fun getUserInfoFlow(userId: String): Flow<Response<UserInfo?>> = callbackFlow{

        val userCollection = firestore.collection(USERINFO).document(userId)

        val snapshotListener = userCollection.addSnapshotListener { value, error ->
            val response = if (error == null && value !=null){
                val data = value.toObject<UserInfo>()

                Response.success(data)

            }else{

                Response.error(error.toString(), null)
            }

            trySend(response)
        }
        awaitClose { snapshotListener.remove() }
    }

    override fun getHospitalLocation(): Flow<Response<HospitalInfo>> {
        return callbackFlow {
            val hospitalLocation = firestore.collection("Hospital")
                .document("HospitalInfo")
            val listener = hospitalLocation
                .addSnapshotListener {
                                     value, error ->
                    val response = if (error == null && value != null ){
                        val data = value.toObject<HospitalInfo>()

                        Response.success(data)
                    }else{
                        Response.error(error.toString(), null)

                    }

                    trySend(response as Response<HospitalInfo>)
                }

            awaitClose { listener.remove() }

        }
    }

    override fun getDepartmentList(): Flow<Response<List<String>>>  = callbackFlow{
        val document = firestore.collection("Hospital")
            .document("HospitalInfo")
        val listener = document.addSnapshotListener { value, error ->
            val response = if (error == null && value != null){
                val data = value.get("department") as List<String>
                Response.success(data)
            }else{
                Response.error(error.toString(), null)
        }
            trySend(response as Response<List<String>>)
        }
        awaitClose { listener.remove() }
    }

    override fun getAllPatientTreatments(patientId: String, onSuccess: (Boolean, List<NewTreatment>) -> Unit){
        firestore.collection("Patients")
            .document(patientId)
            .collection("Appointments")
            .get()
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    val result = task.result.documents.map { it.toObject<NewTreatment>() }
                    onSuccess(true, result as List<NewTreatment> )

                }else{
                    onSuccess(false, emptyList())
                }

            }
    }

    override  fun getHomePagerContents(): Flow<Response<List<ViewPagerContents>?>> = callbackFlow {
        val contents = firestore.collection(VIEW_PAGER_CONTENTS)
        val snapshotListener = contents.addSnapshotListener{value, error->
            val response = if (error==null && value != null){
                val data = value.documents.map { it.toObject<ViewPagerContents>()!! }
                Response.success(data)
            }else{
                Response.error(error.toString(), null)
            }

            trySend(response)

        }
        awaitClose{snapshotListener.remove()}
    }



    override  fun getPatient(name: String, department: String, patientInfo: (Response<*>) -> Unit){

        firestore.collection(PATIENTS)
            .whereEqualTo("department", department)
            .orderBy("firstName")
            .startAt(name.lowercase())
            .endAt(name.lowercase()+"\\uf8ff")
            .get()
            .addOnSuccessListener {doc->
                doc.forEach {
                    //Timber.e("Search is log ${it.id}")
                }

                if (doc.documents.size > 0){
                    val data = doc.toObjects<PatientInfo>()

                    val response = Response.success(data)
                    patientInfo(response)
                }

            }
            .addOnFailureListener {exception->
               val onError =  Response.error(exception.toString(), null)
                patientInfo(onError)
            }
    }

    override fun getAllUsers(): Flow<Response<List<UserInfo>>> = callbackFlow {
        Timber.d("Getting all users...")
        val userCollection = firestore.collection(USERINFO)
        val listener = userCollection.addSnapshotListener{value, error->
            val response = if (error == null && value != null){
                Timber.d("Getting all users succeeded")
                val data = value.documents.map {it.toObject<UserInfo>()}
                Response.success(data)
            }else{
                Timber.d("Getting all users failed with error${error.toString()}")
                Response.error(error.toString(), null)
            }
            trySend(response as Response<List<UserInfo>>)
        }
        awaitClose { listener.remove() }
    }


    override suspend fun saveNewUser(
        userInfo: UserInfo,
        clearAndNavigate: (String)->Unit
    ) {
        firestore.collection(USERINFO)
            .document(userInfo.userId)
            .set(userInfo)
            .addOnSuccessListener {

                //Timber.tag("Success!!!").d("Write Userinfo successfully")
                clearAndNavigate("$HOME_SCREEN/${userInfo.userId}")

        }
            .addOnFailureListener {

            }


    }

    override suspend fun updateUserRecord(
        userId: String,
        userInfoMap: Map<String, Any>,
        isSuccessful: (Boolean) -> Unit
    ) {
        firestore.collection(USERINFO).document(userId).update(userInfoMap)
            .addOnSuccessListener {
                //Timber.d("User Records updated successfully")
                isSuccessful(true)
            }
            .addOnFailureListener {
            }
    }

    override suspend fun addNewPatient(
        patientRecord: PatientInfo,
        isSuccessful: (Boolean) -> Unit
    ) {
        val doc = firestore.collection(PATIENTS).document()
       val patientInfo = patientRecord.copy(patientId = doc.id)

        doc.set(patientInfo)
            .addOnSuccessListener {
                isSuccessful(true)
            }
            .addOnFailureListener {
                isSuccessful(false)
            }
    }

    override suspend fun addTreatment(
        newTreatment: NewTreatment,
        onSuccess: (Boolean) -> Unit
    ) {
        Timber.e("was here called")
        firestore.collection(PATIENTS)
            .document(newTreatment.patientId)
            .collection(APPOINTMENTS)
            .document(newTreatment.treatmentDate)
            .set(newTreatment)
            .addOnSuccessListener {
                Timber.e("succeeded here")
                firestore.collection(PATIENTS).document(newTreatment.patientId).update(
                    mapOf(
                        "nextDateAppointment" to newTreatment.nextAppointment,
                        "lastDateOfAppointment" to newTreatment.treatmentDate,
                        "prescription" to newTreatment.prescription,
                        "doctorComments" to newTreatment.doctorComments,
                        "lastTreatment" to newTreatment.treatment,

                    )
                ).addOnSuccessListener {

                    onSuccess(true)
                }.addOnFailureListener {
                    onSuccess(false)
                }

            }
            .addOnFailureListener {
                Timber.e("could add new patient appointment with ${it.message}")
                onSuccess(false)

            }
    }

    override suspend fun updateHospiceLocation(
        hospitalInfo: HospitalInfo,
        isSuccessful: (Boolean) -> Unit
    ) {
        firestore.collection("Hospital")
            .document("HospitalInfo")
            .update("gpsCoordinates", hospitalInfo.gpsCoordinates)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    isSuccessful(true)
                }else{
                    isSuccessful(false)
                }
            }
    }

    override  fun getGuidelines(category:String, role: String): Flow<Response<List<ArticleContent>>> = callbackFlow{

        val docs = firestore.collection("OrgDocs")
            .document(category)
            .collection(role)
            .whereEqualTo("isPublished", true)

        val snapShotListener = docs.addSnapshotListener { value, error ->
            val response = if (error == null && value != null) {

                val data = value.documents.map { doc ->
                    doc.toObject<ArticleContent>()
                }

                Timber.e("firebase error ${data}")
                Response.success(data)
            }else{

                Timber.e("firebase error ${error.toString()}")
                Response.error(error.toString(), null)
            }

            trySend(response as Response<List<ArticleContent>>)
        }
        awaitClose { snapShotListener.remove() }
    }

    override fun getBaseDocs(): Flow<Response<List<ArticleContent>>> = callbackFlow{

        val docs = firestore.collection("OrgDocs")
            .document("operationDocs")
            .collection("BaseDoc")
            .whereEqualTo("isPublished", true)

        val snapShotListener = docs.addSnapshotListener { value, error ->
            val response = if (error == null && value != null) {

                val data = value.documents.map { doc ->
                    doc.toObject<ArticleContent>()
                }


                Response.success(data)
            }else{

                Response.error(error.toString(), null)
            }

            trySend(response as Response<List<ArticleContent>>)
        }
        awaitClose { snapShotListener.remove() }
    }

    override fun getOperationalDocs(): Flow<Response<List<ArticleContent>>> = callbackFlow{
        val docs = firestore.collection("OrgDocs")
            .document("operationDocs")
            .collection("OperationalDocCollection")
            .whereEqualTo("isPublished", true)

        val snapShotListener = docs.addSnapshotListener { value, error ->
            val response = if (error == null && value != null) {

                val data = value.documents.map { doc ->
                    doc.toObject<ArticleContent>()
                }


                Response.success(data)
            }else{

                Response.error(error.toString(), null)
            }

            trySend(response as Response<List<ArticleContent>>)
        }
        awaitClose { snapShotListener.remove() }
    }


    companion object{
        private const val VIEW_PAGER_CONTENTS = "ViewPagerContents"
        private const val PATIENTS = "Patients"
        private const val USERINFO = "Users"
        private const val APPOINTMENTS = "Appointments"
        private const val USERLOGS = "UserLogs"
    }
}