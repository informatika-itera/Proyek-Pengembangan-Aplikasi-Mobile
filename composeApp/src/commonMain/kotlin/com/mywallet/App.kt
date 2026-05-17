package com.mywallet

import androidx.compose.runtime.Composable
import com.mywallet.navigation.MainNavigation
import com.mywallet.theme.MyWalletTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MyWalletTheme {
        MainNavigation()
    }
}
