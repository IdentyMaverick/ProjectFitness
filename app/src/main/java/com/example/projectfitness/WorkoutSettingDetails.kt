package com.example.projectfitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import database.ProjectFitnessContainer
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave

@Composable
fun WorkoutSettingDetails(
    navController: NavController,
    projectFitnessViewModel: ProjectFitnessViewModel,
    viewModelSave: ViewModelSave,
) {
    viewModelSave.RestTimer()
    //Database Creation*************************************************************************************************************************************************************

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    //******************************************************************************************************************************************************************************

    // Variable Initialize *********************************************************************************************************************************************************

    var config = LocalConfiguration.current
    var screenwidthDp = config.screenWidthDp.dp
    var screenheightDp = config.screenHeightDp.dp


    val selectedItemName = viewModelSave.selectedItemName.value
    val projectFitnessViewItems = projectFitnessViewModel.firestoreItems.value

    //**************************************************************************************************************************************************************************************

    // UI Coding ****************************************************************************************************************************************************************************

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)) {
            Icon(
                painterResource(id = R.drawable.projectfitnessprevious),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(30.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        if (viewModelSave.flagA.value == true) {
                            navController.navigate("workoutsettingscreen")
                        } else if (viewModelSave.flagB.value == true) {
                            navController.navigate("workoutlog")
                        } else if (viewModelSave.flagC.value == true) {
                            navController.navigate("chooseexercises/{name}")
                        } else if (viewModelSave.flagD.value == true){
                            navController.navigate("projectcoachscreen")
                        }
                    },
                tint = Color(0xFFF1C40F)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 50.dp)
                ,
                text = "PROJECT FITNESS",
                fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                color = Color(0xFFF1C40F),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp)
            )

        }

        Text(
            text = "" + selectedItemName.uppercase(),
            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
            style = TextStyle(fontSize = 20.sp, letterSpacing = 5.sp),
            color = Color(0xFFD9D9D9),
            modifier = Modifier
                .padding(top = screenheightDp / 10)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "MAIN MUSCLE GROUP",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 7.sp),
            color = Color(0xFF506172),
            modifier = Modifier
                .padding(top = screenheightDp / 6)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "" + projectFitnessViewItems?.let { returnListMainMuscleItems(it, selectedItemName) },
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFF1C40F),
            modifier = Modifier
                .padding(top = screenheightDp / 5)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "SECONDARY MUSCLE GROUP",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 7.sp),
            color = Color(0xFF506172),
            modifier = Modifier
                .padding(top = screenheightDp / 4)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "" + projectFitnessViewItems?.let { returnListSecondaryMuscleItems(it, selectedItemName) },
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFFF0F00),
            modifier = Modifier
                .padding(top = screenheightDp / 3.5f)
                .align(Alignment.TopCenter)
        )
        Row {

        }
        Image(
            painterResource(
                id = returnWorkoutDetail(selectedItemName)
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = screenheightDp / 2.5f)
                .align(Alignment.TopCenter)
        )
    }
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun WorkoutSettingDetails() {
    WorkoutSettingDetails(
        navController = rememberNavController(),
        projectFitnessViewModel = viewModel<ProjectFitnessViewModel>(),
        viewModelSave = viewModel()
    )
}