package ui.mainpages.inside

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.shadow
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
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProfileUiState
import viewmodel.ProfileViewModel
import viewmodel.ViewModelProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    navController: NavController,
    viewModelProfile: ViewModelProfile,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {

    //Firebase *************************************************************************************************************************************************************
    val uid = Firebase.auth.currentUser?.uid ?: return
    val profileState = profileViewModel.profileState.collectAsState().value

    LaunchedEffect(uid) {
        profileViewModel.load(uid)
    }

    val launcherProfile =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) profileViewModel.changePhoto(uid, uri = uri)
        }
    //******************************************************************************************************************************************************************************
    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current

    //Image select options

    val _user = com.google.firebase.ktx.Firebase.auth.currentUser
    val getFollowers by socialViewModel.getFollowers(_user?.uid ?: "")
        .observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(_user?.uid ?: "")
        .observeAsState(initial = emptyList())
    var numberOfFollows = getFollowing.size
    var numberOfFollowers = getFollowers.size

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarProfile(navController)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {},
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        var tabTitles = listOf("History", "Stats", "App")
        var selectedTabIndex by remember { mutableStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            when (profileState) {
                is ProfileUiState.Loading -> { /* loading */
                }

                is ProfileUiState.Error -> { /* toast/text */
                }

                is ProfileUiState.Ready -> {
                    val profile = (profileState as ProfileUiState.Ready).profile
                    Log.d("Profil bilgileri:", profile.toString())

                    AsyncImage(
                        model = profile.userPhotoUri.ifEmpty { R.drawable.secondinfo },
                        contentDescription = null,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(2.dp, Color(0xFFF1C40F)), CircleShape)
                            .clickable { launcherProfile.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.size(25.dp))
                    Text(profile.first,
                        modifier = Modifier
                            .padding(bottom = 10.dp),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 20.sp,
                        style = TextStyle(letterSpacing = 1.sp))
                    Text("@${profile.nickname}",
                        modifier = Modifier
                            .padding(bottom = 10.dp),
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 20.sp,
                        style = TextStyle(letterSpacing = 1.sp))
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Tüm genişliği kaplasın
                    .height(80.dp) // Yüksekliği biraz artırdım, tıklama alanı rahat olsun
                    .padding(horizontal = 20.dp), // Kenarlardan boşluk
                horizontalArrangement = Arrangement.SpaceEvenly, // Öğeleri eşit aralıklarla dağıt
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- FOLLOWERS KISMI ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, // 🔥 İŞTE SİHİRLİ KOD BURASI
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("projectfollowersscreen") })
                        // Tıklama efektinin (ripple) güzel görünmesi için clip ekleyebilirsin
                        .padding(8.dp) // Tıklama alanı biraz genişlesin
                ) {
                    Text(
                        text = "$numberOfFollowers",
                        color = Color(0xFFD9D9D9),
                        fontFamily = FontFamily(Font(R.font.lexendbold)), // Sayıyı biraz daha kalın yapabilirsin
                        fontSize = 22.sp,
                        style = TextStyle(letterSpacing = 1.sp),
                        // Padding YOK! Column kendisi ortalayacak.
                    )
                    Text(
                        text = "Followers",
                        color = Color.Gray, // Başlığı biraz daha sönük yaparak hiyerarşi kurabilirsin
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 14.sp,
                        style = TextStyle(letterSpacing = 0.5.sp),
                    )
                }

                // Araya dikey çizgi (Divider) istersen:
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.DarkGray))

                // --- FOLLOWING KISMI ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, // 🔥 OTOMATİK ORTALAMA
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("projectfollowersscreen") })
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$numberOfFollows",
                        color = Color(0xFFD9D9D9),
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 22.sp,
                        style = TextStyle(letterSpacing = 1.sp),
                    )
                    Text(
                        text = "Following",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 14.sp,
                        style = TextStyle(letterSpacing = 0.5.sp),
                    )
                }
            }
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF121417),
                contentColor = Color.White,
                indicator = {
                    // TabRowDefaults.SecondaryIndicator(color = Color.Cyan)
                    TabRowDefaults.SecondaryIndicator(
                        color = Color(0xFFF1C40F),
                        height = 1.5f.dp,
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex)
                    )
                }) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            if (selectedTabIndex == index) {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                                        fontSize = 15.sp,
                                        letterSpacing = 0.sp
                                    ),
                                    color = Color(0xFFF1C40F)
                                )
                            } else {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                                        fontSize = 15.sp,
                                        letterSpacing = 0.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    )
                }
            }
            if (selectedTabIndex == 0) {

                /*if (itemsState.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Workout History",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                            color = Color.White.copy(alpha = 1f),
                            modifier = Modifier
                                .padding(horizontal = 50.dp, vertical = 40.dp)
                        )
                        Text(
                            text = "No workout history yet. Start your first workout today!",
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 1.sp, fontSize = 15.sp),
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .padding(horizontal = 50.dp, vertical = 50.dp)
                        )
                        Spacer(Modifier.size(40.dp))
                        Button(
                            onClick = { navController.navigate("activity") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF202B36).copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .width(350.dp),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Text(
                                text = "Start your first workout",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold))
                                ),
                                color = Color(0xFFF1C40F)
                            )
                        }
                    }

                }*/
            }
        }
        /*Box( // Ana arkaplan
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
                                } else if (index == 2) {
                                    Image(
                                        painterResource(id = R.drawable.gym),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                } else if (index == 3) {
                                    Image(
                                        painterResource(id = R.drawable.gymroomwith),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                } else if (index == 4) {
                                    Image(
                                        painterResource(id = R.drawable.gymroomwithgym),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                } else if (index == 5) {
                                    Image(
                                        painterResource(id = R.drawable.gymroomgym),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    )
                                } else if (index == 6) {
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
                                            .padding(start = 100.dp, top = 7.dp),
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                        textAlign = TextAlign.Left,
                                        color = Color(0xFFD9D9D9),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier) {
                                    Text(
                                        text = item.completedWorkoutName.uppercase(),
                                        modifier = Modifier
                                            .padding(start = 100.dp, top = 0.dp),
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
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
                                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
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
                                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
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

            } else if (selectedTabIndex == 1) {

                LazyColumn(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .align(Alignment.CenterHorizontally)
                        .padding(20.dp)
                ) {
                    itemsIndexed(listOfProfile) { index, item ->
                        Column(
                            modifier = Modifier
                                .height(34.dp)
                                .width(341.dp)
                                .background(Color.Transparent, shape = RoundedCornerShape(7.dp))
                                .align(Alignment.CenterHorizontally)
                                .border(
                                    1.dp,
                                    shape = RoundedCornerShape(7.dp),
                                    color = Color(0xFF202B36)
                                )
                        ) {

                            Row {
                                Spacer(modifier = Modifier.size(10.dp))
                                Icon(
                                    painter = painterResource(id = listOfInt[index]),
                                    contentDescription = null,
                                    Modifier
                                        .padding(top = 1.dp)
                                        .size(30.dp),
                                    tint = Color(0xFF202B36)
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    text = item, style = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                        letterSpacing = 3.sp,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF202B36)
                                    ),
                                    shape = RoundedCornerShape(5.dp),
                                    modifier = Modifier
                                        .padding(end = 20.dp)
                                        .height(21.dp)
                                        .width(59.dp)
                                        .align(Alignment.CenterVertically),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "Change",
                                        style = TextStyle(fontSize = 10.sp),
                                        color = Color(0xFFF1C40F)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            } else if (selectedTabIndex == 2) {
                LazyColumn(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .align(Alignment.CenterHorizontally)
                        .padding(20.dp)
                ) {
                    itemsIndexed(listOfApp) { index, item ->
                        Column(
                            modifier = Modifier
                                .height(34.dp)
                                .width(341.dp)
                                .background(Color.Transparent, shape = RoundedCornerShape(7.dp))
                                .align(Alignment.CenterHorizontally)
                                .border(
                                    1.dp,
                                    shape = RoundedCornerShape(7.dp),
                                    color = Color(0xFF202B36)
                                )
                        ) {

                            Row {
                                Spacer(modifier = Modifier.size(10.dp))
                                Icon(
                                    painter = painterResource(id = listOfIntApp[index]),
                                    contentDescription = null,
                                    Modifier
                                        .padding(top = 1.dp)
                                        .size(30.dp),
                                    tint = Color(0xFF202B36)
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    text = item, style = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                        letterSpacing = 3.sp,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (index == 0) {
                                    Text(
                                        text = "Dark", style = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                            letterSpacing = 2.sp,
                                            fontSize = 10.sp,
                                            color = Color.White
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )
                                    Switch(
                                        checked = themeSwitchState,
                                        onCheckedChange = { themeSwitchState = !themeSwitchState },
                                        modifier = Modifier.scale(0.5f)
                                    )
                                    Text(
                                        text = "Day", style = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                            letterSpacing = 2.sp,
                                            fontSize = 10.sp,
                                            color = Color.White
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 15.dp)
                                    )
                                } else {
                                    //
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }


        }

    }*/
    }

    @Composable
    fun CustomDialogScreen(
        onDismiss: () -> Unit,
        launcher: ManagedActivityResultLauncher<String, Uri?>,
    ) {
        var text by remember { mutableStateOf("") }
        Log.d("TAG", "visible")
        Dialog(onDismissRequest = { onDismiss.invoke() }) {
            Box(
                modifier = Modifier
                    .padding(top = 300.dp)
                    .background(
                        Color(0xFFD9D9D9).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth()
                    .height(70.dp)
            )
            {
                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
                {
                    Text(
                        text = "Open Gallery",
                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


fun loadbitmapfromUri(
    uri: Uri,
    context: Context,
    viewModelProfile: ViewModelProfile,
): ImageBitmap? {
    val contentResolver = context.contentResolver
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()

    } catch (e: Exception) {
        viewModelProfile.imageBitmap.value
    }
}

@Composable
fun  HomeTopBarProfile(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate(Screens.Home.route) { popUpTo("profile") { inclusive = true } } }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text("Profile",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.lexendbold)),
            fontSize = 30.sp)

        Spacer(Modifier.weight(1f))

        IconButton(
            enabled = false,
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }

    }
}
