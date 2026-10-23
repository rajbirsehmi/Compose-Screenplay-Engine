package com.sehmi.engine.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.client.api.Vendor

/**
 * Lint issue registry for the Compose Screenplay Engine, registering custom detectors
 * such as [DirectUiTestApiUsageDetector].
 */
class ScreenplayIssueRegistry : IssueRegistry() {
    /**
     * List of lint issues supported by this registry.
     */
    override val issues: List<Issue>
        get() = listOf(DirectUiTestApiUsageDetector.ISSUE)

    /**
     * Lint API version used by this registry.
     */
    override val api: Int
        get() = com.android.tools.lint.detector.api.CURRENT_API

    /**
     * Vendor metadata for the lint issue registry.
     */
    override val vendor = Vendor(
        vendorName = "Compose Screenplay Engine",
        identifier = "compose-screenplay-engine",
        feedbackUrl = "https://github.com/rajbirsehmi/Compose-Screenplay-Engine",
        contact = "support@sehmi.com"
    )
}
