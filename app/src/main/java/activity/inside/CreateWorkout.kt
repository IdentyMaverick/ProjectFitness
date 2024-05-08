package activity.inside

import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import kotlinx.coroutines.launch
import navigation.Screens
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkout(navController: NavController,viewModelSave: ViewModelSave) {

    // Variable Initialize *****************************************************************************************************************************************************************

    val context = LocalContext.current
    val container = ProjectFitnessContainer(context)
    val transparentColorFilter = Color(0f,0f,0f,0.4f)

    var count = viewModelSave.count
    var flag = viewModelSave.flag
    var exercise = viewModelSave.exercises
    var text = remember{ mutableStateOf("") }
    val exercises = arrayOf("All", "Chest", "Leg")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionTest by remember { mutableStateOf(exercises[0]) }
    var allowed = viewModelSave.allowed
    var selectedItemName by viewModelSave.selectedItemName

    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp.dp
    val screenheightDp = configuration.screenHeightDp.dp

    val viewModel: ProjectFitnessViewModel = viewModel()
    val firestoreItems = viewModel.firestoreItems.value

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    //***************************************************************************************************************************************************************************************

    // UI Coding ****************************************************************************************************************************************************************************

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        Icon(
            painter = painterResource(id = R.drawable.projectfitnessprevious),
            contentDescription = null,
            modifier = Modifier
                .clickable(onClick = { navController.navigate("chooseexercises/{name}") })
                .size(30.dp)
                .padding(top = 5.dp),
            tint = Color(0xFFD9D9D9)
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = screenheightDp / 7.5f)
                .align(Alignment.Center)) {


            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(screenwidthDp)
                    .height(screenheightDp / 1.1f)
                    .background(Color(0xFF181F26))
            )
            {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(){
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier
                                .padding(top = 25.dp)
                                .height(45.dp)
                                .width(160.dp),
                        ) {
                            TextField(
                                value = selectedOptionTest,
                                onValueChange = {},
                                Modifier
                                    .menuAnchor()
                                    .height(60.dp)
                                    .width(150.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color(0xFF21282F),
                                    unfocusedTextColor = Color(0xFFD9D9D9),
                                    focusedTextColor = Color(0xFFD9D9D9),
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                ),
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        painterResource(id = R.drawable.down),
                                        contentDescription = null,
                                        tint = Color(0xFFD9D9D9),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                textStyle = TextStyle(fontSize = 12.sp)

                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                                ) {
                                exercises.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(text = selectionOption, fontFamily = FontFamily(Font(
                                            R.font.postnobillscolombomedium
                                        ))) },
                                        onClick = {
                                            selectedOptionTest = selectionOption
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }

                            }

                        }
                    }


                    Spacer(modifier = Modifier.size(10.dp))
                    Box(
                        modifier = Modifier
                            .width(screenwidthDp / 1.1f)
                            .fillMaxHeight()
                            .background(
                                Color(0xFF181F26),
                                shape = RoundedCornerShape(0.dp)
                            )

                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.TopCenter)
                                .padding(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                        ) {
                            items(firestoreItems) { item ->
                                if (item.bodypart == selectedOptionTest && text.value.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.Center)
                                            .height(70.dp)
                                            .background(
                                                Color(0xFF2C3E50),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable(onClick = {/*
                                                viewModelSave.updateSelectedItemName(item.name.toString())
                                                Log.d(
                                                    "TAG1",
                                                    "Selected Item Name is sett : ${item.name}"
                                                )
                                                navController.navigate(route = "workoutdetails")
                                                //ispopupVisible = true
                                                flag.value = true
                                                count.value += 1*/
                                            })

                                    ) {
                                        Image(painterResource(id = R.drawable.cablecrossover /*imager(projectFitnessViewModel = ProjectFitnessViewModel(),item)*/),
                                            contentDescription =null,
                                            alpha = 0.5f,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                ) ,
                                        )
                                        Text(
                                            text = "" + item.name,
                                            modifier = Modifier
                                                .align(
                                                    Alignment.Center
                                                )
                                            ,
                                            style = TextStyle(fontSize = 20.sp,letterSpacing = 3.sp),
                                            color = Color(0xFFD9D9D9),
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                                            textAlign = TextAlign.Center
                                        )
                                        /*Button(
                                            onClick = {
                                                //ispopupVisible = true
                                                navController.navigate (route = "chooseexercises/"+item.name)
                                                flag.value = true
                                                      count.value += 1},
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .width(60.dp)
                                                .height(30.dp)
                                                .padding(end = 10.dp),
                                            colors = ButtonDefaults.buttonColors(Color(0xFFD9D9D9)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(text = "add")
                                        }*/
                                    }
                                    Spacer(modifier = Modifier.size(10.dp))
                                } else if (  selectedOptionTest == "All" && text.value.isEmpty()) {   // OPTIMAL SITUATION
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.Center)
                                            .height(70.dp)
                                            .background(
                                                Color(0xFF2C3E50),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable(onClick = {
                                                viewModelSave.updateSelectedItemName(item.name.toString())
                                                Log.d(
                                                    "TAG1",
                                                    "Selected Item Name is sett : ${item.name}"
                                                )
                                                navController.navigate(route = "workoutdetails")
                                                //ispopupVisible = true
                                                flag.value = true
                                                count.value += 1
                                            })

                                    ) {

                                        Image(
                                            painterResource(
                                                id = R.drawable.cablecrossover/*imager(projectFitnessViewModel = ProjectFitnessViewModel(),item)*/),
                                            contentDescription = null,
                                            alpha = 0.5f,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                ),
                                        )
                                        Text(
                                            text = "" + item.name,
                                            modifier = Modifier
                                                .align(
                                                    Alignment.Center
                                                ),
                                            style = TextStyle(fontSize = 20.sp,letterSpacing = 3.sp),
                                            color = Color(0xFFD9D9D9),
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)) ,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(10.dp))
                                }
                                else if (selectedOptionTest == "All" && text.value.contains("B") == item.name?.contains("B")
                                    || selectedOptionTest == "All" && text.value.contains("Barbell") == item.name?.contains("Barbell")){
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.Center)
                                            .height(70.dp)
                                            .background(
                                                Color(0xFF2C3E50),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable(onClick = {/*
                                                viewModelSave.updateSelectedItemName(item.name.toString())
                                                Log.d(
                                                    "TAG1",
                                                    "Selected Item Name is sett : ${item.name}"
                                                )
                                                navController.navigate(route = "workoutdetails")
                                                //ispopupVisible = true
                                                flag.value = true
                                                count.value += 1*/
                                            })
                                    ) {
                                        Image(painterResource(R.drawable.cablecrossover/*imager(projectFitnessViewModel = ProjectFitnessViewModel(),item)*/),
                                            contentDescription =null,
                                            alpha = 0.5f,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                ),
                                            colorFilter = ColorFilter.tint(Color.Transparent.copy(alpha = 0.4f)))
                                        Text(
                                            text = "" + item.name,
                                            modifier = Modifier
                                                .align(
                                                    Alignment.Center
                                                ),
                                            style = TextStyle(fontSize = 20.sp,letterSpacing = 3.sp),
                                            color = Color(0xFFD9D9D9),
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(10.dp))
                                }
                            }
                        }
                    }
                }

            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF181F26)),
        )
        {

            Row(Modifier.align(Alignment.CenterStart)) {

                    Icon(
                        painter = painterResource(id = R.drawable.projectfitnessprevious),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable(onClick = { navController.navigate("chooseexercises/{name}") })
                            .padding(top = 10.dp)
                            .size(30.dp)
                        ,
                        tint = Color(0xFFF1C40F)
                    )



                Text(
                    text = "Choose Exercise",
                    color = Color(0xFFF1C40F),
                    fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                    style = TextStyle(fontSize = 30.sp),
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { showBottomSheet = true }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnesspointheavy),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp),
                        tint = Color(0xFFF1C40F)
                    )

                }

            }

            if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, containerColor = Color(0xFF283747)) {
                    LaunchedEffect(Unit) {
                        scope.launch { sheetState.expand() }.invokeOnCompletion { if (!sheetState.isVisible) {showBottomSheet = false} }
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(77.dp) )
                    {
                        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                            Button(onClick = { navController.navigate(Screens.LoginScreen.route) }, modifier = Modifier
                                .align(Alignment.End)
                                .padding(bottom = 25.dp)
                                .fillMaxWidth()
                                .height(60.dp),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text(text = "Logout",
                                    style = TextStyle(fontSize = 25.sp , fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))),
                                    color = Color(0xFFF1C40F))
                            }

                        }
                    }
                }
            }

        }

        Row(modifier = Modifier.padding(top = 80.dp)) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = Modifier
                    .height(41.dp)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .background(Color(0xFF21282F), shape = RoundedCornerShape(15.dp)),
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                    color = Color(0xFFD9D9D9)
                ),
                maxLines = 1
                ,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF21282F),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = Color(0xFF21282F),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Favorite icon",
                            tint = Color(0xFFD9D9D9)
                        )
                        Spacer(modifier = Modifier.width(width = 10.dp))
                        innerTextField()
                    }
                }

            )
            Spacer(modifier =Modifier.size(15.dp))
            Button(onClick = {},
                modifier = Modifier
                    .padding(top = screenheightDp / 3.75f)
                    .width(70.dp)
                    .height(19.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "Filter",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 7.sp),
                    color = Color.Black)
            }
        }
    }
}
/*@Composable
fun imager(projectFitnessViewModel: ProjectFitnessViewModel,item : Exercises) : Int {

     var items = item

    if (items.index == "0")
    {
        return R.drawable.barbellbenchpress
    }
    else if (items.index == "1")
    {
        return R.drawable.cablecrossover
    }
    else if (items.index == "2")
    {
        return R.drawable.calfraises
    }
    else if (items.index == "3")
    {
        return R.drawable.chestdip
    }
    else if (items.index == "4")
    {
        return R.drawable.chestpro
    }
    else if (items.index == "5")
    {
        return R.drawable.cablecrossover
    }
    // QUADRICEPS EXERCISES
    else if (items.index == "15")
    {
        return R.drawable.legpress
    }
    else if (items.index == "21")
    {
        return R.drawable.squat
    }
    else if (items.index == "22")
    {
        return R.drawable.legextension
    }
    else if (items.index == "23")
    {
        return R.drawable.machinehacksquat
    }
    else if (items.index == "24")
    {
        return R.drawable.dumbbellsquat
    }
    else if (items.index == "25")
    {
        return R.drawable.dumbbelllunge
    }
    else if (items.index == "26") {
        return R.drawable.frontsquat
    }
    else if (items.index == "27") {
        return R.drawable.dumbbellbulgariansplitsquat
    }
    else if (items.index == "28") {
        return R.drawable.dumbbellsplitsquat
    }
    else if (items.index == "29") {
        return R.drawable.pliesquat
    }
    else if (items.index == "30") {
        return R.drawable.smithmachinesquat
    }
    else if (items.index == "31") {
        return R.drawable.singlelegextension
    }
    else if (items.index == "32") {
        return R.drawable.boxjump
    }
    //HAMSTRING EXERCISES
    else if (items.index == "33") {
        return R.drawable.stifflegdeadlift
    }
    else if (items.index == "34") {
        return R.drawable.dumbbellhamstringcurl
    }
    else if (items.index == "35") {
        return R.drawable.trapbardeadlift
    }
    else if (items.index == "36") {
        return R.drawable.seatedlegcurl
    }
    else if (items.index == "37") {
        return R.drawable.kettlebellswing
    }
    else if (items.index == "38") {
        return R.drawable.sumodeadlift
    }
    else if (items.index == "39") {
        return R.drawable.lyinglegcurl
    }
    else if (items.index == "40") {
        return R.drawable.nordichamstringcurl
    }
    // CALF EXERCISES
    else if (items.index == "41") {
        return R.drawable.seatedcalfraise
    }
    else if (items.index == "42") {
        return R.drawable.legpresscalfraise
    }
    else if (items.index == "43") {
        return R.drawable.standingmachinecalfraise
    }
    //GLUTES
    else if (items.index == "44") {
        return R.drawable.hyperextension
    }
    else if (items.index == "45") {
        return R.drawable.barbellhipthrust
    }
    else if (items.index == "46") {
        return R.drawable.standinggoodmorning
    }
    //EXTENSION
    else if (items.index == "47") {
        return R.drawable.itbandfoamrolling
    }
    else if (items.index == "48") {
        return R.drawable.plantarfascialacrosseball
    }
    else if (items.index == "49") {
        return R.drawable.kneelingposteriorhipcapsulemobilization
    }
    //ABDUCTORS
    else if (items.index == "50") {
        return R.drawable.hipabductionmachine
    }
    //ADDUCTORS
    else if (items.index == "51") {
        return R.drawable.hipadductionmachine
    }
        return R.drawable.down
}*/


/*@Composable
fun PopUpContent(onDismiss : () -> Unit){
    Box(
        Modifier
            .width(250.dp)
            .height(150.dp)
            .background(Color(0xFF506172).copy(alpha = 1f), shape = RoundedCornerShape(20.dp))){
        Text(text = "Pooop Up!" )
       /* Button(onClick = { }) {
            Text(text = "Close")
        }*/
    }
}
*/

@Preview(showSystemUi = true)
@Composable
fun PreviewCreateWorkout() {
    CreateWorkout(navController = rememberNavController(), viewModelSave = viewModel())
}

// Exercises -> LowerBody Exercises -> LowerLeg Exercises -> Calf Raises