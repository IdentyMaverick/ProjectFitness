package ui.mainpages.inside

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import com.grozzbear.R
import data.local.viewmodel.PersonalInformationsScreenViewModel
import viewmodel.ProfileUiState

@Composable
fun PersonalInformationsScreen(
    navController: NavController,
    personalInformationsScreenViewModel: PersonalInformationsScreenViewModel
) {
    val profileState = personalInformationsScreenViewModel.profileState.collectAsState().value

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarPersonalInformationsScreen(navController, personalInformationsScreenViewModel)
        },
        containerColor = Color(0xFF121417),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        profileState.let { state ->
            when (state) {
                is ProfileUiState.Loading -> {
                    Column(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(color = Color(0xFFF1C40F))
                    }
                }

                is ProfileUiState.Error -> {
                    navController.popBackStack()
                }

                is ProfileUiState.Ready -> {
                    val profile = (state as ProfileUiState.Ready).profile
                    val first = remember { mutableStateOf(profile.first) }
                    val email = remember { mutableStateOf(profile.email) }
                    val gender = remember { mutableStateOf(profile.gender) }
                    val birthDate = remember { mutableStateOf(profile.birthDate) }
                    val height = remember { mutableStateOf(profile.height) }
                    val weight = remember { mutableStateOf(profile.weight) }
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(10.dp))
                        AsyncImage(
                            model = if (profile.userPhotoUri == "") R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml else profile.userPhotoUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Update",
                                color = Color.White,
                                fontSize = 20.sp,
                                letterSpacing = 0.sp,
                                fontFamily = FontFamily(Font(R.font.lexendextrabold))

                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "Profile",
                                color = Color(0xFFF1C40F),
                                fontSize = 20.sp,
                                letterSpacing = 0.sp,
                                fontFamily = FontFamily(Font(R.font.lexendextrabold))

                            )
                        }
                        Spacer(Modifier.height(40.dp))
                        Text(
                            "Full Name",
                            color = Color.White.copy(alpha = 0.7f), // Görseldeki gibi biraz daha sönük beyaz
                            fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = first.value,
                            onValueChange = { first.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            placeholder = {
                                Text(
                                    text = "Your Name",
                                    color = Color(0xFF4B5F71), // Görseldeki placeholder rengi
                                    fontFamily = FontFamily(Font(R.font.lexendregular))
                                )

                            },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.lexendregular)),
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1C2126),   // Kutu içi (Odaklanınca)
                                unfocusedContainerColor = Color(0xFF1C2126), // Kutu içi (Normal)
                                focusedBorderColor = Color(0xFF4B5F71),      // Kenarlık rengi (Odaklanınca)
                                unfocusedBorderColor = Color(0xFF2E353D),    // Kenarlık rengi (Normal)
                                cursorColor = Color(0xFFF1C40F),             // Sarı imleç
                                focusedLabelColor = Color.Transparent,
                                unfocusedLabelColor = Color.Transparent
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.personadd),
                                    contentDescription = null,
                                    tint = Color(0xFF4B5F71),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "E-Mail Address",
                            color = Color.White.copy(alpha = 0.7f), // Görseldeki gibi biraz daha sönük beyaz
                            fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            placeholder = {
                                Text(
                                    text = "Your E-mail Address",
                                    color = Color(0xFF4B5F71), // Görseldeki placeholder rengi
                                    fontFamily = FontFamily(Font(R.font.lexendregular))
                                )
                            },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.lexendregular)),
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1C2126),   // Kutu içi (Odaklanınca)
                                unfocusedContainerColor = Color(0xFF1C2126), // Kutu içi (Normal)
                                focusedBorderColor = Color(0xFF4B5F71),      // Kenarlık rengi (Odaklanınca)
                                unfocusedBorderColor = Color(0xFF2E353D),    // Kenarlık rengi (Normal)
                                cursorColor = Color(0xFFF1C40F),             // Sarı imleç
                                focusedLabelColor = Color.Transparent,
                                unfocusedLabelColor = Color.Transparent
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.alternateemailicon128),
                                    contentDescription = null,
                                    tint = Color(0xFF4B5F71),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            readOnly = true
                        )
                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {

                                Selection(profile.gender, setGender = { gender.value = it })
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                BirthDateField(
                                    profile.birthDate,
                                    setBirthDate = { birthDate.value = it })
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                Information(
                                    height.value,
                                    weight.value,
                                    personalInformationsScreenViewModel,
                                    first.value,
                                    gender.value,
                                    birthDate.value,
                                    setHeight = { height.value = it },
                                    setWeight = { weight.value = it })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Information(
    height: String,
    weight: String,
    personalInformationsScreenViewModel: PersonalInformationsScreenViewModel,
    first: String,
    gender: Boolean,
    birthDate: String,
    setHeight: (String) -> Unit,
    setWeight: (String) -> Unit
) {
    // Yan yana dizilim için Row kullanıyoruz
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Ekran kenarlarından boşluk
            horizontalArrangement = Arrangement.spacedBy(12.dp) // İki kutu arasındaki boşluk
        ) {
            // SOL TARAF (Boy/Height)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Height (cm)",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = height,
                    onValueChange = { setHeight(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Height", // Placeholder'ı güncelledim
                            color = Color(0xFF4B5F71),
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1C2126),
                        unfocusedContainerColor = Color(0xFF1C2126),
                        focusedBorderColor = Color(0xFF4B5F71),
                        unfocusedBorderColor = Color(0xFF2E353D),
                        cursorColor = Color(0xFFF1C40F)
                    )
                )
            }

            // SAĞ TARAF (Kilo/Weight vb.)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Weight (kg)", // Örnek olarak Weight yazdım
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = weight,
                    onValueChange = { setWeight(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Weight",
                            color = Color(0xFF4B5F71),
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1C2126),
                        unfocusedContainerColor = Color(0xFF1C2126),
                        focusedBorderColor = Color(0xFF4B5F71),
                        unfocusedBorderColor = Color(0xFF2E353D),
                        cursorColor = Color(0xFFF1C40F)
                    )
                )
            }
        }
        Spacer(Modifier.height(40.dp))
        Column(
            modifier = Modifier
                .clickable(onClick = {
                    personalInformationsScreenViewModel.updateUserInformation(
                        first,
                        gender,
                        birthDate,
                        height,
                        weight
                    )
                })
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .height(70.dp)
                    .background(Color(0xFFF1C40F), RoundedCornerShape(30.dp))
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Save Changes",
                        color = Color(0xFF121417),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.lexendextrabold))
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        painter = painterResource(R.drawable.sendicon128),
                        contentDescription = null,
                        tint = Color(0xFF121417),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDateField(birthDate: String, setBirthDate: (String) -> Unit) {
    val datePickerState = rememberDatePickerState(
        yearRange = 1900..2026,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= 2026
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(birthDate) }

    androidx.compose.runtime.LaunchedEffect(birthDate) {
        selectedDate = birthDate
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            .format(java.util.Date(it))
                    } ?: selectedDate
                    selectedDate = date
                    setBirthDate(date)
                    showDatePicker = false
                }) { Text("OK", color = Color(0xFFF1C40F)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column {
        Text(
            "Birth Date",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C2126))
                .clickable { showDatePicker = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(selectedDate, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun HomeTopBarPersonalInformationsScreen(
    navController: NavController,
    personalInformationsScreenViewModel: PersonalInformationsScreenViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {
            navController.popBackStack()
            personalInformationsScreenViewModel.loadUid()
        }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "PERSONAL INFORMATIONS",
            color = Color.White,
            fontSize = 20.sp,
            letterSpacing = 0.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selection(gender: Boolean, setGender: (Boolean) -> Unit) {
    val gender = if (gender == false) 0 else 1
    val options = listOf("Male", "Female")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[gender]) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Gender",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.lexendsemibold))
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C2126)) // Tasarımdaki koyu gri
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedOption,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color(0xFF1C2126))
                    .fillMaxWidth(0.3f) // Genişliği ayarla
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            if (selectionOption == "Male") {
                                setGender(false)
                            } else {
                                setGender(true)
                            }

                            selectedOption = selectionOption
                            expanded = false
                        }
                    )
                    {
                        Text(
                            text = selectionOption,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    }
                }
            }
        }
    }
}