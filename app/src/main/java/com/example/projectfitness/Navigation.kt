package com.example.projectfitness

import Info
import LoginScreen
import RegisterScreen
import SecondInfo
import ThirdInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.FirstInfoScreen.route) {
        composable(route = Screens.FirstInfoScreen.route) {
            Info(navController = navController)
        }
        composable(route = Screens.SecondInfoScreen.route) {
            SecondInfo(navController = navController)
        }
        composable(route = Screens.ThirdInfoScreen.route) {
            ThirdInfo(navController = navController)
        }
        composable(route = Screens.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screens.LoginScreen.ForgetPasswordScreen.route) {
            ForgetPasswordScreen(navController = navController)
        }
        composable(route = Screens.LoginScreen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screens.Home.route) {
            Home(navController = navController)
        }
        composable(route = Screens.Activity.route) {
            Activity(navController = navController)
        }
        composable(route = Screens.Home.Profile.route) {
            Profile(navController = navController)
        }
        composable(route = Screens.Home.Profile.ProfileEdit.route) {
            ProfileEdit()
        }
        composable(route = Screens.LeaderBoard.route){
            LeaderBoard(navController = navController)
        }
        composable(route = Screens.Activity.CreateWorkout.route){
            CreateWorkout(navController = navController)
        }
    }

}

/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagers(){
    val pageCount = 3
    val pagerState = rememberPagerState()
    HorizontalPager(state = pagerState, pageCount = pageCount) { index ->
        if (index == 0) {
            Info()
        } else if (index == 1) {
            SecondInfo()
        } else if (index == 2) {
            ThirdInfo(navController = rememberNavController())
        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        )
        {
            repeat(pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color(0xFFF1C40F) else Color.White
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}
*/
@Composable
fun NavigationBar(navController: NavController, indexs: Int,flag: Boolean,flag2 : Boolean) {

    val items = listOf("Home", "Activity", "LeaderBoard", "Meal")
    androidx.compose.material3.NavigationBar(
        Modifier
            .fillMaxHeight()
            .height(50.dp)
            .padding(top = 600.dp), containerColor = Color(0xFF181F26)
    ) {

        var flag = flag
        var flag2 = flag2

        var color = if (flag){
            Color(0xFFF1C40F)
        }else Color.Black

        var color2 = if (flag2){
            Color(0xFFF1C40F)
        }else{ Color.Black  }

        items.forEachIndexed { index, item ->
            if (index == 0) { // Click Home Button
                NavigationBarItem(
                    selected = flag,
                    onClick = {
                        if (indexs == 0) { // When inside Home screen
                            flag = false
                        } else if (indexs == 1) {
                            flag = true
                            flag2 = false
                            navController.navigate(Screens.Home.route)

                        } else if (indexs == 2) {
                            flag = true
                            flag2 = false
                            navController.navigate(Screens.Home.route)
                        } else if (indexs == 3) {
                            flag = true
                            flag2 = false
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.home),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = color,
                        selectedIconColor = color
                    ),
                )
            } else if (index == 1) {
                NavigationBarItem(
                    selected = flag2,
                    onClick = {
                        if (indexs == 0) {
                            flag = false
                            flag2 = true
                            navController.navigate(Screens.Activity.route)
                        } else if (indexs == 1) {
                            // Do nothing
                        } else if (indexs == 2) {
                            flag = false
                            flag2 = true
                            navController.navigate(Screens.Activity.route)
                        } else if (indexs == 3) {
                            flag = false
                            flag2 = true
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.run),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = color2,
                        selectedIconColor = color2
                    ),
                )
            } else if (index == 2) {
                NavigationBarItem(
                    selected = true,
                    onClick = { if (indexs == 0) {
                        navController.navigate(Screens.LeaderBoard.route)
                        //flag = false
                       //flag2 = true
                    } else if (indexs == 1) {
                        navController.navigate(Screens.LeaderBoard.route)
                    } else if (indexs == 2) {
                        //flag = false
                        //flag2 = true
                    } else if (indexs == 3) {
                        //flag = false
                        //flag2 = true
                    }},
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.leaderboard),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = Color.Black,
                        selectedIconColor = Color.Black
                    ),
                )
            } else if (index == 3) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.meal),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = Color.Black,
                        selectedIconColor = Color.Black
                    ),
                )
            }

        }
    }
}