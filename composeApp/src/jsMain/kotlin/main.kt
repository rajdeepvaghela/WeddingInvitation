import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.rdapps.weddinginvitation.App
import com.rdapps.weddinginvitation.model.DeviceInfo
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        CanvasBasedWindow("Wedding Invitation") {
            Napier.base(DebugAntilog("Wedding Invitation"))

            document.title = "કંકોત્રી - Wedding Invitation"

            val deviceInfo = with(window.navigator) {
                DeviceInfo(
                    userAgent = userAgent,
                    vendor = "$vendor $vendorSub",
                    platform = platform
                )
            }

            App(window.location.hash, deviceInfo)
        }
    }
}
