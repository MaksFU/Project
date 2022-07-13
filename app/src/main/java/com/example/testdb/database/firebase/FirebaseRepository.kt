package com.example.testdb.database.firebase

import androidx.lifecycle.LiveData
import com.example.testdb.database.DatabaseRepository
import com.example.testdb.modelsDb.CategoryDb
import com.example.testdb.modelsDb.WorkerDb
import com.example.testdb.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.example.testdb.R
import com.example.testdb.modelsDb.InfoDb
import com.google.firebase.database.*

class FirebaseRepository: DatabaseRepository {

    init {
        AUTH = FirebaseAuth.getInstance()
    }

    private val workersRef: DatabaseReference by lazy {REF_DATABASE.child(USER.bossId).child(WORKERS)}
    private val categoriesRef: DatabaseReference by lazy {REF_DATABASE.child(USER.bossId).child(CATEGORIES)}
    private val costsRef: DatabaseReference by lazy {REF_DATABASE.child(USER.id).child(COST)}
    private val incRef: DatabaseReference by lazy {REF_DATABASE.child(USER.id).child(INCOME)}
    private val costsMutRef: DatabaseReference by lazy {REF_DATABASE.child(USER.bossId).child(M_COST)}
    private val incMutRef: DatabaseReference by lazy {REF_DATABASE.child(USER.bossId).child(M_INCOME)}
    private val infoRef: DatabaseReference by lazy {REF_DATABASE.child(USER.bossId).child(INFO)}

    override val allWorkers: LiveData<List<WorkerDb>> by lazy {AllWorkersLiveData() }

    override suspend fun insertWorker(worker: WorkerDb, pass:String, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        AUTH.signInWithEmailAndPassword(USER.email, pass)
            .addOnFailureListener { onFail("Ваш пароль не верен") }
            .addOnSuccessListener {

                AUTH.createUserWithEmailAndPassword(worker.email, worker.firstPassword)
                    .addOnFailureListener {
                        onFail( it.message.toString()) }
                    .addOnSuccessListener {
                        workersRef.child(it.user?.uid.toString()).setValue(mapOf(
                            W_ID to it.user?.uid.toString(), W_ID_FOR_SORT to worker.idForSort,
                            W_F_NAME to worker.fName, W_S_NAME to worker.sName, W_T_NAME to worker.tName,
                            W_POSITION to worker.position, W_EMAIL to worker.email,
                            W_F_PASSWORD to worker.firstPassword, W_BOSS_ID to USER.bossId, W_IS_ADMIN to worker.admin))

                        FirebaseDatabase.getInstance().reference.child(it.user?.uid.toString()).child(W_USER_INFO)
                            .setValue(mapOf(
                                W_ID to it.user?.uid.toString(), W_EMAIL to worker.email,
                                W_F_NAME to worker.fName, W_S_NAME to  worker.sName,
                                W_T_NAME to worker.tName,
                                W_BOSS_ID to USER.bossId, W_IS_ADMIN to worker.admin))
                        AUTH.signInWithEmailAndPassword(USER.email, pass).addOnFailureListener { AUTH.signOut() }
                        onSuccess()
                    }
            }
    }

    override suspend fun deleteWorker(worker: WorkerDb, onSuccess: () -> Unit) {
        workersRef.child(worker.id).removeValue().addOnSuccessListener {
            var count  = worker.idForSort.toDouble()
            for(i in allWorkers.value!!.subList(worker.idForSort, allWorkers.value!!.size)) {
                workersRef.child(i.id).child(W_ID_FOR_SORT).setValue(count)
                count++
            }
            FirebaseDatabase.getInstance().reference.child(worker.id)
                .child(W_USER_INFO).child(W_BOSS_ID).setValue("NONE")
            onSuccess()
        }
    }

    override suspend fun updateWorkerRules(id:String, isAdmin: Boolean, pos: String) {
        workersRef.child(id).updateChildren(mapOf(W_IS_ADMIN to isAdmin, W_POSITION to pos))
        REF_DATABASE.child(id).child(W_USER_INFO).updateChildren(mapOf(W_IS_ADMIN to isAdmin, W_POSITION to pos))
    }

    override suspend fun changeWorkerIds(idClickedWorker: String, idNextWorker: String,
                                         idFSClickedWorker: Int, idFSNextWorker: Int, onSuccess: () -> Unit) {
        workersRef.child(idClickedWorker).child(W_ID_FOR_SORT).setValue(idFSClickedWorker)
        workersRef.child(idNextWorker).child(W_ID_FOR_SORT).setValue(idFSNextWorker)
    }


    override val allCategories: LiveData<List<CategoryDb>> by lazy {AllCategoriesLiveData()}

    override suspend fun insertCategory(cat: CategoryDb, onSuccess: () -> Unit) {
        val id = categoriesRef.push().key.toString()
        categoriesRef.child(id).setValue(mapOf(
            C_NAME to cat.name, W_ID to id, W_ID_FOR_SORT to cat.idForSort))
            .addOnSuccessListener { onSuccess() }
    }

    override suspend fun deleteCategory(cat: CategoryDb, onSuccess: () -> Unit) {
        categoriesRef.child(cat.id).removeValue().addOnSuccessListener {
            var count  = cat.idForSort.toDouble()
            for(i in allCategories.value!!.subList(cat.idForSort, allCategories.value!!.size)) {
                categoriesRef.child(i.id).child(W_ID_FOR_SORT).setValue(count)
                count++
            }
        }
    }


    override fun getCurrCostOrInc(cat: String, incomeOrCost:String, date: String): LiveData<Double> {
        return CurCostOrIncLiveData(cat, incomeOrCost, date)
    }

    override fun getMutCurrCostOrInc(cat: String, mutIncomeOrCost: String, date: String): LiveData<Double> {
        return CurMutCostOrIncLiveData(cat, "mut"+ mutIncomeOrCost[0].uppercase()
                    + mutIncomeOrCost.subSequence(1, mutIncomeOrCost.lastIndex+1), date)
    }


    override fun getCostOrInc(incomeOrCost: String, startAt: String, id: String)
    : LiveData<Map<String, Map<String, Long>>> {
        return CostOrIncLiveData(incomeOrCost, startAt, id)
    }

    override suspend fun insertCost(cat: String, num: Float, date: String, onSuccess: () -> Unit) {
        costsRef.child(date).child(cat).setValue(num).addOnSuccessListener { onSuccess() }
    }

    override suspend fun insertInc(cat: String, num: Float, date: String, onSuccess: () -> Unit) {
        incRef.child(date).child(cat).setValue(num).addOnSuccessListener { onSuccess() }
    }


    override suspend fun insertMutCost(cat: String, num: Float, date: String) {
        costsMutRef.child(date).child(cat).setValue(num)
    }
    override suspend fun insertMutInc(cat: String, num: Float, date: String) {
        incMutRef.child(date).child(cat).setValue(num)
    }

    override fun getInfo(from: String): LiveData<List<InfoDb>> {
        return InfoLiveData(from)
    }

    override suspend fun insertInfo(info: InfoDb, timestamp: Long) {
        val id = infoRef.push().key.toString()
        val tm = if (timestamp == -1L) ServerValue.TIMESTAMP else timestamp
        infoRef.child(id).setValue(
            mapOf(
                I_TIME to tm, I_TYPE to info.type, C_CAT to info.cat,
                W_F_NAME to info.fName, W_S_NAME to info.sName, W_T_NAME to info.tName,
                C_NOTE to info.note, W_POSITION to info.position, C_NUMBER to info.number
            )
        )
    }

    override fun getStaticCostOrIncByDay(incomeOrCost: String, day: String, id: String,
        onSuccess: (res: Map<String, Long>) -> Unit
    ){
        FirebaseDatabase.getInstance().reference.child(id).child(incomeOrCost).child(day).get()
            .addOnSuccessListener {
                val res = it.value
                if (res!= null)
                    onSuccess(it.value as Map<String, Long>)
                else onSuccess(mapOf("0" to 0))
            }

    }

    override fun registerToDatabase(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        AUTH.createUserWithEmailAndPassword(USER.email, USER.password)
            .addOnSuccessListener{
                setConsts()
                USER.bossId = it.user!!.uid
                USER.id = it.user!!.uid
                val res = mapOf(
                    W_ID to USER.id, W_EMAIL to USER.email,
                    W_F_NAME to USER.fName, W_S_NAME to  USER.sName,
                    W_T_NAME to USER.tName, W_F_PASSWORD to "Изменён",
                    W_BOSS_ID to USER.bossId, W_IS_ADMIN to USER.admin, W_POSITION to USER.position)
                REF_DATABASE.child(USER.id).child(W_USER_INFO).setValue(res)
                workersRef.child(USER.id).setValue(res)
                onSuccess()
            }
            .addOnFailureListener { onFail(it.message.toString()) }
    }

    override fun authorizeToDatabase(onSuccess: () -> Unit, onFail: (String) -> Unit, onDel: () -> Unit) {
        AUTH.signInWithEmailAndPassword(USER.email, USER.password)
            .addOnSuccessListener { res ->
                getUserInfo(res.user!!.uid, onSuccess, onDel)
            }
            .addOnFailureListener{ onFail(it.message.toString()) }
    }


     override fun getUserInfo(id: String, onSuccess: () -> Unit, onFail: () -> Unit){
        FirebaseDatabase.getInstance().reference.child(id).child(W_USER_INFO)
            .addListenerForSingleValueEvent(UserInfoListener{
                USER = it.getValue(WorkerDb :: class.java)!!
                BOTTOM_NAV.menu.findItem(R.id.first).isVisible = USER.admin
                setConsts()
                if (USER.bossId == "NONE")
                    onFail()
                else
                    onSuccess()
            })
    }

    override fun signOut() {
        AUTH.signOut()
    }

    private fun setConsts(){
        CURRENT_ID = AUTH.currentUser?.uid.toString()
        REF_DATABASE = FirebaseDatabase.getInstance().reference
    }
}