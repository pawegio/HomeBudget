package com.pawegio.homebudget

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.pawegio.homebudget.main.transaction.TransactionAction
import com.pawegio.homebudget.picker.PickerAction
import com.pawegio.homebudget.util.CurrentActivityObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private var pendingActivityResult: Continuation<ActivityResult>? = null
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        pendingActivityResult?.also {
            pendingActivityResult = null
            it.resume(result)
        }
    }

    suspend fun startForResult(intent: Intent): ActivityResult = suspendCancellableCoroutine { continuation ->
        check(pendingActivityResult == null) { "Another activity result is pending" }
        pendingActivityResult = continuation
        continuation.invokeOnCancellation { pendingActivityResult = null }
        activityResultLauncher.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val root = findViewById<View>(R.id.rootContainer)
            ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
                val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                val safeArea = insets.getInsets(types)
                view.setPadding(safeArea.left, safeArea.top, safeArea.right, safeArea.bottom)
                WindowInsetsCompat.Builder(insets).setInsets(types, Insets.NONE).build()
            }
            ViewCompat.requestApplyInsets(root)
        }
        lifecycle.addObserver(CurrentActivityObserver)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        appNavController = navHostFragment.navController.also(::createNavGraph)
    }
}

@SuppressLint("StaticFieldLeak")
var appNavController: NavController? = null
