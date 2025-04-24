package burak.android.dailyinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import burak.android.dailyinfo.currency.CurrencyScreen
import burak.android.dailyinfo.ui.theme.DailyInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyInfoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CurrencyScreen()
                }
            }
        }
    }
}
