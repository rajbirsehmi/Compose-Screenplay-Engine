package com.sehmi.engine.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Custom Lint detector that identifies direct raw Compose UI test API usages
 * (such as `performClick`, `performTextReplacement`, `performScrollTo` on SemanticsNodeInteraction)
 * that bypass the Screenplay Engine's flakiness retry pipeline and diagnostics capture.
 *
 * Enforces usage of Screenplay Engine action wrappers (e.g., `Click.onTag`).
 */
class DirectUiTestApiUsageDetector : Detector(), SourceCodeScanner {

    companion object {
        /**
         * The Lint [Issue] definition for raw Compose test API usage.
         */
        val ISSUE = Issue.create(
            id = "DirectUiTestApiUsage",
            briefDescription = "Avoid direct raw Compose UI test API usage without Screenplay engine wrappers.",
            explanation = "Direct usage of standard Compose testing APIs (e.g. performClick) bypasses the Screenplay Engine's flakiness retry pipeline and failure diagnostics capture. Use Screenplay actions (e.g. Click.onTag) instead.",
            category = Category.CORRECTNESS,
            severity = Severity.ERROR,
            implementation = Implementation(
                DirectUiTestApiUsageDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    /**
     * Returns the list of method names applicable for this detector.
     *
     * @return List of target method names.
     */
    override fun getApplicableMethodNames(): List<String> = listOf("performClick", "performTextReplacement", "performScrollTo")

    /**
     * Visits method calls in UAST to detect unauthorized raw Compose UI test API calls.
     *
     * @param context The lint evaluation context.
     * @param node The UAST call expression node.
     * @param method The resolved PSI method.
     */
    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "androidx.compose.ui.test.SemanticsNodeInteractionKt")) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Avoid raw Compose test API '${method.name}'. Use Compose Screenplay Engine actions instead."
            )
        }
    }
}
