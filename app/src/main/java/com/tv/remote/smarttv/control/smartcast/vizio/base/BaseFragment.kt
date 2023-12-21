package tvremote.forroku.smart.tvremotecontrol.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.tv.remote.smarttv.control.smartcast.vizio.R
import tvremote.forroku.smart.tvremotecontrol.db.SessionManager

abstract class BaseFragment<layoutId : ViewBinding> : Fragment() {

    lateinit var binding: layoutId
    abstract fun getViewBinding(): layoutId
    private var fragment: Fragment? = null
    var sessionManager: SessionManager? = null


    protected var permissionArray: Array<String>? = null;



    protected  fun adLoadingDialog(adDialog: (Dialog) -> Unit){
        val dialog = Dialog(requireActivity())
        try {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        } catch (ne: Exception) {
            ne.printStackTrace()
        }
//        dialog.setContentView(R.layout.ad_loading_dialog)
        dialog.setCancelable(false)
        adDialog.invoke(dialog)
    }




//    fun disconnectDialog(boolean: (Boolean) -> Unit){
//        val dialoge = Dialog(requireActivity())
//        try {
//            dialoge.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialoge.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        dialoge.setContentView(R.layout.exit_dialog)
//        dialoge.show()
//        dialoge.findViewById<ImageView>(R.id.imageChange).loadImage(R.drawable.castsamsung , requireActivity())
//        dialoge.findViewById<TextView>(R.id.textChange).text= "Are you sure you want to Disconnect this Cast/Remote Device?"
//        dialoge.findViewById<MaterialCardView>(R.id.yesBtn).setOnClickListener {
//            dialoge.dismiss()
//            boolean.invoke(true)
//        }
//        dialoge.findViewById<MaterialCardView>(R.id.noBtn).setOnClickListener {
//            dialoge.dismiss()
//        }
//
//    }


//    protected fun showProgressDialog(dialog: (Dialog) -> Unit) {
//        val wifiDialog = Dialog(requireActivity())
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.progress_dialog)
//        dialog.invoke(wifiDialog)
//    }


    protected fun showProgressDialog(dialog: (Dialog) -> Unit) {
        val wifiDialog = Dialog(requireActivity())
        try {
            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (ne: Exception) {
            ne.printStackTrace()
        }
        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        wifiDialog.setContentView(R.layout.progress_dialog)
        dialog.invoke(wifiDialog)
    }




//    protected fun showDialog(activity: Activity, dialog: (Dialog) -> Unit) {
//        val wifiDialog = Dialog(activity)
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.filter_lodaing)
//        dialog.invoke(wifiDialog)
//    }

    protected fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }
//    protected fun openOptionDialog() {
//        val dialog = OptionDialog.getInstance()
//        if (childFragmentManager.findFragmentByTag(OptionDialog.NAME) != null) {
//            ((childFragmentManager.findFragmentByTag(OptionDialog.NAME) as OptionDialog).dismiss())
//        }
//        try {
//            dialog.show(childFragmentManager, OptionDialog.NAME)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (dialog.isAdded)
//                dialog.dismiss()
//        }
//    }

//    protected fun connectWifi() {
//        val wifiDialog = Dialog(requireActivity())
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.wifi_dialog)
//        wifiDialog.findViewById<MaterialCardView>(R.id.yesBtn).setOnClickListener {
//            wifiDialog.dismiss()
//        }
//        wifiDialog.findViewById<MaterialCardView>(R.id.noBtn).setOnClickListener {
//            try {
//                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
//                wifiDialog.dismiss()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        wifiDialog.show()
//    }



//
//    protected fun connectedWifi() {
//        val wifiDialog = Dialog(requireActivity())
//
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.setContentView(R.layout.wifi_notconnected_dialge)
//        wifiDialog.findViewById<MaterialCardView>(R.id.wifiOpen).setOnClickListener {
//            try {
//                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
//                wifiDialog.dismiss()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        wifiDialog.show()
//    }


    fun goToNext(activity: Class<out Activity>) {
        requireActivity().startActivity(Intent(requireActivity(), activity))
    }
    fun goToNextWithData(activity: Class<out Activity>, position: Int) {
        val intent = Intent(requireActivity(), activity)
        intent.putExtra("pos", position)
        startActivity(intent)
    }
    fun goToNextWithFinish(activity: Class<out Activity>) {
        requireActivity().startActivity(Intent(requireActivity(), activity))
        requireActivity().finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireActivity())
        attachBaseViewModel()
    }

//    protected fun disconnectRemote(value: (Dialog, DisconnectRemoteBinding) -> Unit) {
//        val disconectDialog = Dialog(requireContext())
//        try {
//            disconectDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            disconectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//
//        val dialogBinding = DisconnectRemoteBinding.inflate(layoutInflater)
//        disconectDialog.setContentView(dialogBinding.root)
//        value.invoke(disconectDialog, dialogBinding)
//    }
//
//    protected fun buttonClick(value: (Dialog, IrButtonWorkingBinding) -> Unit) {
//        val disconectDialog = Dialog(requireContext())
//        try {
//            disconectDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            disconectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//
//        val dialogBinding = IrButtonWorkingBinding.inflate(layoutInflater)
//        disconectDialog.setContentView(dialogBinding.root)
//        value.invoke(disconectDialog, dialogBinding)
//    }


//    protected fun irNotSupport(activity: Activity , dialog: (Dialog) -> Unit) {
//        val irDialog = Dialog(activity)
//        try {
//            irDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        irDialog.setContentView(R.layout.ir_not_support)
//        val button = irDialog.findViewById<MaterialButton>(R.id.btnClose)
//        button.setOnClickListener {
//            irDialog.dismiss()
//        }
//        dialog.invoke(irDialog)
//    }


    private fun attachBaseViewModel() {

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionArray = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        initViews()
        clicks()
        attachViewModel()

//        if (activity is BaseActivity<*>) {
//            fragment = this
//            (activity as BaseActivity<*>).fragment = fragment
//        }

    }

//    fun navigateFragment(int: NavDirections) {
//        findNavController().navigate(int)
//    }


    abstract fun initViews()
    abstract fun clicks()
    abstract fun attachViewModel()


}