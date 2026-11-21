package app.template.patches.vwfndr

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.patch
import app.template.patches.shared.Constants.COMPATIBILITY_VWFNDR
import com.android.tools.smali.dexlib2.Opcode

@Suppress("unused")
val unlockPaidFeaturesPatch = patch(
    name = "Unlock paid features",
    description = "Unlocks premium/paid features in VWFNDR + MBL including DNG shooting, graded JPEG, and hidden settings.",
    default = true
) {
    compatibleWith(COMPATIBILITY_VWFNDR)

    execute {
        // Strategy 1 — force isPurchased / hasPremium style checks to return true
        listOf(
            PurchaseCheckFingerprint,
            PurchaseCheckFingerprint2,
            PurchaseCheckFingerprint3,
            BillingStateFingerprint,
            BillingStateFingerprint2,
        ).forEach { fingerprint ->
            runCatching {
                fingerprint.method.apply {
                    // clear existing body, inject: return true
                    implementation!!.instructions.toList()
                        .indices.forEach { _ -> implementation!!.instructions.removeAt(0) }
                    addInstructions(
                        0,
                        """
                            const/4 v0, 0x1
                            return v0
                        """
                    )
                }
            }.onFailure {
                println("[vwfndr-patch] ${fingerprint::class.simpleName} not resolved — skipping")
            }
        }

        // Strategy 2 — NOP the conditional branch in feature gates
        listOf(
            FeatureGateFingerprint,
            FeatureGateFingerprint2,
            FeatureGateFingerprint3,
        ).forEach { fingerprint ->
            runCatching {
                fingerprint.method.apply {
                    val instructions = implementation!!.instructions.toList()
                    val gateIndex = instructions.indexOfFirst {
                        it.opcode == Opcode.IF_EQZ || it.opcode == Opcode.IF_NEZ
                    }
                    if (gateIndex != -1) {
                        replaceInstruction(gateIndex, "nop")
                    }
                }
            }.onFailure {
                println("[vwfndr-patch] ${fingerprint::class.simpleName} not resolved — skipping")
            }
        }
    }
}
