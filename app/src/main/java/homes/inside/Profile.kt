package homes.inside

import SocialViewModel
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectfitness.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import database.ProjectFitnessContainer
import viewmodel.ViewModelProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavController , viewModelProfile: ViewModelProfile , socialViewModel: SocialViewModel) {

    //Firebase Database Creation*************************************************************************************************************************************************************
    val storageRef = Firebase.storage.reference
    val uid = Firebase.auth.currentUser?.uid
    Log.d("uid",uid.toString())
    var profileRef : StorageReference = storageRef.child("gs://projectfitness-ddfeb.appspot.com/profile_photos/$uid/profile.jpg")
    //******************************************************************************************************************************************************************************
    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository
    val itemsState by itemRepo.getAllCompletedWorkouts().collectAsState(initial = emptyList())
    val user by itemRepo.getProjectFitnessUser().collectAsState(initial = emptyList())
    val userPhotoUri = viewModelProfile.selectedImageUri.value
    //******************************************************************************************************************************************************************************
    //Image select options
    var isdialogVisible by remember{ mutableStateOf(false) }

    var selectedimageUri : Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { // Galeriden seçim ekranı
        uri: Uri? ->
        uri?.let {
            selectedimageUri = it
            profileRef.putFile(selectedimageUri!!)
            isdialogVisible = false}
    }
    if (isdialogVisible) {
        CustomDialogScreen(onDismiss = {isdialogVisible = false}, launcher = launcher)
    }
    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp
    val screenheightDp = configuration.screenHeightDp

    var listOfProfile = mutableListOf<String>("Age","Gender","Weight","Height")
    var listOfApp = mutableListOf<String>("Theme","Notification","Privacy")
    var listOfInt = mutableListOf<Int>(R.drawable.forwardmedia,R.drawable.wc,R.drawable.monitorweight,R.drawable.height)
    var listOfIntApp = mutableListOf<Int>(R.drawable.contrast,R.drawable.addalert,R.drawable.addmoderator)

    var themeSwitchState by remember { mutableStateOf(false) } // If false dark theme on
    Log.d("Firebases",viewModelProfile.selectedImageUri.value.toString())
    if (userPhotoUri != null) {
        if (userPhotoUri.isNotEmpty()){
            Log.d("Lol","1st selected")
            selectedimageUri = Uri.parse(userPhotoUri)
            viewModelProfile.imageBitmap.value = loadbitmapfromUri(uri = selectedimageUri!!, context = context , viewModelProfile)
        } else {
            Log.d("Lol","2st selected")
        }
    }

    val _user = com.google.firebase.ktx.Firebase.auth.currentUser
    val getAllUserState by socialViewModel.getAllUsers().observeAsState(initial = emptyList())
    val getFollowers by socialViewModel.getFollowers(_user?.uid ?: "").observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(_user?.uid ?: "").observeAsState(initial = emptyList())
    var numberOfFollows = getFollowing.size
    var numberOfFollowers = getFollowers.size
    Log.d("nof", numberOfFollows.toString())

    Box( // Ana arkaplan
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF1C40F), Color(0xFF181F26)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, screenheightDp * 1.4f.toFloat())
                )
            )

    )
    {
            Row(Modifier.align(Alignment.TopCenter)) {

                IconButton(
                    onClick = { navController.navigate("home") }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnessprevious),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp),
                        tint = Color(0xFF181F26)
                    )

                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "PROJECT FITNESS",
                    color = Color(0xFF181F26),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 25.sp, letterSpacing = 3.sp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnesscircleheavy),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                            .padding(end = 10.dp),
                        tint = Color(0xFF181F26)
                    )
                }
            }

        Column(modifier = Modifier
            .padding(top = 40.dp)
            .background(Color.Transparent)
            .fillMaxWidth()) {
            Text(text = "Profile", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombolight)), lineHeight = 55.sp),
                color = Color(0xFF000000),
                modifier = Modifier.padding(start = 25.dp),
            )
        }

        Column(
            Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .height(600.dp)
                .align(Alignment.BottomCenter)) {
            if (!viewModelProfile.selectedImageUri.value.isNullOrEmpty()) {
                viewModelProfile.selectedImageUri.value.let { url ->
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(136.dp)
                            .align(Alignment.CenterHorizontally)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                clip = false
                            )
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .crossfade(true)
                                .build(), contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .shadow(
                                    elevation = 50.dp,
                                    shape = CircleShape,
                                    clip = false
                                )
                                .clip(CircleShape)
                                .border(BorderStroke(1.dp, Color.White), CircleShape)
                                .clickable(onClick = { isdialogVisible = true }),
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            } else {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(136.dp)
                        .align(Alignment.CenterHorizontally)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            clip = false
                        )
                        .clip(CircleShape)
                ){
                    Image(
                        painterResource(id = R.drawable.secondinfo) , contentDescription = null,
                        modifier = Modifier
                            .size(130.dp)
                            .shadow(
                                elevation = 50.dp,
                                shape = CircleShape,
                                clip = false
                            )
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, Color.White), CircleShape)
                            .clickable(onClick = { isdialogVisible = true }),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.size(30.dp))
            user.forEachIndexed{index , item ->
                Text(
                    text = item.userName,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    color = Color(0xFFD9D9D9),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    fontSize = 20.sp,
                    style = TextStyle(letterSpacing = 3.sp)
                )
                Text(
                    text = "@"+item.nickName,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                    fontSize = 20.sp,
                    style = TextStyle(letterSpacing = 3.sp)
                )
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)) {
                Text(
                    text = "Followers $numberOfFollowers",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(bottom = 10.dp, start = 40.dp)
                        .clickable(onClick = { navController.navigate("projectfollowersscreen") }),
                    color = Color(0xFFD9D9D9),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                    fontSize = 20.sp,
                    style = TextStyle(letterSpacing = 3.sp) ,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Follows $numberOfFollows" ,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(bottom = 30.dp, end = 40.dp)
                        .clickable(onClick = { navController.navigate("projectfollowscreen") }),
                    color = Color(0xFFD9D9D9),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                    fontSize = 20.sp,
                    style = TextStyle(letterSpacing = 3.sp)
                )
            }

        var tabTitles = listOf("Activity","Profile","App")
        var selectedTabIndex by remember { mutableStateOf(0) }
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF181F26),
                contentColor = Color.White,
                indicator = {
                    // TabRowDefaults.SecondaryIndicator(color = Color.Cyan)
                    TabRowDefaults.SecondaryIndicator(color = Color(0xFFF1C40F), height = 1.5f.dp, modifier = Modifier.tabIndicatorOffset(selectedTabIndex))
                }) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            if (selectedTabIndex == index){
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                fontSize = 15.sp,
                                letterSpacing = 3.sp),
                                color = Color(0xFFF1C40F))
                            }
                            else {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        fontSize = 15.sp,
                                        letterSpacing = 3.sp),
                                    color = Color.White)
                            }
                        }
                    )
                }
            }
            if (selectedTabIndex == 0) {

                if (itemsState.isEmpty()){
                    Text(
                        text = "No workout history yet. Start your first workout today!",
                        fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 1.sp, fontSize = 15.sp),
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(top = 100.dp)
                            .align(Alignment.CenterHorizontally))}

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 30.dp),
                    state = LazyListState()
                ) {
                    itemsIndexed(itemsState) { index, item ->
                        Box(
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp)
                                .height(93.dp)
                                .fillMaxHeight()
                                .border(
                                    1.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF202B36)
                                )
                        )
                        {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 5.dp)
                                    .width(80.dp)
                                    .height(80.dp)
                                    .clip(shape = RoundedCornerShape(20.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xFF181F26)  // End color
                                            ),
                                            start = Offset.Infinite,
                                            end = Offset(100.0f, 0.0f)
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )

                            )
                            {
                                if (index == 0) {
                                    Image(
                                        painterResource(id = R.drawable.secondinfo),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                } else if (index == 1) {
                                    Image(
                                        painterResource(id = R.drawable.login),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                }
                                else if (index == 2) {
                                    Image(
                                        painterResource(id = R.drawable.gym),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                }
                                else if (index == 3) {
                                    Image(
                                        painterResource(id = R.drawable.gymroomwith),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                }
                                else if (index == 4) {
                                    Image(
                                        painterResource(id = R.drawable.gymroomwithgym),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                }
                                else if (index == 5) {
                                    Image(
                                        painterResource(id = R.drawable.gymroomgym),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                }
                                else if (index == 6) {
                                    Image(
                                        painterResource(id = R.drawable.login),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(end = 15.dp)
                            ) {
                                Row(modifier = Modifier) {
                                    Text(
                                        text = item.completionDate,
                                        modifier = Modifier
                                            .padding(start = 100.dp , top = 7.dp),
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        textAlign = TextAlign.Left,
                                        color = Color(0xFFD9D9D9),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier) {
                                    Text(
                                        text = item.completedWorkoutName.uppercase(),
                                        modifier = Modifier
                                            .padding(start = 100.dp , top = 0.dp),
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        textAlign = TextAlign.Left,
                                        color = Color(0xFFD9D9D9),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(painter = painterResource(id = R.drawable.keyboarddoublearrowright),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(top = 0.dp)
                                            .size(25.dp)
                                            .clickable {
                                                viewModelProfile.selectedCompletedWorkoutId.value =
                                                    item.workoutId
                                                viewModelProfile.selectedCompletedId.value =
                                                    item.completedWorkoutId
                                                navController.navigate("historyexercisescreen")
                                            }
                                    )
                                }
                                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                                    Text(
                                        text = "3 exercise",
                                        modifier = Modifier
                                            .padding(start = 100.dp),
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        textAlign = TextAlign.Left,
                                        color = Color(0xFFD9D9D9),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Completed in " + item.durationMinutes,
                                        modifier = Modifier
                                            .padding(start = 0.dp),
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        textAlign = TextAlign.Left,
                                        color = Color(0xFFD9D9D9),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }

            }
            else if (selectedTabIndex == 1) {

                LazyColumn(modifier = Modifier
                    .background(Color.Transparent)
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)) {
                    itemsIndexed(listOfProfile){ index,item ->
                        Column(modifier = Modifier
                            .height(34.dp)
                            .width(341.dp)
                            .background(Color.Transparent, shape = RoundedCornerShape(7.dp))
                            .align(Alignment.CenterHorizontally)
                            .border(
                                1.dp,
                                shape = RoundedCornerShape(7.dp),
                                color = Color(0xFF202B36)
                            )){

                            Row {
                                Spacer(modifier = Modifier.size(10.dp))
                                Icon(painter = painterResource(id = listOfInt[index]), contentDescription = null ,
                                    Modifier
                                        .padding(top = 1.dp)
                                        .size(30.dp),
                                    tint = Color(0xFF202B36) )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(text = item, style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 3.sp,
                                    fontSize = 15.sp,
                                    color = Color.White),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically))
                                Spacer(modifier = Modifier.weight(1f))
                                Button(onClick = {  }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202B36)),
                                    shape = RoundedCornerShape(5.dp),
                                    modifier = Modifier
                                        .padding(end = 20.dp)
                                        .height(21.dp)
                                        .width(59.dp)
                                        .align(Alignment.CenterVertically),
                                    contentPadding = PaddingValues(0.dp)
                                        ) {
                                    Text(text = "Change",
                                        style = TextStyle(fontSize = 10.sp),
                                        color = Color(0xFFF1C40F))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
            else if (selectedTabIndex == 2) {
                LazyColumn(modifier = Modifier
                    .background(Color.Transparent)
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)) {
                    itemsIndexed(listOfApp){ index,item ->
                        Column(modifier = Modifier
                            .height(34.dp)
                            .width(341.dp)
                            .background(Color.Transparent, shape = RoundedCornerShape(7.dp))
                            .align(Alignment.CenterHorizontally)
                            .border(
                                1.dp,
                                shape = RoundedCornerShape(7.dp),
                                color = Color(0xFF202B36)
                            )){

                            Row {
                                Spacer(modifier = Modifier.size(10.dp))
                                Icon(painter = painterResource(id = listOfIntApp[index]), contentDescription = null ,
                                    Modifier
                                        .padding(top = 1.dp)
                                        .size(30.dp),
                                    tint = Color(0xFF202B36))
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(text = item, style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 3.sp,
                                    fontSize = 15.sp,
                                    color = Color.White),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically))
                                Spacer(modifier = Modifier.weight(1f))
                                if (index == 0){
                                    Text(text = "Dark", style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        letterSpacing = 2.sp,
                                        fontSize = 10.sp,
                                        color = Color.White),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically))
                                    Switch(checked = themeSwitchState, onCheckedChange = { themeSwitchState = !themeSwitchState },
                                        modifier = Modifier.scale(0.5f)
                                    )
                                    Text(text = "Day", style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        letterSpacing = 2.sp,
                                        fontSize = 10.sp,
                                        color = Color.White),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 15.dp))
                                }
                                else{
                                    //
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
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


fun loadbitmapfromUri(uri:Uri,context:Context,viewModelProfile: ViewModelProfile) : ImageBitmap?{
    val contentResolver = context.contentResolver
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()

    } catch (e:Exception){
        viewModelProfile.imageBitmap.value
    }
}