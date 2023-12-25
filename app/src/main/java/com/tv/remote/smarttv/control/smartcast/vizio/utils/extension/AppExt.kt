package com.tv.remote.smarttv.control.smartcast.vizio.utils.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.utils.general.AppConstant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
//fun View.setOnSingleClickListener(l: (View) -> Unit) {
//    setOnClickListener(OnSingleClickListener(l))
//}


fun isOrientationPortrait(context: Context): Boolean {
    return (context.resources.configuration.orientation
            == Configuration.ORIENTATION_PORTRAIT)
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}


fun Vibrator.playVibrate() {
    if (Build.VERSION.SDK_INT >= 26) {
        this.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        this.vibrate(200)
    }
}

fun Activity.showErrorDialog(context: Context?, errorString: String?) {
    AlertDialog.Builder(context).setTitle("Error")
        .setMessage(errorString)
        .setPositiveButton("ok") { dialog, id -> dialog.cancel() }
        .create()
        .show()
}


fun getDisplaySize(context: Context): Point {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val width = display.width
    val height = display.height
    return Point(width, height)
}


fun formatMillis(millisec: Int): String {
    var seconds = (millisec / 1000)
    val hours = seconds / (60 * 60)
    seconds %= 60 * 60
    val minutes = seconds / 60
    seconds %= 60
    val time: String = if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
    return time
}



fun Activity.permissionsChecker(permissions: Array<String>, value: (Boolean) -> Unit) {
    Permissions.check(this, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            value.invoke(true)
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            showToast("Permission Denied!")
            value.invoke(false)
        }
    })
}


//fun Activity.permissionsChecker(permissions: Array<String>, value: (Boolean) -> Unit) {
//    Permissions.check(this, permissions, null, null, object : PermissionHandler() {
//        override fun onGranted() {
//            value.invoke(true)
//        }
//
//        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
//            super.onDenied(context, deniedPermissions)
//            showToast("Permission Denied!")
//            value.invoke(false)
//        }
//    })
//}


//fun bitmapToUriConverter(mBitmap: Bitmap, context: Context): Uri? {
//    var uri: Uri? = null
//    try {
//        val options = BitmapFactory.Options()
//        options.inSampleSize = calculateInSampleSize(
//            options,
//            100,
//            100
//        )
//        options.inJustDecodeBounds = false
//        val newBitmap = Bitmap.createScaledBitmap(
//            mBitmap, 200, 200,
//            true
//        )
//        val file: File = File(
//            context.filesDir, "Image"
//                    + Random().nextInt() + ".jpeg"
//        )
//        val out: FileOutputStream = context.openFileOutput(
//            file.name,
//            Context.MODE_WORLD_READABLE
//        )
//        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//        out.flush()
//        out.close()
//        val realPath = file.absolutePath
//        val f = File(realPath)
//        uri = Uri.fromFile(f)
//    } catch (e: java.lang.Exception) {
////        Log.e("Your Error Message", e.message!!)
//    }
//    return uri
//}

//fun calculateInSampleSize(
//    options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
//): Int {
//    val height = options.outHeight
//    val width = options.outWidth
//    var inSampleSize = 1
//    if (height > reqHeight || width > reqWidth) {
//        val halfHeight = height.div(2)
//        val halfWidth = width.div(2)
//        while (halfHeight.div(inSampleSize) >= reqHeight
//            && halfWidth.div(inSampleSize) >= reqWidth
//        ) {
//            inSampleSize *= 2
//        }
//    }
//    return inSampleSize
//}

//fun getVersionCode() = BuildConfig.VERSION_CODE


//fun getVersionName() = BuildConfig.VERSION_NAME


fun getMobileVersion() = Build.VERSION.SDK_INT

//@SuppressLint("HardwareIds")
//fun getDeviceId(context: Context): String? {
//    val deviceId: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//    } else {
//        val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        if (mTelephony.deviceId != null) {
//            mTelephony.deviceId
//        } else {
//            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        }
//    }
//    return deviceId
//}

//
//@SuppressLint("HardwareIds")
//fun Fragment.getIMEIDeviceId(context: Context): String? {
//    val deviceId: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//    } else {
//        val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                return ""
//            }
//        }
//        if (mTelephony.deviceId != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mTelephony.imei
//            } else {
//                mTelephony.deviceId
//            }
//        } else {
//            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        }
//    }
//    return deviceId
//}

//fun Fragment.getAndroidUId() {
//    return Settings.Secure.getString()
//}

//
//fun Fragment.manageKeyboard(string: String, pinView: PinView) {
//    val pinString = pinView.text.toString()
//    val merString = pinString + string
//    pinView.setText(merString)
//}
//
//fun Fragment.manageKeyboard(string: String, pinView: EditText) {
//    val pinString = pinView.text.toString()
//    val merString = pinString + string
//    pinView.setText(merString)
//}

//fun Fragment.clockInUpdate(cardText: TextView, tvClock: TextView) {
//    cardText.text = CLOCK_OUT
//    tvClock.text = getCurrentDateAndTime24Hours()
//}
//
//fun Fragment.clockOutUpdate(cardText: TextView, tvClock: TextView) {
//    cardText.text = CLOCK_IN
//    tvClock.text = NOT_CLOCK_IN
//}
//
//fun Fragment.manageTillKeyboard(string: String, textView: TextView) {
//    val pinString = textView.text.toString()
//    val merString = pinString + string
//    textView.text = merString
//}
//
//fun Fragment.manageKeyboard(string: String, textView: TextView) {
//    val pinString = textView.text.toString()
//    val merString = pinString + string
//    textView.text = merString
//}
//
//fun Fragment.clearTillKeyBoard(pinView: TextView) {
//    pinView.text = ""
//}
//
//fun Fragment.clearKeyBoard(pinView: PinView) {
//    val pinViewText = pinView.text.toString()
//    if (pinViewText.isNullOrEmpty().not())
//        pinView.setText(pinViewText.dropLast(1))
//
//}
//
//fun Fragment.clearKeyBoard(pinView: EditText) {
//    val pinViewText = pinView.text.toString()
//    if (pinViewText.isNullOrEmpty().not())
//        pinView.setText(pinViewText.dropLast(1))
//
//}

//fun Fragment.generateOrderId(branchId: Int?, cashierId: Int?, lastOrderId: Int): String {
//    return "ORD$branchId$cashierId${lastOrderId.plus(1)}"
//}

//
//fun EditText.isValidPhone(): Boolean {
//    return (!TextUtils.isEmpty(text.toString().trim { it <= ' ' })
//            && Patterns.PHONE.matcher(text.toString().trim { it <= ' ' }).matches()
//            && text.toString().length >= 8)
//}

//fun EditText.isEmailValid(): Boolean {
//    return !TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches()
//}


fun View.preventTwoClick() {
    this.isEnabled = false
    this.postDelayed({ this.isEnabled = true }, 500)
}

fun View.disabled() {
    this.isEnabled = false
}

fun View.randomColor(): Int {
    val rnd = Random()
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}


fun EditText.scrollEditText() {
    this.setOnTouchListener(View.OnTouchListener { v, event ->
        if (this.hasFocus()) {
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
            }
        }
        false
    })
}

fun String.splitString() = this.split(",").map { it.trim() }
fun <T> List<T>.second(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[1]
}

fun <T> List<T>.third(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[2]
}

//fun ImageView.loadImage(resourceID: Int) {
//    Glide.with(context)
//        .load(resourceID)
//        .into(this)
//}

//fun View.setOnSingleClickListener(l: (View) -> Unit) {
//    setOnClickListener(OnSingleClickListener(l))
//}

fun <T> RecyclerView.Adapter<*>.autoNotify(
    oldList: List<T>,
    newList: List<T>,
    compare: (T, T) -> Boolean
) {

    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return compare(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
    })
    diff.dispatchUpdatesTo(this)
}


//fun ImageView.loadImage(uri: Uri?) {
//    Glide.with(context)
//        .load(uri)
//        .centerCrop()
//        .placeholder(R.drawable.app_icon)
//        .into(this)
//}

fun ImageView.loadImage(string: String?, activity: Activity) {
    Glide.with(activity.applicationContext).load(string)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(true)
        .into(this)
}


fun ImageView.loadImage(string: Int?, activity: Activity) {
    Glide.with(activity.applicationContext).load(string)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(true)
        .into(this)
}
fun ImageView.loadImage(array: ArrayList<GalleryImagesModel>?, activity: Activity) {
    Glide.with(activity.applicationContext).load(array)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(true)
        .into(this)
}



fun Long.convertLongToTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat(AppConstant.currentDateFormat, Locale.US)
    return format.format(date)
}


//
//fun date(): String? {
//    val time = getCurrentTime12Hours()
//    val currentDate = getCurrentDate()
//    return if (time < "12:00 AM") {
//        currentDate
//    } else {
//        getPreviousDate(currentDate)
//    }
//}

//fun Fragment.convert24To12(time: String, time1: (String) -> Unit)  {
//
//    try {
//        val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE_AND_TIME_24_HOUR)
//        val dateObj = sdf.parse(time)
//        time1.invoke(SimpleDateFormat(AppConstant.FORMAT_DATE_AND_TIME_FOR_INVOICE_DATE).format(dateObj))
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//}
//
//fun Fragment.getBase64(input : String) : String {
//    return Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
//}

//fun convertLongToTime(time: Long): String {
//    val date = Date(time)
//    val format = SimpleDateFormat(FORMAT_DATE)
//    return format.format(date)
//}
//
//infix fun TextView.round(value: String) {
//    this.text = value.toBigDecimal().setScale(2, RoundingMode.UP).toString()
//}
//
//fun Double.round(): Double {
//    return this.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
//}
//
//fun Double.roundUp(): Double {
//    return this.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
//}
//
//fun Double.roundUpThree(): Double {
//    return this.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()
//}
//
//fun Double.roundDecimal(digit: Int) = "%.${digit}f".format(this)
//
//fun Double.computeFinalValue(): Double {
//    val formatter: NumberFormat = DecimalFormat(".###")
//    formatter.roundingMode = RoundingMode.FLOOR
//    return formatter.format(this.roundUp()).toDouble()
//}

//
//fun Double.pointThreeFormat(): Double {
//    val df = DecimalFormat("#.###")
//    return df.format(this).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
//}


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun TextView.defaultTextColor() {
    this.setTextColor(ContextCompat.getColor(this.context, R.color.black))
}

//fun View.defaultBackGround() {
//    this.setBackgroundResource(R.drawable.otp_screen_background)
//}


fun ImageView.defaultImageColor() {
    this.setColorFilter(ContextCompat.getColor(this.context, R.color.black))
}

fun View.gone() {
    this.visibility = View.GONE
}


fun EditText.string(): String {
    return this.text.toString().trim()
}

fun String?.requireString(): String {
    return "" + this
}

//fun EditText.error(errorMessage: String?) {
//    error = context.getString(R.string.field_required)
//    errorMessage?.let {
//        error = errorMessage
//    }
//    requestFocus()
//
//}
//fun Activity.shareWhatsApp() {
//    val whatsappIntent = Intent(Intent.ACTION_SEND)
//    whatsappIntent.type = "text/plain"
//    whatsappIntent.setPackage(getString(R.string.package_whatsapp))
//    whatsappIntent.putExtra(
//        Intent.EXTRA_TEXT,
//        getString(R.string.invite_text) + BuildConfig.APPLICATION_ID
//    )
//    try {
//        this.startActivity(whatsappIntent)
//    } catch (ex: ActivityNotFoundException) {
//        showToast(getString(R.string.whatsapp_error))
//    }
//}
//
//fun Activity.shareFacebook() {
//    val sendIntent = Intent()
//    sendIntent.action = Intent.ACTION_SEND
//    sendIntent.putExtra(
//        Intent.EXTRA_TEXT,
//        getString(R.string.invite_text) + BuildConfig.APPLICATION_ID
//    )
//    sendIntent.type = "text/plain"
//    sendIntent.setPackage(getString(R.string.package_fb))
//    try {
//        this.startActivity(sendIntent)
//    } catch (ex: ActivityNotFoundException) {
//        showToast(getString(R.string.fb_error))
//    }
//}

//fun Fragment.showAlertDialog(msg: String) {
//    var newMessage = msg
//    if (newMessage.isEmpty()) {
//        newMessage = "Unable to process your request \nPlease try again later !!"
//    }
//    AlertMessageDialog.newInstance(newMessage)
//        .show(requireActivity().supportFragmentManager, AlertMessageDialog.TAG)
//}


fun Fragment.removeSpace(string: String): String {
    return string.replace(" ", "")
}

fun getIndex(spinner: Spinner, myString: String): Int {
    for (i in 0 until spinner.count) {
        if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
            return i
        }
    }
    return 0
}

fun Activity.openWifiSetting() {
    try {
        this.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Activity.shareText() {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        var shareMessage = "\nLet me recommend you this application\n\n"
        shareMessage =
            """
               ${shareMessage}https://play.google.com/store/apps/details?id=${this.packageName}
                """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        this.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: Exception) {
        e.message?.let { showToast(it) }
    }
}

fun ArrayList<Double>.sum(): Double {
    var sum: Double = 0.0
    for (element in this) {
        sum += element
    }
    return sum
}
//
//infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
//infix fun Int.shl(that: Byte): Int = this.shl(that.toInt()) // Not necessary in this case because no there's (Int shl Byte)
//infix fun Byte.shl(that: Byte): Int = this.toInt().shl(that.toInt()) // Not necessary in this case because no there's (Byte shl Byte)
//
//infix fun Byte.and(that: Int): Int = this.toInt().and(that)
//infix fun Int.and(that: Byte): Int = this.and(that.toInt()) // Not necessary in this case because no there's (Int and Byte)
//infix fun Byte.and(that: Byte): Int = this.toInt().and(that.toInt())
//
//
//
//


//fun Int.getEposExceptionText(): String {
//    var return_text = ""
//    return_text = when (this) {
//        Epos2Exception.ERR_PARAM -> "ERR_PARAM"
//        Epos2Exception.ERR_CONNECT -> "ERR_CONNECT"
//        Epos2Exception.ERR_TIMEOUT -> "ERR_TIMEOUT"
//        Epos2Exception.ERR_MEMORY -> "ERR_MEMORY"
//        Epos2Exception.ERR_ILLEGAL -> "ERR_ILLEGAL"
//        Epos2Exception.ERR_PROCESSING -> "ERR_PROCESSING"
//        Epos2Exception.ERR_NOT_FOUND -> "ERR_NOT_FOUND"
//        Epos2Exception.ERR_IN_USE -> "ERR_IN_USE"
//        Epos2Exception.ERR_TYPE_INVALID -> "ERR_TYPE_INVALID"
//        Epos2Exception.ERR_DISCONNECT -> "ERR_DISCONNECT"
//        Epos2Exception.ERR_ALREADY_OPENED -> "ERR_ALREADY_OPENED"
//        Epos2Exception.ERR_ALREADY_USED -> "ERR_ALREADY_USED"
//        Epos2Exception.ERR_BOX_COUNT_OVER -> "ERR_BOX_COUNT_OVER"
//        Epos2Exception.ERR_BOX_CLIENT_OVER -> "ERR_BOX_CLIENT_OVER"
//        Epos2Exception.ERR_UNSUPPORTED -> "ERR_UNSUPPORTED"
//        Epos2Exception.ERR_FAILURE -> "ERR_FAILURE"
//        else -> String.format("%d", this)
//    }
//    return return_text
//}

fun String.capitalLetter(): String {
    return this?.replaceFirstChar { it.uppercase() }!!
}


fun Activity.actionView(url: String) {
    this.applicationContext?.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    )
}


fun Activity.openActivityWithExist(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
    this.finish()
}

fun Activity.openActivity(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
}

fun Activity.openActivityWithExist(clazz: Class<out Activity>, hasExtra: Boolean) {
    val intent = Intent(this, clazz)
    // intent.putExtra(HAS_NOTIFICATION, hasExtra)
    startActivity(intent)
    this.finish()
}

fun Activity.openActivityForResult(clazz: Class<out Activity>, requestCode: Int) {
    startActivityForResult(Intent(this, clazz), requestCode)
}


//fun Fragment.showAlertDialog(msg: String) {
//    var newMessage = msg
//    if (newMessage.isEmpty()) {
//        newMessage = "Unable to process your request \nPlease try again later !!"
//    }
//    DialogUtils.newInstance(newMessage)
//        .show(requireActivity().supportFragmentManager, "AlertMessageDialog.TAG")
//}
//
//fun FragmentActivity.showAlertDialog(msg: String) {
//    var newMessage = msg
//    if (newMessage.isEmpty()) {
//        newMessage = "Unable to process your request \nPlease try again later !!"
//    }
//    DialogUtils.newInstance(newMessage).show(supportFragmentManager, "DialogUtils")
//}


fun View.setAnimation(animation: Int) {
    val startAnimation: Animation = AnimationUtils.loadAnimation(context, animation)
    this.startAnimation(startAnimation)
}

fun View.animationClear(animation: Int) {
    AnimationUtils.loadAnimation(context, animation)
    this.clearAnimation()
}

fun View.showSnackBar(message: String) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snackbar.show()
}
//
//fun String.stringToModel(): SystemOrderStatus {
//    return Gson().fromJson(this, SystemOrderStatus::class.java)
//}

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//fun isNetworkAvailable(context: Context): Boolean {
//    val connectivityManager =
//        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
//}


//
//fun Activity.permissionsChecker(permissions: Array<String>, value: (Boolean) -> Unit) {
//    Permissions.check(this, permissions, null, null, object : PermissionHandler() {
//        override fun onGranted() {
//            value.invoke(true)
//        }
//
//        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
//            super.onDenied(context, deniedPermissions)
//            showToast(getString(R.string.permissions_denied))
//            value.invoke(false)
//        }
//    })
//}


fun Fragment.permissionsChecker(permissions: Array<String>, value: (Boolean) -> Unit) {
    Permissions.check(requireActivity(), permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            value.invoke(true)
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            showToast("Permission Denied!")
            value.invoke(false)
        }
    })
}


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

//fun Fragment.showToast(message: String, activity: Activity) {
//    val layout = activity.layoutInflater.inflate(
//        R.layout.custom_toast,
//        activity.findViewById(R.id.cl_customToastContainer)
//    )
//    layout.findViewById<TextView>(R.id.tv_message).text = message
//
//
//    Toast(activity).apply {
//        setGravity(Gravity.TOP, 0, 0)
//        duration = Toast.LENGTH_SHORT
//        view = layout
//        show()
//    }
//}

//fun Activity.showToast(message: String, activity: Activity) {
//    val layout = activity.layoutInflater.inflate(
//        R.layout.custom_toast,
//        activity.findViewById(R.id.cl_customToastContainer)
//    )
//    layout.findViewById<TextView>(R.id.tv_message).text = message
//
//
//    Toast(activity).apply {
//        setGravity(Gravity.TOP, 0, 0)
//        duration = Toast.LENGTH_SHORT
//        view = layout
//        show()
//    }
//}


fun Fragment.getSimpleName(): String {
    return this.javaClass.simpleName
}

fun Fragment.getColorCustom(color: Int): Int {
    return ContextCompat.getColor(requireContext(), color)
}

fun getColorCustom(color: Int, context: Context): Int {
    return ContextCompat.getColor(context, color)
}

fun String?.require(): String {
    var value = ""
    this?.let {
        value = it
    }
    return value
}

fun Int?.require(): Int {
    var value = -1
    this?.let {
        value = it
    }
    return value
}

//fun Fragment.getCurrentDateAndTime24Hours(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE_AND_TIME_24_HOUR)
//    return sdf.format(Date())
//}

//
//fun Fragment.getInvoiceDateQrCode(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE_AND_TIME_FOR_INVOICE_DATE)
//    return sdf.format(Date())
//}
//
//
//fun getCurrentDateAndTime24Hours(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE_AND_TIME_24_HOUR)
//    return sdf.format(Date())
//}
//
//fun Fragment.getCurrentTime12Hours(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_TIME_12_HOUR)
//    return sdf.format(Date().time)
//}
//
//fun Fragment.getCurrentTime24Hours(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_TIME_24_HOUR)
//    return sdf.format(Date().time)
//}
//
//fun Fragment.time24(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_TIME_HOUR)
//    return sdf.format(Date().time)
//}


//fun getCurrentTime12Hours(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_TIME_12_HOUR)
//    return sdf.format(Date().time)
//}


fun String.changeDateTimeFormat(oldFormat: String, newFormat: String): String? {
    val parser = SimpleDateFormat(oldFormat, Locale.ENGLISH)
    val formatter = SimpleDateFormat(newFormat, Locale.ENGLISH)
    formatter.timeZone = TimeZone.getDefault()
    return parser.parse(this)?.let { formatter.format(it) }
}


//fun Long.changeDateTimeFormat(): String? {
//    val date = Date(this)
//    val formatter = SimpleDateFormat(AppConstant.SHOW_FORMAT, Locale.ENGLISH)
//    return formatter.format(date)
//}


//fun getCurrentDate(): String {
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE)
//    return sdf.format(Date())
//}


//fun Fragment.getPreviousDate(inputDate: String): String? {
//    var inputDate = inputDate
//    val format = SimpleDateFormat(AppConstant.FORMAT_DATE)
//    try {
//        val date = format.parse(inputDate)
//        val c = Calendar.getInstance()
//        c.time = date
//        c.add(Calendar.DATE, -1)
//        inputDate = format.format(c.time)
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//        inputDate = ""
//    }
//    return inputDate
//}
//
//fun getPreviousDate(inputDate: String): String? {
//    var inputDate = inputDate
//    val format = SimpleDateFormat(AppConstant.FORMAT_DATE)
//    try {
//        val date = format.parse(inputDate)
//        val c = Calendar.getInstance()
//        c.time = date
//        c.add(Calendar.DATE, -1)
//        inputDate = format.format(c.time)
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//        inputDate = ""
//    }
//    return inputDate
//}
//
//fun getFutureDate(inputDate: String): String {
//    var inputDate = inputDate
//    val format = SimpleDateFormat(AppConstant.FORMAT_DATE)
//    try {
//        val date = format.parse(inputDate)
//        val c = Calendar.getInstance()
//        c.time = date
//        c.add(Calendar.DATE, 1)
//        inputDate = format.format(c.time)
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//        inputDate = ""
//    }
//    return inputDate
//}

fun getPastDay(): Calendar {
    val calendar: Calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, -1)
    return calendar
}
//
//fun currentTimeWithFormat(): String {
//    val currentDate = Date().time
//    val myFormat = AppConstant.FORMAT_TIME_12_HOUR_WITH_SECONDS
//    val sdf = SimpleDateFormat(myFormat, Locale.US)
//    return sdf.format(currentDate)
//}
//
//fun timeFormat(string: Date): String {
//    val myFormat = AppConstant.FORMAT_TIME_12_HOUR_WITH_SECONDS
//    val sdf = SimpleDateFormat(myFormat, Locale.US)
//    return sdf.format(string)
//}


//
//fun timeDialog(context: Context, string: (String) -> Unit) {
//    val calendar = Calendar.getInstance()
//    TimePickerDialog(context, { _: TimePicker?, hourOfDay: Int, minute: Int ->
//        calendar[Calendar.MINUTE] = minute
//        calendar[Calendar.HOUR] = hourOfDay
//        val time = calendar.time
//        string.invoke(timeFormat(time))
//    }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], true).show()
//}

//fun getCurrentDate(): String {
//    val calendar = Calendar.getInstance()
//    val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE, Locale.ENGLISH)
//    return sdf.format(calendar.time)
//}

fun getCurrentDateLong(): Long {
    val calendar = Calendar.getInstance()
    return calendar.timeInMillis
}

fun getPreviousDate(): Long {
    val cal = Calendar.getInstance()
    cal.time = cal.time
    cal.add(Calendar.DAY_OF_MONTH, -5) //Goes to previous day
    return cal.timeInMillis
}

//
//@RequiresApi(Build.VERSION_CODES.N)
//fun dateDialog(context: Context, string: (String, Long) -> Unit) {
//    val calendar = Calendar.getInstance()
//    val dateSetListener =
//        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            calendar[Calendar.YEAR] = year
//            calendar[Calendar.MONTH] = monthOfYear
//            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
//            val sdf = SimpleDateFormat(AppConstant.FORMAT_DATE, Locale.ENGLISH)
//            string.invoke(sdf.format(calendar.time), calendar.timeInMillis)
//        }
//
//    DatePickerDialog(
//        context,
//        dateSetListener,
//        calendar[Calendar.YEAR],
//        calendar[Calendar.MONTH],
//        calendar[Calendar.DAY_OF_MONTH]
//    ).show()
//
//}

fun Fragment.getColorToLoad(id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requireContext().getColor(id)
    } else {
        requireContext().resources.getColor(id)
    }
}

fun EditText.getString(): String {
    return if (this.text.isNullOrEmpty()) {
        ""
    } else {
        this.text.toString()
    }
}

fun String.splitSecound(): String {
    return this.split(":")[1]
}

fun TextView.getString(): String {
    return if (this.text.isNullOrEmpty()) {
        ""
    } else {
        this.text.toString()
    }
}

fun EditText.getInt(): Int {
    var value = 0
    try {
        value = this.text.toString().toInt()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}


fun String.getDouble(): Double {
    var value = 0.0
    try {
        value = this.toDouble()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}

fun TextView.getDouble(): Double {
    var value = 0.0
    try {
        value = this.text.toString().toDouble()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}

fun EditText.getDouble(): Double {
    return if (this.text.isNullOrEmpty()) {
        0.0
    } else {
        this.text.toString().toDouble()
    }
}

fun TextView.getInt(): Int {
    var value = 0
    try {
        value = this.text.toString().toInt()
    } catch (e: Exception) {
    }
    return value
}

fun EditText.observe(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun TabLayout.observe(tabPosition: (Int) -> Unit) {
    this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            tabPosition.invoke(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}

fun TabLayout.observePosition(tabPosition: (Int) -> Unit) {
    this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            tabPosition.invoke(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    })
}

fun ImageView.setImageTint(color: Int) {
    this.setColorFilter(
        ContextCompat.getColor(context, color),
        android.graphics.PorterDuff.Mode.SRC_IN
    )
}

infix fun Double.of(amount: Double): Double {
    return (this.div(100)).times(amount)
}


fun Activity.getCurrentTimerTime(): String? {
    return SimpleDateFormat("yyyy/dd/MM HH:mm:ss").format(Date())
}

infix fun String.durationBetween(endTime: String): String {
    val simpleDateFormat = SimpleDateFormat("yyyy/dd/MM HH:mm:ss")
    val date1 = simpleDateFormat.parse(this)
    val date2 = simpleDateFormat.parse(endTime)

    val difference: Long = date2.time - date1.time
    val days = (difference / (1000 * 60 * 60 * 24)).toLong()
    val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
    val min = ((difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60))
    if (hours < 0) -hours else hours
    return String.format("%02d", min)
}


infix fun View.width(score: Float?): LinearLayout.LayoutParams? {
    if (score != null) {
        return LinearLayout.LayoutParams(0, 40, score)
    } else {
        val param = score?.let {
            LinearLayout.LayoutParams(0, 40, it)
        }
        return param
    }
}

fun RecyclerView.observer(endHasReached: (Boolean) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                endHasReached.invoke(true)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        }
    })
}


fun View.enabled() {
    this.isEnabled = true
}

fun Spinner.observer(name: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parentView: AdapterView<*>?,
            selectedItemView: View?,
            position: Int,
            id: Long
        ) {
            name.invoke(position)
        }

        override fun onNothingSelected(parentView: AdapterView<*>?) {
        }
    }
}


fun TextView.setHtmlText(text: String) {
    this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(text)
    }
}

fun Long.toDateTime(): String {
    val hours = this.div(60)
    val minutes = this % 60
    return String.format("%02d:%02d", hours, minutes)
}


//fun Long.millisToTime(): String {
//    val formatter = SimpleDateFormat(localDateTimeFormat, Locale.getDefault())
//    return formatter.format(Date(this))
//}

fun <T> Spinner.setAdapter(list: List<T>, resourceID: Int) {
    val adapter: ArrayAdapter<T> = ArrayAdapter<T>(context, resourceID, list)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter = adapter
}

@SuppressLint("HardwareIds")
fun Fragment.getUDID(): String {
    return Settings.Secure.getString(
        context?.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun SeekBar.observer(progress: (Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            p0?.progress?.let { progress.invoke(it) }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }

    })
}

//infix fun ArrayList<UploadDocsModel>.filter(type: String): Int {
//    return this.filter { it.type.equals(type, ignoreCase = true) }.count()
//}

//fun Activity.generate(key: String = "SHA256") {
//    try {
//        val info: PackageInfo = this.packageManager
//            .getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES)
//        for (signature in info.signatures) {
//            val md = MessageDigest.getInstance(key)
//            md.update(signature.toByteArray())
//            val digest: ByteArray = md.digest()
//            val toRet = StringBuilder()
//            for (i in digest.indices) {
//                if (i != 0) toRet.append(":")
//                val b: Byte = digest[i] and 0xff.toByte()
//                val hex = Integer.toHexString(b.toInt())
//                if (hex.length == 1) toRet.append("0")
//                toRet.append(hex)
//            }
////            Log.e(
////                "SHA", "$key $toRet"
////            )
//        }
//    } catch (e1: PackageManager.NameNotFoundException) {
////        Log.e("name not found", e1.toString())
//    } catch (e: NoSuchAlgorithmException) {
////        Log.e("no such an algorithm", e.toString())
//    } catch (e: java.lang.Exception) {
////        Log.e("exception", e.toString())
//    }
//}