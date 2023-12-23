package tvremote.forroku.smart.tvremotecontrol.db

import android.content.Context
import android.content.SharedPreferences


class SessionManager(var context: Context) {
    private var pref: SharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)


    fun login(bool: Boolean) {
        pref.edit().putBoolean("login", bool).apply()
    }

    fun getLogin(): Boolean {
        return pref.getBoolean("login", false)
    }

    fun getLanguage(): String? {
        return pref.getString("lan", "")
    }

    fun setLanguage(id: String) {
        pref.edit().putString("lan", id).apply()
    }

    fun setRemote(a:String){
        pref.edit().putString("remote",a).apply()
    }
//    fun getRemote(): String? {
//        val value = pref.getString("remote","")
//        return if (value?.isEmpty() == true){
//            SelectRemoteEnum.IR.name
//        }else{
//            value
//        }
//    }


    fun setSound(a: Boolean) {
        pref.edit().putBoolean("sound", a).apply()
    }
    fun getSound():Boolean {
        return pref.getBoolean("sound",false)
    }

    fun setVibration(b:Boolean){
        pref.edit().putBoolean("Vibration",b).apply()
    }
    fun getVibration():Boolean{
        return pref.getBoolean("Vibration",false)
    }







    fun setTheme(a:Int) {
        pref.edit().putInt("theme",a).apply()
    }

    fun getTheme():Int{
        return pref.getInt("theme",0)
    }







//    fun setPhotoFav(path: String) {
//        val string = pref.getString("photo", "")
//        if (string?.isEmpty() == true) {
//            val arrayList = ArrayList<ImagesModel>()
//            arrayList.add(ImagesModel(picturepath = path))
//            val gson = Gson().toJson(arrayList)
//            pref.edit().putString("photo", gson).apply()
//        } else {
//            val typeToken = object : TypeToken<ArrayList<ImagesModel?>?>() {}
//            val gson = Gson().fromJson(string, typeToken)
//            gson?.add(ImagesModel(picturepath = path))
//            pref.edit().putString("photo", Gson().toJson(gson)).apply()
//        }
//    }
//
//
//
//    fun addPhotoRecent(path: String) {
//        val string = pref.getString("photoRec", "")
//        if (string?.isEmpty() == true) {
//            val arrayList = ArrayList<ImagesModel>()
//            arrayList.add(ImagesModel(picturepath = path))
//            val gson = Gson().toJson(arrayList)
//            pref.edit().putString("photoRec", gson).apply()
//        } else {
//            val typeToken = object : TypeToken<ArrayList<ImagesModel?>?>() {}
//            val gson = Gson().fromJson(string, typeToken)
//            gson?.add(ImagesModel(picturepath = path))
//            pref.edit().putString("photoRec", Gson().toJson(gson)).apply()
//        }
//    }
//
//
//    fun getPhotoRecent(): ArrayList<ImagesModel> {
//        val string = pref.getString("photoRec", "")
//        return if (string?.isEmpty() == true) {
//            ArrayList()
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            Gson().fromJson(string, typeToken) ?: ArrayList()
//        }
//    }
//
//
//    fun addVideoRecent(path: String) {
//        val string = pref.getString("videoRec", "")
//        if (string?.isEmpty() == true) {
//            val arrayList = ArrayList<ImagesModel>()
//            arrayList.add(ImagesModel(picturepath = path))
//            val gson = Gson().toJson(arrayList)
//            pref.edit().putString("videoRec", gson).apply()
//        } else {
//            val typeToken = object : TypeToken<ArrayList<ImagesModel?>?>() {}
//            val gson = Gson().fromJson(string, typeToken)
//            gson?.add(ImagesModel(picturepath = path))
//            pref.edit().putString("videoRec", Gson().toJson(gson)).apply()
//        }
//    }
//
//
//    fun getVideoRecent(): ArrayList<ImagesModel> {
//        val string = pref.getString("videoRec", "")
//        return if (string?.isEmpty() == true) {
//            ArrayList()
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            Gson().fromJson(string, typeToken) ?: ArrayList()
//        }
//    }
//
//
//
//    fun addAudioRecent(path: String) {
//        val string = pref.getString("audioRec", "")
//        if (string?.isEmpty() == true) {
//            val arrayList = ArrayList<ImagesModel>()
//            arrayList.add(ImagesModel(picturepath = path))
//            val gson = Gson().toJson(arrayList)
//            pref.edit().putString("audioRec", gson).apply()
//        } else {
//            val typeToken = object : TypeToken<ArrayList<ImagesModel?>?>() {}
//            val gson = Gson().fromJson(string, typeToken)
//            gson?.add(ImagesModel(picturepath = path))
//            pref.edit().putString("audioRec", Gson().toJson(gson)).apply()
//        }
//    }
//
//
//    fun getAudioRecent(): ArrayList<ImagesModel> {
//        val string = pref.getString("audioRec", "")
//        return if (string?.isEmpty() == true) {
//            ArrayList()
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            Gson().fromJson(string, typeToken) ?: ArrayList()
//        }
//    }
//
//
//
//
//    fun getPhotoFav(): ArrayList<ImagesModel> {
//        val string = pref.getString("photo", "")
//        return if (string?.isEmpty() == true) {
//            ArrayList<ImagesModel>()
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            Gson().fromJson(string, typeToken) ?: ArrayList<ImagesModel>()
//        }
//    }
//
//    fun deletePhotoFav(path: String, click: (Boolean) -> Unit) {
//        val string = pref.getString("photo", "")
//        if (string?.isEmpty() == true) {
//            click.invoke(true)
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            val list = Gson().fromJson(string, typeToken) ?: ArrayList<ImagesModel>()
//
//            val index = list.indexOfFirst { it.picturepath == path }
//
//            if (index != -1){
//                list.removeAt(index)
//            }
//
//            val gson = Gson().toJson(list)
//            pref.edit().putString("photo", gson).apply()
//            click.invoke(true)
//        }
//
//    }
//
//
//    fun setVideo(path: String) {
//        val string = pref.getString("video", "")
//        if (string?.isEmpty() == true) {
//            val arraylist = ArrayList<ImagesModel>()
//            arraylist.add(ImagesModel(picturepath = path))
//            val gson = Gson().toJson((arraylist))
//            pref.edit().putString("video", gson).apply()
//        } else {
//            val typeToken = object : TypeToken<ArrayList<ImagesModel?>?>() {}
//            val gson = Gson().fromJson(string, typeToken)
//            gson?.add(ImagesModel(picturepath = path))
//            pref.edit().putString("video", Gson().toJson(gson)).apply()
//        }
//    }
//
//    fun getVideo(): ArrayList<ImagesModel> {
//        val string = pref.getString("video", "")
//        return if (string?.isEmpty() == true) {
//            ArrayList<ImagesModel>()
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            Gson().fromJson(string, typeToken) ?: ArrayList<ImagesModel>()
//
//        }
//    }
//
//    fun deleteVideoFav(path: String, click: (Boolean) -> Unit) {
//        val string = pref.getString("video", "")
//        if (string?.isEmpty() == true) {
//            click.invoke(true)
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            val list = Gson().fromJson(string, typeToken) ?: ArrayList<ImagesModel>()
//
//
//            val index = list.indexOfFirst { it.picturepath == path }
//
//            if (index != -1){
//                list.removeAt(index)
//            }
//
//            val gson = Gson().toJson(list)
//            pref.edit().putString("video", gson).apply()
//            click.invoke(true)
//
//        }
//    }
//
//    fun setAudio(path: String) {
//        val string = pref.getString("audio", "")
//        if (string?.isEmpty() == true) {
//            val arraylist = ArrayList<ImagesModel>()
//            arraylist.add(ImagesModel(picturepath = path))
//            val gson = Gson().toJson((arraylist))
//            pref.edit().putString("audio", gson).apply()
//        } else {
//            val typeToken = object : TypeToken<ArrayList<ImagesModel?>?>() {}
//            val gson = Gson().fromJson(string, typeToken)
//            gson?.add(ImagesModel(picturepath = path))
//            pref.edit().putString("audio", Gson().toJson(gson)).apply()
//        }
//    }
//
//    fun getAudio(): ArrayList<ImagesModel> {
//        val string = pref.getString("audio", "")
//        return if (string?.isEmpty() == true) {
//            ArrayList<ImagesModel>()
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            Gson().fromJson(string, typeToken) ?: ArrayList<ImagesModel>()
//
//        }
//    }
//
//    fun deleteAudioFav(path: String, click: (Boolean) -> Unit) {
//        val string = pref.getString("audio", "")
//        if (string?.isEmpty() == true) {
//            click.invoke(true)
//        } else {
//            val typeToken: TypeToken<ArrayList<ImagesModel>> =
//                object : TypeToken<ArrayList<ImagesModel>>() {}
//            val list = Gson().fromJson(string, typeToken) ?: ArrayList<ImagesModel>()
//
//
//            val index = list.indexOfFirst { it.picturepath == path }
//
//            if (index != -1){
//                list.removeAt(index)
//            }
//
//            val gson = Gson().toJson(list)
//            pref.edit().putString("audio", gson).apply()
//            click.invoke(true)
//
//
//        }
//    }






}