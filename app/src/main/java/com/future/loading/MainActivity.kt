package com.future.loading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.future.loading.ui.theme.LoadingTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.LightGray
                ) {
                    Loading(whiteBackground = true)
                }
            }
        }
    }
}


@Composable
fun Loading(
    whiteBackground: Boolean = false,
    viewModel: LoadingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    LaunchedEffect(Unit) {
        viewModel.setBackground(whiteBackground)
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.startTimer(System.currentTimeMillis())
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.timerDestroy()
                }
                else -> {}
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)
        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val width = LocalConfiguration.current.screenWidthDp
    val count = 8
    val rotateAngle = (360 / count).toDouble()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        //1284总宽度  菊花宽度：209    17宽 38长     106     17/53  38/53
        Box(
            modifier = Modifier
                .width((width * 0.16f).dp)
                .aspectRatio(1f)
                .background(
                    if (whiteBackground) Color.White else Color.Black,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
                    .aspectRatio(1f)
            ) {
                val r = size.width / 2
                //圆弧形的矩形 长度
                val drawWidth = 0.50 * r
                //圆弧形的矩形 宽度
                val strokeWidth = 0.32 * r
                if (viewModel.mTicker.value > 0) {
                    for (index in 1..count) {
                        val startX =
                            (r + (r - drawWidth) * cos(Math.toRadians(rotateAngle * index))).toFloat()
                        val startY =
                            (r - (r - drawWidth) * sin(Math.toRadians(rotateAngle * index))).toFloat()
                        val endX = (r + r * cos(Math.toRadians(rotateAngle * index))).toFloat()
                        val endY = (r - r * sin(Math.toRadians(rotateAngle * index))).toFloat()
                        drawLine(
                            color = viewModel.mColor[index - 1],
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            cap = StrokeCap.Round,
                            strokeWidth = strokeWidth.toFloat(),
                        )
                    }
                }
            }
        }
    }
}