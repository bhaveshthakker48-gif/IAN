package com.org.wfnr_2024.ViewModel

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.bombayneurosciences.bna_2023.Model.Journal1.JournalDataResponse
import org.bombayneurosciences.bna_2023.Model.Journal1.JournalResponseItem
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import retrofit2.Response
import java.lang.Exception


class BNA_ViewModel(val BNARespository: BNARespository, val app:Application):ViewModel() {


    val getJournalLiveData: MutableLiveData<ResourceApp<List<JournalResponseItem>>> = MutableLiveData()

    private val _journalData = MutableLiveData<List<JournalDataResponse.JournalDataResponseItem>>()
    val journalData: LiveData<List<JournalDataResponse.JournalDataResponseItem>> = _journalData




    /*fun getJournalData(progressDialog: ProgressDialog, context: Context)=viewModelScope.launch {

        try {
            if (ConstanstsApp.checkInternetConenction(app))
            {
                progressDialog.dismiss()

                getJournalLiveData.postValue(ResourceApp.Loading())
                val data=BNARespository.getJournalData()
                getJournalLiveData.postValue(handleGetJournalData(data)!!)

            }
            else{
                //Toast.makeText(app, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
                ConstanstsApp.showCustomToast(context, R.string.no_internet_connection.toString())
                progressDialog.dismiss()
            }

        }
        catch (e: Exception)
        {
            e.printStackTrace()
            progressDialog.dismiss()
        }

    }

    private fun handleGetJournalData(response: Response<List<JournalResponseItem>>): ResourceApp<List<JournalResponseItem>> {
        if (response.isSuccessful) {
            response.body()?.let { resultSuccess ->
                return ResourceApp.Success(resultSuccess)
            }
        }
        return ResourceApp.Error(response.message())
    }

   */

    fun fetchJournalData() {
        viewModelScope.launch {
            try {

                val response = BNARespository.getJournalData()
                _journalData.value = response // Update LiveData with the response

               /* val gson = Gson()
                val json = gson.toJson(response)
                Log.e("pdfdatalist",""+json)*/

            } catch (e: Exception) {
                // Handle error
            }
        }
    }


}


