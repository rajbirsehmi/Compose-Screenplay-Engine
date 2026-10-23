package com.sehmi.composescreenplayengine

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ShowcaseAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleLoginScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isDarkThemeOverride by remember { mutableStateOf<Boolean?>(null) }
    val systemInDark = isSystemInDarkTheme()
    val activeDarkTheme = isDarkThemeOverride ?: systemInDark

    ShowcaseAppTheme(darkTheme = activeDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("🎭 Screenplay Showcase App", fontWeight = FontWeight.Bold)
                            Text("Compose Screenplay Engine Interaction Gallery", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isDarkThemeOverride = !activeDarkTheme },
                            modifier = Modifier.testTag("btn_theme_toggle")
                        ) {
                            Text(if (activeDarkTheme) "🌙" else "☀️", fontSize = 18.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Text("👆", fontSize = 20.sp) },
                        label = { Text("Gestures") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Text("✍️", fontSize = 20.sp) },
                        label = { Text("Text/Key") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Text("📜", fontSize = 20.sp) },
                        label = { Text("Lists/Drag") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Text("⏱️", fontSize = 20.sp) },
                        label = { Text("Clock/OS") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Text("🎭", fontSize = 20.sp) },
                        label = { Text("BDD/Diag") }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> GesturesShowcaseTab()
                    1 -> TextAndKeyboardShowcaseTab()
                    2 -> ListsAndDragShowcaseTab()
                    3 -> ClockAndSystemShowcaseTab()
                    4 -> BddAndDiagnosticsShowcaseTab()
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// TAB 1: GESTURES & TOUCH INTERACTION SHOWCASE
// ----------------------------------------------------------------------------
@Composable
private fun GesturesShowcaseTab() {
    var gestureStatus by remember { mutableStateOf("Status: Ready") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusCard(gestureStatus, testTag = "status_text")

        ShowcaseSection(title = "Single Tap Actions", iconEmoji = "👆") {
            Button(
                onClick = { gestureStatus = "Status: Single Clicked" },
                modifier = Modifier.fillMaxWidth().testTag("btn_single_click")
            ) {
                Text("Click.onTag('btn_single_click')")
            }

            Button(
                onClick = { gestureStatus = "Status: Text Clicked" },
                modifier = Modifier.fillMaxWidth().testTag("btn_text_click")
            ) {
                Text("Click Me By Text")
            }
        }

        ShowcaseSection(title = "Double & Long Taps", iconEmoji = "⚡") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_double_click")
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = { gestureStatus = "Status: Double Clicked" })
                        }
                ) {
                    Text("Double Click")
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(20.dp))
                        .testTag("btn_long_click")
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = { gestureStatus = "Status: Long Clicked" })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Long Click Box", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
        }

        ShowcaseSection(title = "Parent-Child Tree Traversal", iconEmoji = "🌿") {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().testTag("parent_container")
            ) {
                Button(
                    onClick = { gestureStatus = "Status: First Child Clicked" },
                    modifier = Modifier.padding(16.dp).fillMaxWidth().testTag("child_item")
                ) {
                    Text("ClickFirstChild.ofParent('parent_container')")
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// TAB 2: TEXT ENTRY & KEYBOARD SHOWCASE
// ----------------------------------------------------------------------------
@Composable
private fun TextAndKeyboardShowcaseTab() {
    var usernameText by remember { mutableStateOf("") }
    var replaceTextVal by remember { mutableStateOf("Initial Content") }
    var clearTextVal by remember { mutableStateOf("Clearable Text") }
    var imeStatus by remember { mutableStateOf("IME Status: Idle") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusCard(imeStatus, testTag = "txt_ime_status")

        ShowcaseSection(title = "Text Entry & Value Building", iconEmoji = "✍️") {
            OutlinedTextField(
                value = usernameText,
                onValueChange = { usernameText = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth().testTag("input_username")
            )

            OutlinedTextField(
                value = replaceTextVal,
                onValueChange = { replaceTextVal = it },
                label = { Text("ReplaceText Target ('input_replace')") },
                modifier = Modifier.fillMaxWidth().testTag("input_replace")
            )

            OutlinedTextField(
                value = clearTextVal,
                onValueChange = { clearTextVal = it },
                label = { Text("ClearText Target ('input_clear')") },
                modifier = Modifier.fillMaxWidth().testTag("input_clear")
            )
        }

        ShowcaseSection(title = "Focus & Keyboard IME Actions", iconEmoji = "⌨️") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Focus Target ('input_focus')") },
                modifier = Modifier.fillMaxWidth().testTag("input_focus")
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("IME Action Target ('input_ime')") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { imeStatus = "IME Status: Search Triggered" }),
                modifier = Modifier.fillMaxWidth().testTag("input_ime")
            )
        }
    }
}

// ----------------------------------------------------------------------------
// TAB 3: LISTS, SCROLLING & DRAG DROP SHOWCASE
// ----------------------------------------------------------------------------
@Composable
private fun ListsAndDragShowcaseTab() {
    var dragStatus by remember { mutableStateOf("Drag Status: Idle") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusCard(dragStatus, testTag = "txt_drag_status")

        ShowcaseSection(title = "Drag & Drop Interaction", iconEmoji = "🤏") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .testTag("drag_source"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Drag Source", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                        .testTag("drop_target"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Drop Target", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        ShowcaseSection(title = "Lazy List Traversal & ScrollToIndex", iconEmoji = "📜") {
            Card(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(8.dp).testTag("lazy_list"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed((0..24).toList()) { index, _ ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth().testTag("list_item_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("List Item #$index", fontWeight = FontWeight.Bold)
                                Text("Tag: list_item_$index", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// TAB 4: CLOCK, STATES & OS CONTROL SHOWCASE
// ----------------------------------------------------------------------------
@Composable
private fun ClockAndSystemShowcaseTab() {
    var timerSeconds by remember { mutableIntStateOf(60) }
    var isEnabledToggle by remember { mutableStateOf(true) }
    var isDelayedNodeVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (timerSeconds > 0) {
                timerSeconds--
            } else {
                isDelayedNodeVisible = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShowcaseSection(title = "Compose Test Clock (AdvanceTime)", iconEmoji = "⏱️") {
            Text(
                text = "Timer: ${timerSeconds}s",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("timer_text")
            )
            Text("AdvanceTime.byMillis(10000L) advances this test clock instantly.")
        }

        ShowcaseSection(title = "Node Enabled & Toggle Queries", iconEmoji = "🎛️") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Target Button:")
                Switch(
                    checked = isEnabledToggle,
                    onCheckedChange = { isEnabledToggle = it },
                    modifier = Modifier.testTag("switch_toggle")
                )
            }

            Button(
                onClick = {},
                enabled = isEnabledToggle,
                modifier = Modifier.fillMaxWidth().testTag("target_toggle_button")
            ) {
                Text(if (isEnabledToggle) "Target Node: Enabled" else "Target Node: Disabled")
            }
        }

        ShowcaseSection(title = "Polling Wait (Wait.forTag)", iconEmoji = "⏳") {
            if (isDelayedNodeVisible) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().testTag("btn_delayed_appear")
                ) {
                    Text("Delayed Node Appeared!")
                }
            } else {
                Text("Waiting for delayed node to appear...")
            }
        }

        ShowcaseSection(title = "OS & System Controls (OnDevice)", iconEmoji = "📱") {
            Text("Supports OnDevice.pressBack(), pressHome(), rotateScreenPortrait(), executeShell(), etc.")
        }
    }
}

// ----------------------------------------------------------------------------
// TAB 5: BDD WORKFLOW & DIAGNOSTICS SHOWCASE
// ----------------------------------------------------------------------------
@Composable
private fun BddAndDiagnosticsShowcaseTab() {
    var bddStep by remember { mutableStateOf("State: Initialized") }
    var loggedInUser by remember { mutableStateOf<String?>(null) }
    var usernameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusCard(bddStep, testTag = "status_text")

        ShowcaseSection(title = "Live BDD Workflow Simulator", iconEmoji = "🎬") {
            if (loggedInUser == null) {
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth().testTag("input_username")
                )

                Button(
                    onClick = {
                        loggedInUser = usernameInput.ifBlank { "Alice" }
                        bddStep = "State: Logged In as $loggedInUser"
                    },
                    modifier = Modifier.fillMaxWidth().testTag("btn_login")
                ) {
                    Text("Execute Workflow (btn_login)")
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome, $loggedInUser!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("txt_welcome")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                loggedInUser = null
                                bddStep = "State: Logged Out"
                            },
                            modifier = Modifier.testTag("btn_logout")
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }

        ShowcaseSection(title = "Diagnostic Failure Report Capture", iconEmoji = "📊") {
            Text("Automated HTML failure report generator (FailureDiagnosticWatcher) dumps semantics trees & screenshots on failure.")
        }
    }
}

// ----------------------------------------------------------------------------
// HELPER COMPONENTS
// ----------------------------------------------------------------------------
@Composable
private fun StatusCard(statusText: String, testTag: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    iconEmoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(iconEmoji, fontSize = 20.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            content()
        }
    }
}
