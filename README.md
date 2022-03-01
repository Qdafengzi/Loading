<img src="art/Media_220301_223226.gif" width="200"/>  <img src="art/Media_220301_223336.gif" width="200"/>


- 绘制
  利用生命周期控制 转圈圈 和倒计时
  还没研究好如何利用动画周期性的驱动绘制，后面再做优化。
```kotlin
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
```
- ViewModel

```kotlin
class LoadingViewModel : ViewModel() {

    private var mTimer: CountDownTimer? = null

    private val white1 = Color(0xFFCCCCCC)
    private val white2 = Color(0xD6CCCCCC)
    private val white3 = Color(0xB8CCCCCC)
    private val white4 = Color(0x99CCCCCC)
    private val white5 = Color(0x7ACCCCCC)
    private val white6 = Color(0x5CCCCCCC)
    private val white7 = Color(0x3DCCCCCC)
    private val white8 = Color(0x1FCCCCCC)

    private val black1 = Color(0xFF000000)
    private val black2 = Color(0xD6000000)
    private val black3 = Color(0xB8000000)
    private val black4 = Color(0x99000000)
    private val black5 = Color(0x7A000000)
    private val black6 = Color(0x5C000000)
    private val black7 = Color(0x3D000000)
    private val black8 = Color(0x1F000000)


    val mColor = mutableListOf(
        white1,
        white2,
        white3,
        white4,
        white5,
        white6,
        white7,
        white8,
    )

    fun setBackground(whiteBackground: Boolean) {
        if (whiteBackground) {
            mColor[0] = black1
            mColor[1] = black2
            mColor[2] = black3
            mColor[3] = black4
            mColor[4] = black5
            mColor[5] = black6
            mColor[6] = black7
            mColor[7] = black8
        } else {
            mColor[0] = white1
            mColor[1] = white2
            mColor[2] = white3
            mColor[3] = white4
            mColor[4] = white5
            mColor[5] = white6
            mColor[6] = white7
            mColor[7] = white8
        }
    }

    val mTicker = mutableStateOf(0L)

    /**
     * 支付倒计时
     */
    fun startTimer(time: Long) {
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer = null
        }
        mTimer = object : CountDownTimer(time, 100) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val data = mColor.removeAt(mColor.size - 1)
                mColor.add(0, data)
                mTicker.value = System.currentTimeMillis()
            }

            override fun onFinish() {

            }
        }.start()
    }

    fun timerDestroy() {
        mTimer?.cancel()
        mTimer = null
    }
}
```
