package activity.inside

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import data.local.viewmodel.ActivityInsideViewModel

@Composable
fun ActivityInside(navController: NavController, activityInsideViewModel: ActivityInsideViewModel) {
    val selectedCatalog by activityInsideViewModel.selectedCatalog.collectAsState()
    val storageRef = Firebase.storage.reference.child("cloudgoogle/${selectedCatalog.gifUrl}")
    var gifUrl by remember { mutableStateOf<String?>(null) }
    val steps = selectedCatalog.instructions.split(".")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    val verticalScroll = rememberScrollState()

    LaunchedEffect(selectedCatalog.gifUrl) {
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            gifUrl = uri.toString()
        }.addOnFailureListener {
            Log.e("FirebaseStorage", "Error getting download URL", it)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ActivityInsideTopBar(navController, selectedCatalog)
        },
        containerColor = Color(0xFF121417),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(verticalScroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(20.dp))
            if (!gifUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = gifUrl ?: R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml,
                        contentDescription = null,
                        modifier = Modifier
                            .height(150.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        alpha = 0.8f
                    )
                    Text(
                        text = selectedCatalog.level,
                        color = Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 15.dp)
                            .background(Color(0xFFF1C40F), shape = RoundedCornerShape(5.dp))
                            .padding(5.dp)
                            .align(Alignment.BottomStart),
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    )
                }
            } else CircularProgressIndicator(color = Color(0xFFF1C40F))
            Tabs(selectedCatalog, steps)
        }
    }
}

@Composable
fun ActivityInsideTopBar(navController: NavController, selectedCatalog: ExerciseCatalogEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = selectedCatalog.name,
                color = Color.White,
                fontSize = 24.sp,
                letterSpacing = 0.sp,
                fontFamily = FontFamily(Font(R.font.oswaldbold))
            )
            Text(
                text = selectedCatalog.bodyPart + " · " + selectedCatalog.movementType,
                color = Color(0xFFF1C40F),
                fontSize = 12.sp,
                letterSpacing = 0.sp,
                fontFamily = FontFamily(Font(R.font.oswaldbold))
            )
        }


        Spacer(Modifier.weight(1f))
        Text(
            "Spacer",
            color = Color.Transparent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tabs(selectedCatalog: ExerciseCatalogEntity, steps: List<String>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tab = listOf("Instruct", "History", "Charts")

    Spacer(Modifier.height(10.dp))

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent, divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(selectedTabIndex),
                    color = Color(0xFFF1C40F),
                    height = 2.dp
                )
            },
            modifier = Modifier,
            contentColor = Color(0xFFF1C40F)
        ) {
            tab.forEachIndexed { index, string ->
                var isSelected = selectedTabIndex == index

                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    text = {
                        Text(
                            text = string,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color(0xFFF1C40F) else Color(0xFF4B5F71)
                        )
                    }
                )
            }
        }
        if (selectedTabIndex == 0) {
            Spacer(Modifier.height(35.dp))

            Column(modifier = Modifier) {
                Text(
                    text = "Muscle Worked",
                    color = Color.White,
                    fontSize = 20.sp,
                    letterSpacing = 0.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
                Spacer(Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = R.drawable.height,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp)),
                        alpha = 0.8f,
                        contentScale = ContentScale.None
                    )
                    Spacer(Modifier.weight(1f))
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = "PRIMARY",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp,
                            modifier = Modifier,
                            fontFamily = FontFamily(Font(R.font.lexendbold))
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = selectedCatalog.bodyPart,
                            color = Color.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp,
                            modifier = Modifier
                                .background(
                                    Color(0xFFF1C40F),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(4.dp),
                            fontFamily = FontFamily(Font(R.font.lexendbold))
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = "SECONDARY",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp,
                            modifier = Modifier,
                            fontFamily = FontFamily(Font(R.font.lexendbold))
                        )
                        Spacer(Modifier.height(5.dp))
                        selectedCatalog.secondaryMuscles.forEachIndexed { index, string ->
                            Column() {
                                Text(
                                    text = string,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.sp,
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFF1C40F),
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(4.dp),
                                    fontFamily = FontFamily(Font(R.font.lexendbold))
                                )
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Column() {
                Text(
                    text = "Step-By-Step",
                    color = Color.White,
                    fontSize = 20.sp,
                    letterSpacing = 0.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
                Spacer(Modifier.height(30.dp))
                steps.forEachIndexed { index, string ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFF1C40F),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .size(30.dp), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$index",
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.oswaldbold))
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = string,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun Instructions(selectedCatalog: ExerciseCatalogEntity) {
    val basicText = selectedCatalog.instructions
    val steps = basicText.split(".")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    LazyColumn() {
        itemsIndexed(steps) { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFF1C40F),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .size(30.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$index",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "$item",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular))
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}