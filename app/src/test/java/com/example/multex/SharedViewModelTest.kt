package com.example.multex

import android.net.Uri
import androidx.compose.ui.graphics.BlendMode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class SharedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SharedViewModel

    @Before
    fun setUp() {
        viewModel = SharedViewModel()
    }

    @Test
    fun onUri1Change_updatesImageUri1() {
        val uri = Uri.parse("file://test/uri1")
        viewModel.onUri1Change(uri)
        assertEquals(uri, viewModel.imageUri1.value)
    }

    @Test
    fun onUri2Change_updatesImageUri2() {
        val uri = Uri.parse("file://test/uri2")
        viewModel.onUri2Change(uri)
        assertEquals(uri, viewModel.imageUri2.value)
    }

    @Test
    fun swapImages_swapsUrisAndRotations() {
        val uri1 = Uri.parse("file://test/uri1")
        val uri2 = Uri.parse("file://test/uri2")
        viewModel.onUri1Change(uri1)
        viewModel.onUri2Change(uri2)
        viewModel.rotateImage1()

        val initialUri1 = viewModel.imageUri1.value
        val initialUri2 = viewModel.imageUri2.value
        val initialRotation1 = viewModel.rotation1.value
        val initialRotation2 = viewModel.rotation2.value

        viewModel.swapImages()

        assertEquals(initialUri2, viewModel.imageUri1.value)
        assertEquals(initialUri1, viewModel.imageUri2.value)
        assertEquals(initialRotation2, viewModel.rotation1.value)
        assertEquals(initialRotation1, viewModel.rotation2.value)
    }

    @Test
    fun rotateImage1_rotatesBy90Degrees() {
        val initialRotation = viewModel.rotation1.value
        viewModel.rotateImage1()
        assertEquals((initialRotation + 90f) % 360f, viewModel.rotation1.value)
    }

    @Test
    fun rotateImage2_rotatesBy90Degrees() {
        val initialRotation = viewModel.rotation2.value
        viewModel.rotateImage2()
        assertEquals((initialRotation + 90f) % 360f, viewModel.rotation2.value)
    }

    @Test
    fun resetRotation1_resetsRotationTo0() {
        viewModel.rotateImage1()
        viewModel.resetRotation1()
        assertEquals(0f, viewModel.rotation1.value)
    }

    @Test
    fun resetRotation2_resetsRotationTo0() {
        viewModel.rotateImage2()
        viewModel.resetRotation2()
        assertEquals(0f, viewModel.rotation2.value)
    }

    @Test
    fun onBlendModeChange_updatesBlendMode() {
        val blendMode = BlendMode.ColorBurn
        viewModel.onBlendModeChange(blendMode)
        assertEquals(blendMode, viewModel.blendMode.value)
    }

    @Test
    fun onAlpha1Change_updatesAlpha1() {
        val alpha = 0.5f
        viewModel.onAlpha1Change(alpha)
        assertEquals(alpha, viewModel.alpha1.value)
    }

    @Test
    fun onAlpha2Change_updatesAlpha2() {
        val alpha = 0.5f
        viewModel.onAlpha2Change(alpha)
        assertEquals(alpha, viewModel.alpha2.value)
    }

    @Test
    fun onBrightness1Change_updatesBrightness1() {
        val brightness = 1.5f
        viewModel.onBrightness1Change(brightness)
        assertEquals(brightness, viewModel.brightness1.value)
    }

    @Test
    fun onContrast1Change_updatesContrast1() {
        val contrast = 1.5f
        viewModel.onContrast1Change(contrast)
        assertEquals(contrast, viewModel.contrast1.value)
    }

    @Test
    fun onSaturation1Change_updatesSaturation1() {
        val saturation = 1.5f
        viewModel.onSaturation1Change(saturation)
        assertEquals(saturation, viewModel.saturation1.value)
    }

    @Test
    fun onHighlights1Change_updatesHighlights1() {
        val highlights = 0.5f
        viewModel.onHighlights1Change(highlights)
        assertEquals(highlights, viewModel.highlights1.value)
    }

    @Test
    fun onShadows1Change_updatesShadows1() {
        val shadows = 0.5f
        viewModel.onShadows1Change(shadows)
        assertEquals(shadows, viewModel.shadows1.value)
    }

    @Test
    fun onBrightness2Change_updatesBrightness2() {
        val brightness = 1.5f
        viewModel.onBrightness2Change(brightness)
        assertEquals(brightness, viewModel.brightness2.value)
    }

    @Test
    fun onContrast2Change_updatesContrast2() {
        val contrast = 1.5f
        viewModel.onContrast2Change(contrast)
        assertEquals(contrast, viewModel.contrast2.value)
    }

    @Test
    fun onSaturation2Change_updatesSaturation2() {
        val saturation = 1.5f
        viewModel.onSaturation2Change(saturation)
        assertEquals(saturation, viewModel.saturation2.value)
    }

    @Test
    fun onHighlights2Change_updatesHighlights2() {
        val highlights = 0.5f
        viewModel.onHighlights2Change(highlights)
        assertEquals(highlights, viewModel.highlights2.value)
    }

    @Test
    fun onShadows2Change_updatesShadows2() {
        val shadows = 0.5f
        viewModel.onShadows2Change(shadows)
        assertEquals(shadows, viewModel.shadows2.value)
    }

    @Test
    fun resetSettings_resetsAllSettings() {
        // Change some values
        viewModel.onBlendModeChange(BlendMode.ColorBurn)
        viewModel.onAlpha1Change(0.5f)
        viewModel.rotateImage1()

        // Reset
        viewModel.resetSettings()

        // Verify
        assertEquals(BlendMode.Screen, viewModel.blendMode.value)
        assertEquals(1f, viewModel.alpha1.value)
        assertEquals(1f, viewModel.alpha2.value)
        assertEquals(1f, viewModel.brightness1.value)
        assertEquals(1f, viewModel.contrast1.value)
        assertEquals(1f, viewModel.saturation1.value)
        assertEquals(0f, viewModel.highlights1.value)
        assertEquals(0f, viewModel.shadows1.value)
        assertEquals(1f, viewModel.brightness2.value)
        assertEquals(1f, viewModel.contrast2.value)
        assertEquals(1f, viewModel.saturation2.value)
        assertEquals(0f, viewModel.highlights2.value)
        assertEquals(0f, viewModel.shadows2.value)
        assertEquals(0f, viewModel.rotation1.value)
        assertEquals(0f, viewModel.rotation2.value)
    }

    @Test
    fun onImageSelectionIntroShown_updatesFlag() {
        assertFalse(viewModel.imageSelectionIntroShown.value)
        viewModel.onImageSelectionIntroShown()
        assertTrue(viewModel.imageSelectionIntroShown.value)
    }
}
