package ui.mainpages.inside

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzSystemBar
import data.local.viewmodel.PersonalInformationsScreenViewModel
import data.remote.UserProfile
import viewmodel.ProfileUiState
import java.util.Calendar

private val InfoAccent = Color(0xFFF1C40F)
private val InfoSurface = Color(0xFF1C2126)
private val InfoBorder = Color(0xFF2E353D)
private val InfoMuted = Color(0xFF4B5F71)

@Composable
fun PersonalInformationsScreen(
    navController: NavController,
    personalInformationsScreenViewModel: PersonalInformationsScreenViewModel
) {
    val profileState by personalInformationsScreenViewModel.profileState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        personalInformationsScreenViewModel.loadUid()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SettingsFlowTopBar(
                title = "PERSONAL INFO",
                onBack = {
                    personalInformationsScreenViewModel.loadUid()
                    navController.popBackStack()
                }
            )
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when (val state = profileState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = InfoAccent)
                }
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Couldn't load your profile",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Check your connection and try again.",
                            color = Color.Gray,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { personalInformationsScreenViewModel.loadUid() },
                            colors = ButtonDefaults.buttonColors(containerColor = InfoAccent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Retry",
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.lexendbold))
                            )
                        }
                    }
                }
            }

            is ProfileUiState.Ready -> {
                PersonalInfoForm(
                    profile = state.profile,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onSave = { first, gender, birthDate, height, weight ->
                        personalInformationsScreenViewModel.updateUserInformation(
                            first = first,
                            gender = gender,
                            birthDate = birthDate,
                            height = height,
                            weight = weight
                        )
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        personalInformationsScreenViewModel.loadUid()
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonalInfoForm(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    onSave: (first: String, gender: Boolean, birthDate: String, height: String, weight: String) -> Unit
) {
    var first by remember(profile.first) { mutableStateOf(profile.first) }
    var gender by remember(profile.gender) { mutableStateOf(profile.gender) }
    var birthDate by remember(profile.birthDate) { mutableStateOf(profile.birthDate) }
    var height by remember(profile.height) { mutableStateOf(profile.height) }
    var weight by remember(profile.weight) { mutableStateOf(profile.weight) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(104.dp)
                .border(3.dp, InfoAccent, CircleShape)
                .padding(3.dp)
                .border(2.dp, Color.Black, CircleShape)
                .padding(3.dp)
        ) {
            AsyncImage(
                model = profile.userPhotoUri.ifBlank {
                    R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml
                },
                contentDescription = "Profile photo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row {
            Text(
                text = "Update ",
                color = Color.White,
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
            Text(
                text = "Profile",
                color = InfoAccent,
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
        }

        Text(
            text = "@${profile.nickname}",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 13.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular)),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileFieldLabel("Full Name")
        ProfileOutlinedField(
            value = first,
            onValueChange = { first = it },
            placeholder = "Your name",
            leadingIconRes = R.drawable.personadd
        )

        Spacer(modifier = Modifier.height(18.dp))

        ProfileFieldLabel("Email Address")
        ProfileOutlinedField(
            value = profile.email,
            onValueChange = {},
            placeholder = "Your email",
            leadingIconRes = R.drawable.alternateemailicon128,
            readOnly = true,
            enabled = false
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenderSelector(
                gender = gender,
                onGenderChange = { gender = it },
                modifier = Modifier.weight(1f)
            )
            BirthDateSelector(
                birthDate = birthDate,
                onBirthDateChange = { birthDate = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ProfileFieldLabel("Height (cm)", padded = false)
                ProfileOutlinedField(
                    value = height,
                    onValueChange = { height = it.filter { ch -> ch.isDigit() || ch == '.' }.take(6) },
                    placeholder = "Height",
                    keyboardType = KeyboardType.Decimal,
                    horizontalPadding = 0.dp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                ProfileFieldLabel("Weight (kg)", padded = false)
                ProfileOutlinedField(
                    value = weight,
                    onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' }.take(6) },
                    placeholder = "Weight",
                    keyboardType = KeyboardType.Decimal,
                    horizontalPadding = 0.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onSave(first.trim(), gender, birthDate, height.trim(), weight.trim())
            },
            colors = ButtonDefaults.buttonColors(containerColor = InfoAccent),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Text(
                text = "Save Changes",
                color = Color(0xFF121417),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.sendicon128),
                contentDescription = null,
                tint = Color(0xFF121417),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ProfileFieldLabel(text: String, padded: Boolean = true) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.7f),
        fontFamily = FontFamily(Font(R.font.lexendsemibold)),
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (padded) Modifier.padding(horizontal = 20.dp) else Modifier)
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun ProfileOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIconRes: Int? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    horizontalPadding: Dp = 16.dp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        placeholder = {
            Text(
                text = placeholder,
                color = InfoMuted,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
        },
        textStyle = TextStyle(
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.lexendregular)),
            fontSize = 16.sp
        ),
        singleLine = true,
        readOnly = readOnly,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InfoSurface,
            unfocusedContainerColor = InfoSurface,
            disabledContainerColor = InfoSurface,
            focusedBorderColor = InfoMuted,
            unfocusedBorderColor = InfoBorder,
            disabledBorderColor = InfoBorder,
            cursorColor = InfoAccent,
            disabledTextColor = Color.White.copy(alpha = 0.55f)
        ),
        leadingIcon = leadingIconRes?.let { res ->
            {
                Icon(
                    painter = painterResource(res),
                    contentDescription = null,
                    tint = InfoMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

@Composable
private fun GenderSelector(
    gender: Boolean,
    onGenderChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Male", "Female")
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = if (gender) "Female" else "Male"

    Column(modifier = modifier) {
        Text(
            text = "Gender",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.lexendsemibold))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(InfoSurface)
                .border(1.dp, InfoBorder, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedOption,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(InfoSurface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.lexendregular))
                            )
                        },
                        onClick = {
                            onGenderChange(option == "Female")
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDateSelector(
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val datePickerState = rememberDatePickerState(
        yearRange = 1900..currentYear,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= currentYear
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val date = datePickerState.selectedDateMillis?.let {
                            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                .format(java.util.Date(it))
                        } ?: birthDate
                        onBirthDateChange(date)
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = InfoAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Birth Date",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.lexendsemibold))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(InfoSurface)
                .border(1.dp, InfoBorder, RoundedCornerShape(12.dp))
                .clickable { showDatePicker = true }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (birthDate.isBlank()) "Select date" else birthDate,
                color = if (birthDate.isBlank()) InfoMuted else Color.White,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
