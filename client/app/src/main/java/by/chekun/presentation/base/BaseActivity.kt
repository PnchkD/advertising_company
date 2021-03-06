package by.chekun.presentation.base


import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import by.chekun.App
import by.chekun.R
import by.chekun.di.component.ViewModelComponent
import by.chekun.domain.UserViewModel
import by.chekun.presentation.activities.add.AddAdvertisementActivity
import by.chekun.presentation.activities.login.LoginActivity
import by.chekun.presentation.activities.login.RegistrationActivity
import by.chekun.presentation.activities.main.MainActivity
import by.chekun.presentation.activities.settings.SettingsActivity
import by.chekun.presentation.activities.user.ProfileActivity
import by.chekun.repository.database.entity.User
import by.chekun.utils.hideKeyboardEx
import java.util.*
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    protected open val PERMISSION_REQUEST = 5

    open var arrayPermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    open lateinit var messageRationalePermission: String
    open lateinit var messageNecessaryPermissions: String
    protected var requestCode: Int? = null
    private var mToolbar: Toolbar? = null

    var viewModel: UserViewModel? = null
        @Inject set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createDaggerDependencies()

    }


    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //handle presses on the action bar items
        when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            R.id.action_add_car -> {
                startActivity(Intent(this, AddAdvertisementActivity::class.java))
                return true
            }
            R.id.action_registration -> {
                startActivity(Intent(this, RegistrationActivity::class.java))
                return true
            }
            R.id.action_login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            R.id.action_logout -> {
                viewModel?.deleteAll()
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
//        val user: User? = viewModel?.getCurrentUser()
//
//        if(user != null) {
//            val profileItem: MenuItem = menu!!.findItem(R.id.action_profile);
//            val loginItem: MenuItem = menu.findItem(R.id.action_login);
//            val addAdvItem: MenuItem = menu!!.findItem(R.id.action_add_car);
//            val settingsItem: MenuItem = menu.findItem(R.id.action_settings);
//        } else {
//            val registrationItem: MenuItem = menu!!.findItem(R.id.action_registration);
//            val loginItem: MenuItem = menu.findItem(R.id.action_login);
//            val addAdvItem: MenuItem = menu!!.findItem(R.id.action_add_car);
//            val settingsItem: MenuItem = menu.findItem(R.id.action_settings);
//        }
        //Action View
         //Configure the search info and add any event listeners
        //return super.onCreateOptionsMenu(menu);
        return true
    }





    protected fun getToolbar(): Toolbar? = mToolbar

    protected fun setToolbarTitle(title: CharSequence) {
        mToolbar?.title = title
    }

    private fun findViewAt(viewGroup: ViewGroup, x: Int, y: Int): View? {
        (0 until viewGroup.childCount)
                .map { viewGroup.getChildAt(it) }
                .forEach {
                    when (it) {
                        is ViewGroup -> {
                            val foundView = findViewAt(it, x, y)
                            if (foundView?.isShown!!) return foundView
                        }
                        else -> {
                            val location = IntArray(2)
                            it.getLocationOnScreen(location)
                            val rect = Rect(location[0], location[1], location[0] + it.width, location[1] + it.height)
                            if (rect.contains(x, y)) return it
                        }
                    }
                }
        return null
    }

    private fun hideKeyboard() = this.hideKeyboardEx()

    @TargetApi(Build.VERSION_CODES.M)
    fun isStoragePermissionGranted(requestCode: Int = 0): Boolean {
        this.requestCode = requestCode
        if (checkPermissionList()) {
            return true
        } else {
            requestPermission()
            return false
        }
    }

    fun checkPermissionList(): Boolean {
        val list = ArrayList<Boolean>()
        arrayPermission.forEach {
            list.add(ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED)
        }
        return list.all { it }
    }

    open fun openNeeded??ction(requestCodeIntent: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, requestCodeIntent)
    }

    fun checkPermissionRationaleList(): Boolean {
        val list = ArrayList<Boolean>()
        arrayPermission.forEach {
            list.add(ActivityCompat.shouldShowRequestPermissionRationale(this, it))
        }
        return list.all { it }
    }

    fun showNoGalleryPermission() {
        openApplicationSettings()
        Toast.makeText(this, messageNecessaryPermissions, Toast.LENGTH_LONG).show()
    }

    fun openApplicationSettings() {
        val appSettingsIntent = Intent(
                ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName"))
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST)
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayPermission,
                PERMISSION_REQUEST)
    }

    fun replaceFragment(resLayout: Int, fragment: BaseFragment) {

    }

    protected abstract fun injectDependency(component: ViewModelComponent)

    private fun createDaggerDependencies() {
        injectDependency((application as App).getViewModelComponent())
    }
}