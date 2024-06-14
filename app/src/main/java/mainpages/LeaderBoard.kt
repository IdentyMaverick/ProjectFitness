package mainpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import navigation.NavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoard(navController: NavController) {

    var flag by remember { mutableStateOf(true) }
    var flag2 by remember { mutableStateOf(true) }
    var flag3 by remember { mutableStateOf(true) }
    var flag4 by remember { mutableStateOf(true) }

    var flaggg by remember { mutableStateOf(false) }
    var flaggg2 by remember { mutableStateOf(false) }
    var flaggg3 by remember { mutableStateOf(true) }
    var flaggg4 by remember { mutableStateOf(false) }

    val exercises = arrayOf("Chest Press", "Bench Press")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionTest by remember { mutableStateOf(exercises[0]) }

    var tabTitles = listOf("1 RP MAX","WORKOUT TIME")
    var selectedTabIndex by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF181F26))){
        SecondaryTabRow(selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF181F26),
            contentColor = Color.White,
            indicator = {
                // TabRowDefaults.SecondaryIndicator(color = Color.Cyan)
                TabRowDefaults.SecondaryIndicator(color = Color(0xFFF1C40F), height = 1.5f.dp, modifier = Modifier.tabIndicatorOffset(selectedTabIndex))
            }){
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                            fontSize = 15.sp,
                            letterSpacing = 3.sp)
                    ) }
                )
            }
        }
        if (selectedTabIndex == 0){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp , start = 50.dp)) {
                Text("Muscle Group" ,
                    style = TextStyle(fontSize = 15.sp ,
                        color = Color.White ,
                        fontFamily = FontFamily(
                    Font(R.font.postnobillscolombosemibold))) ,
                    letterSpacing = 3.sp)
                Spacer(modifier = Modifier.size(80.dp))
                Text("Exercise" ,
                    style = TextStyle(fontSize = 15.sp ,
                        color = Color.White ,
                        fontFamily = FontFamily(
                            Font(R.font.postnobillscolombosemibold))) ,
                    letterSpacing = 3.sp)
            }
        }
    }
    val indexs = 2
    NavigationBar(navController = navController, indexs,flaggg,flaggg2,flaggg3,flaggg4)
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewLeaderBoard() {
    LeaderBoard(rememberNavController())
}