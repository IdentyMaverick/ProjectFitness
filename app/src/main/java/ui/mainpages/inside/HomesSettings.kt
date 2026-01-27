package ui.mainpages.inside

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave

@Composable
fun HomesSettings(
    navController: NavController,
    viewModelSave: ViewModelSave,
    viewModel: ProjectFitnessViewModel,
    viewModelProfile: ViewModelProfile
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBar()
        },
        containerColor = Color(0xFF121417),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) {
paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        )  {


            Spacer(Modifier.size(40.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text("Profile",
                    style = TextStyle(color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 30.dp))
                Spacer(Modifier.size(20.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                ) {
                    Text("Personal Informations",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 18.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier
                .size(40.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text("Notifications",
                    style = TextStyle(color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 30.dp))
                Spacer(Modifier.size(20.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                ) {
                    Text("Workout Reminders",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 18.sp
                        )
                    )
                }
                Spacer(Modifier.size(10.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                ) {
                    Text("Achivement Reminders",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 18.sp
                        )
                    )
                }
            }
            Spacer(Modifier.size(40.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text("App Settings",
                    style = TextStyle(color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 30.dp))
                Spacer(Modifier.size(10.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                ) {
                    Text("Application Theme & More",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 18.sp
                        )
                    )
                }
            }
            Spacer(Modifier.size(40.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text("Safety & Privacy",
                    style = TextStyle(color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 30.dp))
                Spacer(Modifier.size(10.dp))
                Row(
modifier = Modifier
    .padding(horizontal = 30.dp)
                ) {
                    Text("App Permission",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}
    @Composable
    fun HomeTopBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.left
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 30.sp,
                letterSpacing = 0.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )

            Spacer(Modifier.weight(1f))

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.projectfitnesspointheavy),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                    tint = Color.Transparent
                )
            }
        }
    }