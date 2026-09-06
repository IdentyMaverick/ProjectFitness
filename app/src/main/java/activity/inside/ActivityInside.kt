package activity.inside

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import com.grozzbear.ui.components.GrozzComingSoonPanel
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusPhoto
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.local.viewmodel.ActivityInsideViewModel

@Composable
fun ActivityInside(navController: NavController, activityInsideViewModel: ActivityInsideViewModel) {
    val selectedCatalog by activityInsideViewModel.selectedCatalog.collectAsState()
    val steps =
        remember(selectedCatalog.instructions) {
            selectedCatalog.instructions
                .split(".")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

    var gifUrl by remember { mutableStateOf<String?>(null) }
    var muscleGraph by remember { mutableStateOf<String?>(null) }
    var gifLoading by remember { mutableStateOf(false) }
    var muscleLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCatalog.gifUrl, selectedCatalog.muscle) {
        gifUrl = null
        muscleGraph = null

        val gifPath = selectedCatalog.gifUrl?.trim().orEmpty()
        val musclePath = selectedCatalog.muscle?.trim().orEmpty()

        if (gifPath.isNotEmpty()) {
            gifLoading = true
            Firebase.storage.reference
                .child("cloudgoogle/$gifPath")
                .downloadUrl
                .addOnSuccessListener { gifUrl = it.toString() }
                .addOnFailureListener { Log.e("FirebaseStorage", "GIF URL failed", it) }
                .addOnCompleteListener { gifLoading = false }
        }

        if (musclePath.isNotEmpty()) {
            muscleLoading = true
            Firebase.storage.reference
                .child("cloudgoogle/$musclePath")
                .downloadUrl
                .addOnSuccessListener { muscleGraph = it.toString() }
                .addOnFailureListener { Log.e("FirebaseStorage", "Muscle URL failed", it) }
                .addOnCompleteListener { muscleLoading = false }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ActivityInsideTopBar(
                title = selectedCatalog.name.ifBlank { "Exercise" },
                subtitle =
                    listOfNotNull(
                        selectedCatalog.bodyPart.takeIf { it.isNotBlank() },
                        selectedCatalog.movementType.takeIf { it.isNotBlank() },
                    ).joinToString(" · "),
                onBack = { navController.popBackStack() },
            )
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            ExerciseDemoCard(
                gifUrl = gifUrl,
                level = selectedCatalog.level,
                loading = gifLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExerciseDetailTabs(
                selectedCatalog = selectedCatalog,
                steps = steps,
                muscleUrl = muscleGraph,
                muscleLoading = muscleLoading,
            )
        }
    }
}

@Composable
private fun ActivityInsideTopBar(title: String, subtitle: String, onBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = GrozzOnBackground,
                fontSize = 20.sp,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = GrozzYellow,
                    fontSize = 12.sp,
                    fontFamily = Lexend,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ExerciseDemoCard(gifUrl: String?, level: String, loading: Boolean) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(GrozzRadiusPhoto))
                .background(GrozzSurface),
        contentAlignment = Alignment.Center,
    ) {
        when {
            !gifUrl.isNullOrBlank() -> {
                AsyncImage(
                    model = gifUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            loading -> {
                CircularProgressIndicator(color = GrozzYellow, strokeWidth = 2.dp)
            }

            else -> {
                Icon(
                    painter = painterResource(R.drawable.dumbbell),
                    contentDescription = null,
                    tint = GrozzMuted,
                    modifier = Modifier.size(64.dp),
                )
            }
        }

        if (level.isNotBlank()) {
            Text(
                text = level,
                color = GrozzOnPrimary,
                fontSize = 12.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(GrozzYellow, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseDetailTabs(
    selectedCatalog: ExerciseCatalogEntity,
    steps: List<String>,
    muscleUrl: String?,
    muscleLoading: Boolean,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Instruct", "History", "Charts")

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = GrozzSystemBar,
            contentColor = GrozzYellow,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(selectedTabIndex),
                    color = GrozzYellow,
                    height = 2.dp,
                )
            },
        ) {
            tabs.forEachIndexed { index, label ->
                val selected = selectedTabIndex == index
                Tab(
                    selected = selected,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = label,
                            fontFamily = Lexend,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (selected) GrozzYellow else GrozzMuted,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTabIndex) {
            0 ->
                InstructTab(
                    selectedCatalog = selectedCatalog,
                    steps = steps,
                    muscleUrl = muscleUrl,
                    muscleLoading = muscleLoading,
                )

            1 ->
                GrozzComingSoonPanel(
                    title = "History",
                    message = "Your past sets for this exercise will show up here.",
                )

            else ->
                GrozzComingSoonPanel(
                    title = "Charts",
                    message = "Progress charts for this movement are coming soon.",
                )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InstructTab(
    selectedCatalog: ExerciseCatalogEntity,
    steps: List<String>,
    muscleUrl: String?,
    muscleLoading: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Muscle worked",
            style = MaterialTheme.typography.titleLarge,
            color = GrozzOnBackground,
        )
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(GrozzSurface),
            contentAlignment = Alignment.Center,
        ) {
            when {
                !muscleUrl.isNullOrBlank() -> {
                    AsyncImage(
                        model = muscleUrl,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                        contentScale = ContentScale.Fit,
                    )
                }

                muscleLoading -> {
                    CircularProgressIndicator(color = GrozzYellow, strokeWidth = 2.dp)
                }

                else -> {
                    Text(
                        text = "Muscle map unavailable",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrozzTextSecondary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "PRIMARY",
            style = MaterialTheme.typography.labelMedium,
            color = GrozzMuted,
        )
        Spacer(modifier = Modifier.height(8.dp))
        MuscleChip(
            text = selectedCatalog.bodyPart.ifBlank { "—" },
            emphasized = true,
        )

        if (selectedCatalog.secondaryMuscles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "SECONDARY",
                style = MaterialTheme.typography.labelMedium,
                color = GrozzMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                selectedCatalog.secondaryMuscles
                    .filter { it.isNotBlank() }
                    .forEach { muscle ->
                        MuscleChip(text = muscle, emphasized = false)
                    }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Step-by-step",
            style = MaterialTheme.typography.titleLarge,
            color = GrozzOnBackground,
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (steps.isEmpty()) {
            Text(
                text = "No instructions available for this exercise.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrozzTextSecondary,
            )
        } else {
            steps.forEachIndexed { index, step ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(28.dp)
                                .background(GrozzYellow, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = GrozzOnPrimary,
                            fontSize = 14.sp,
                            fontFamily = Oswald,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrozzOnBackground,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MuscleChip(text: String, emphasized: Boolean) {
    Text(
        text = text,
        color = if (emphasized) GrozzOnBackground else GrozzOnPrimary,
        fontSize = 13.sp,
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        modifier =
            Modifier
                .background(
                    color = if (emphasized) GrozzMuted.copy(alpha = 0.55f) else GrozzYellow,
                    shape = RoundedCornerShape(6.dp),
                ).padding(horizontal = 10.dp, vertical = 5.dp),
    )
}
