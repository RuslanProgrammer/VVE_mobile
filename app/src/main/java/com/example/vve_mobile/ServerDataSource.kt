package com.example.vve_mobile

import android.util.Log
import com.example.vve_mobile.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class ServerDataSource(private val apiHelper: ApiHelper) {
//    companion object {
//        lateinit var _user: User
//    }

    suspend fun registerAdministrator(name: String, surname: String, email: String, password: String, shop: Int){
        val administrator = Administrator(-1, name, surname, email, shop)
//        _user = apiHelper.registerAdministrator(administrator)
    }
    suspend fun getPeopleByCheckout(id: Int): Int {
        return apiHelper.getPeopleByCheckout(id)
    }

    suspend fun getInfo(id: Int): String {
        return apiHelper.getInfo(id)
    }

    suspend fun rebalance(id: Int) {
        apiHelper.rebalance(id)
    }

    suspend fun createBackup(){
        return apiHelper.createBackup()
    }

    suspend fun restoreLastBackup(){
        return apiHelper.restoreLastBackup()
    }

    fun getUser(): User {
//        Log.d("get User", _user.toString())
        return Administrator(5, "admin", "surname", "admin@gmail.com", 1)
    }

    suspend fun getCheckoutList(): List<Checkout> {
        return apiHelper.getCheckouts()
    }

    suspend fun getShops(): List<Shop> {
        return apiHelper.getShops()
    }

    suspend fun getCheckoutListByShop(shop: Int): List<Checkout> {
        return apiHelper.getCheckoutsByShop(shop)
    }

    suspend fun getCheckoutById(id: Int): Checkout {
        return apiHelper.getCheckoutById(id)
    }

    suspend fun getAllAvailableWorkersByShopId(shop: Int) =
        (apiHelper.getAllAvailableWorkersByShopId(shop) ?: listOf())

    suspend fun getAllAvailableWorkersAndCurrentByShopId(shop: Int, worker_id: Int): List<Worker> {
        return listOf(apiHelper.getWorkerById(worker_id)) + (apiHelper.getAllAvailableWorkersByShopId(
            shop
        ) ?: listOf())
    }

    suspend fun createCheckout(title: String, description: String, shop: Int, worker: Int) {
        apiHelper.createCheckout(title, description, shop, worker)
    }

    suspend fun deleteCheckout(id: Int) {
        Log.d("DELETE", id.toString())
        apiHelper.deleteCheckout(id)
    }

    suspend fun getWorkers() = apiHelper.getWorkers()
    suspend fun updateCheckout(
        id: Int,
        title: String,
        description: String,
        shop: Int,
        worker: Int
    ) {
        apiHelper.updateCheckout(id, title, description, shop, worker)
    }

    suspend fun loginUser(email: String, password: String) : User {
        val res = apiHelper.loginAdministrator(email, password).string()
        val jsonObject = JSONObject(res.substring(res.indexOf("{"), res.lastIndexOf("}") + 1))
        val role = jsonObject.getString("role");
        val user = jsonObject.getJSONObject("user")
        if (role == "Administrator") {
//            _user = Administrator(user.getInt("id"),
//                user.getString("name"),
//                user.getString("surname"),
//                user.getString("email"),
//                "",
//                user.getInt("shop"),
//            )
        }
//        Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!", _user.toString())
            return Administrator(5, "admin", "surname", "admin@gmail.com", 1)
    }
}


interface ApiService {
    @GET("api/checkouts/")
    suspend fun getCheckouts(): List<Checkout>

    @GET("api/checkouts/{id}")
    suspend fun getCheckoutById(
        @Path("id") id: Int
    ): Checkout

    @GET("api/shops/getFreeWorkers/{id}")
    suspend fun getAllAvailableWorkersByShopId(
        @Path("id") id: Int
    ): List<Worker>?

    @GET("api/workers/")
    suspend fun getWorkers(): List<Worker>

    @GET("api/shops/")
    suspend fun getShops(): List<Shop>

    @GET("api/shops/getSituation/{id}")
    suspend fun getInfo(@Path("id") id: Int): ResponseBody

    @GET("api/backups/create/")
    suspend fun createBackup()

    @GET("api/checkouts/rebalance_by_shop/{id}")
    suspend fun rebalance(@Path("id") id: Int)

    @GET("api/backups/restore_last/")
    suspend fun restoreLastBackup()

    @GET("api/administrators/")
    suspend fun getAdministrators(): List<Administrator>

    @GET("api/checkouts/getCustomers/{id}")
    suspend fun getPeopleByCheckout(@Path("id") id: Int): Int

    @POST("api/checkouts/")
    suspend fun createCheckout(@Body checkout: Checkout)

    @POST("auth/administrator/register/")
    suspend fun registerAdministrator(@Body administrator: Administrator) : Administrator

    @POST("auth/administrator/login/")
    suspend fun loginAdministrator(@Body data: Map<String, String>) : ResponseBody

    @PUT("api/checkouts/{id}/")
    suspend fun updateCheckout(@Path("id") id: Int, @Body checkout: Checkout): Response<Unit>

    @DELETE("api/checkouts/{id}/")
    suspend fun deleteCheckout(
        @Path("id") id: Int
    ): Response<Unit>

//    @Headers("Content-Type: application/json")
//    @FormUrlEncoded
//    @POST("checkouts/")
//    suspend fun createCheckout(@Field("title") title: String, @Field("description")description: String, @Field("shop")shop: Int,@Field("worker") worker: Int)
}

object RetrofitBuilder {

    private const val BASE_URL = "http://10.192.248.211:8000/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build() //Doesn't require the adapter
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}


class ApiHelper(private val apiService: ApiService) {

    suspend fun getCheckouts() = apiService.getCheckouts()
    suspend fun getAdministrators() = apiService.getAdministrators()
    suspend fun getCheckoutsByShop(shop: Int): List<Checkout> {
        val res = apiService.getCheckouts()
        return res.filter { checkout ->
            checkout.shop == shop
        }
    }

    suspend fun getCheckoutById(id: Int) = apiService.getCheckoutById(id)
    suspend fun getInfo(id: Int): String {
        var str = ""
        try {
            val res = apiService.getInfo(id).string()
            val json = res.split(";")
            for (i in json) {
                val a = i.split(":")[0].substring(2, i.split(":")[0].length - 1)
                val b = i.split(":")[1]
                try {
                    str += "Problems in Checkout '${a}' with ${b[0]} people\n"
                } catch (exception: Exception) {

                }
            }
        }
            catch(exception2: Exception){

            }
        //Problems in ${checkouts.find(x => x.id == key).title} with ${obj[key]} people\n

        return str
    }
    suspend fun rebalance(id: Int) = apiService.rebalance(id)
    suspend fun getPeopleByCheckout(id: Int) = apiService.getPeopleByCheckout(id)
    suspend fun createBackup() = apiService.createBackup()
    suspend fun restoreLastBackup() = apiService.restoreLastBackup()
    suspend fun getAllAvailableWorkersByShopId(shop: Int) =
        apiService.getAllAvailableWorkersByShopId(shop)

    suspend fun getWorkersByShopId(shop: Int): List<Worker> {
        val res = apiService.getWorkers()
        return res.filter { worker ->
            worker.shop == shop
        }
    }

    suspend fun createCheckout(title: String, description: String, shop: Int, worker: Int) {
        apiService.createCheckout(Checkout(-1, title, shop, worker, description))
//        apiService.createCheckout(title, description,shop, worker)
    }

    suspend fun getWorkerById(workerId: Int): Worker {
        return apiService.getWorkers().filter { worker ->
            worker.id == workerId
        }.first()
    }

    suspend fun getWorkers() = apiService.getWorkers()
    suspend fun getShops() = apiService.getShops()
    suspend fun registerAdministrator(administrator: Administrator) = apiService.registerAdministrator(administrator)
    suspend fun loginAdministrator(email: String, password: String): ResponseBody {
        val map: HashMap<String, String> = HashMap()
        map["email"] = email
        map["password"] = password
        return apiService.loginAdministrator(map)
    }

    suspend fun deleteCheckout(id: Int) = apiService.deleteCheckout(id)
    suspend fun updateCheckout(
        id: Int,
        title: String,
        description: String,
        shop: Int,
        worker: Int
    ) = apiService.updateCheckout(id, Checkout(id, title, shop, worker, description))
}