package com.gitata.uts_parse_202201251017

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.gitata.uts_parse_202201251017.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import kotlin.text.Typography.quote

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getListSurah()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
        return true

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

//        when (item.itemId){
//            R.id.action_about -> {
//                Toast.makeText(this, "testing",Toast.LENGTH_SHORT).show()
//                return true
//            }
//        }
        if (item.itemId == R.id.action_about) {
            Log.d("item", item.toString())
            val intent = Intent(this@MainActivity, AboutActivity::class.java)
            startActivity(intent)
            return true
        }else{
            return super.onOptionsItemSelected(item)
        }

//        return super.onOptionsItemSelected(item)
    }


    private fun getListSurah() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "http://api.alquran.cloud/v1/surah"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                //jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                val listSurah = ArrayList<String>()

                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray = jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val no = jsonObject.getString("number")
                        val name =jsonObject.getString("name")
                        val englishName = jsonObject.getString("englishName")
                        val translation = jsonObject.getString("englishNameTranslation")
                        val ayahs = jsonObject.getString("numberOfAyahs")
                        val type = jsonObject.getString("revelationType")
                        listSurah.add("\n$no -$englishName | $translation\n $type | $ayahs \n$name\n\n ")
                    }

                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, listSurah)
                    binding.listSurah.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                // jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}