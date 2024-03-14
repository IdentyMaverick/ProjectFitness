package homes.inside

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import navigation.Screens

@Composable
fun Profile(navController: NavController) {
    //Image select options
    var imageBitmap : ImageBitmap? by remember { mutableStateOf(null) }
    val updatedImageBitmap = rememberUpdatedState(imageBitmap)
    var selectedimageUri : Uri? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedimageUri = it }
    }

    var isdialogVisible by remember{ mutableStateOf(false) }
    if (isdialogVisible) {
        CustomDialogScreen(onDismiss = {isdialogVisible = false}, launcher = launcher)
    }

    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp
    val screenheightDp = configuration.screenHeightDp


    if (selectedimageUri != null){
        imageBitmap = loadbitmapfromUri(uri = selectedimageUri!!, context = context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            text = "PROJECT FITNESS",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            color = Color(0xFFF1C40F),
            style = TextStyle(fontSize = 20.sp,letterSpacing = 10.sp)
        )
        Icon(
            painterResource(id = R.drawable.left),
            contentDescription = null,
            tint = Color(0xFFD9D9D9),
            modifier = Modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = { navController.navigate(Screens.Home.route) })
                .size(30.dp)
                .padding(top = 5.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .background(Color(0xFF181F26))
            .align(Alignment.Center))
        {
            Text(
                text = "Profile",
                fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                fontSize = 17.sp,
                color = Color(0xFFD9D9D9),
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .align(Alignment.TopCenter)
            )
            Column(modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 300.dp)
                .fillMaxWidth()) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(170.dp)
                        .background(Color.Transparent, shape = CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            onClick = { isdialogVisible = true },
                            enabled = true
                        )
                ) {
                    if (updatedImageBitmap.value != null){
                        drawImage(image = updatedImageBitmap.value!!, dstOffset = IntOffset.Zero)
                    }
                    else{drawCircle(Color.Black,200f)}
                }

            }
            Text(
                text = "Osman Deniz Savaş",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 70.dp),
                color = Color(0xFFD9D9D9),
                fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                fontSize = 17.sp,
            )
            Text(
                text = "@maverick",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 20.dp),
                color = Color(0xFFD9D9D9),
                fontFamily = FontFamily(Font(R.font.poppinsextralighttext)),
                fontSize = 15.sp,
            )
            Text(
                text = "Friends \n     0 ",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 60.dp, end = (screenwidthDp / 2).dp),
                color = Color(0xFFD9D9D9),
                fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                fontSize = 15.sp,
            )
            Canvas(modifier = Modifier.align(Alignment.Center)) {
                drawLine(color = (Color(0xFFF1C40F)), start = Offset(0f, 20f), end = Offset(0f, 100f))
            }
            Text(
                text = "Workouts \n        0 ",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 60.dp, start = (screenwidthDp / 2).dp),
                color = Color(0xFFD9D9D9),
                fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                fontSize = 15.sp,
            )
            Canvas(modifier = Modifier.padding(top = 270.dp)) {
                drawLine(color = Color(0xFFF1C40F), start = Offset(0f, 170f), end = Offset(screenwidthDp*3.toFloat(), 170f))
            }
            OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 200.dp), shape = RoundedCornerShape(15.dp)) {
                Text(text = "Edit Profile", color = Color(0xFFD9D9D9), fontFamily = FontFamily(Font(
                    R.font.poppinslighttext
                )))
            }
        }
    }
}
@Composable
fun CustomDialogScreen(onDismiss : () -> Unit,launcher : ManagedActivityResultLauncher<String,Uri?>){
    var text by remember{ mutableStateOf("") }
    Log.d("TAG","visible")
    Dialog(onDismissRequest = {onDismiss.invoke()}) {
        Box(modifier = Modifier
            .padding(top = 300.dp)
            .background(
                Color(0xFFD9D9D9).copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .height(70.dp))
        {
            Button(onClick = {launcher.launch("image/*")},
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp))
            {
                Text(text = "Open Gallery",
                    fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                    color = Color.Black,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun loadbitmapfromUri(uri:Uri,context:Context) : ImageBitmap?{
    val contentResolver = context.contentResolver
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    } catch (e:Exception){
        null
    }
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=900,unit=dp,dpi=402")
@Composable
fun PreviewProfile() {
    Profile(navController = rememberNavController())
}