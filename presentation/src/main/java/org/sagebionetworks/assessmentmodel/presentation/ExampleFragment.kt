import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { // In here, we can call composables!
            MaterialTheme {
                Greeting(name = "compose")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text (text = "Hello $name!")
}
//
//@Preview
//@Composable
//fun ComposablePreview() {
//    Greeting("Joshua")
//}