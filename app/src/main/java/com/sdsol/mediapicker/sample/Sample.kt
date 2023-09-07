package com.sdsol.mediapicker.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sdsol.mediapicker.R


@Preview
@Composable
private fun composable(){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(all = 8.dp)
    ) {
        val (title, type) = createRefs()

        Image(
            painter = painterResource(R.drawable.ic_photo),
            contentDescription = "Badge",
            colorFilter = ColorFilter.tint(
                colorResource(
                    id =  R.color.white
                )
            ),
            modifier = Modifier
                .size(28.dp)
                .constrainAs(type) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                },
        )
        Text(
            text = "123456789009876554431213TEST12312312312TEST12312312334234TEST 123456789009876554431213TEST12312312312TEST12312312334234TEST 123456789009876554431213TEST12312312312TEST12312312334234TEST",
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(type.end, 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            fontSize = 16.sp,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                color = colorResource(
                    id = R.color.white
                )
            ),
            textAlign = TextAlign.Start
        )
    }
}