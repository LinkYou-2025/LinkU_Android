
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.Paperlogy

@Composable
fun CircleItem(
    emoji: String? = null,
    text: String,
    sizeDp: Float,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedGradient: List<Color> = listOf(
        Color(0xFF2C6FFF).copy(alpha = 0.4f),
        Color(0xFFC800FF).copy(alpha = 0.4f)
    ),
    borderGradient: List<Color> = listOf(
        Color(0xFF2C6FFF),
        Color(0xFFC800FF)
    )
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .border(
                width = 1.dp,
                brush = Brush.sweepGradient(borderGradient),
                shape = CircleShape
            )
            .background(
                brush = if (selected)
                    Brush.horizontalGradient(selectedGradient)
                else SolidColor(Color.White),
                shape = CircleShape
            )
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (emoji != null) {
                Text(
                    text = emoji,
                    fontFamily = Paperlogy,
                    fontSize = 24.sp
                )
            }

            Text(
                text = text,
                fontSize = 14.sp,
                fontFamily = Paperlogy,
                textAlign = TextAlign.Center,
                color = if (selected) Color.White else Color.Black
            )
        }
    }
}