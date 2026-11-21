package app.template.patches.vwfndr

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.patch
import app.template.patches.shared.Constants.COMPATIBILITY_VWFNDR
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction

@Suppress("unused")
val disablePhotoLimitPatch = patch(
    name = "Disable photo limit",
    description = "Removes the photo capture limit from VWFNDR + MBL, allowing unlimited shots per session.",
    default = true
) {
    compatibleWith(COMPATIBILITY_VWFNDR)

    execute {
        listOf(
            PhotoLimitFingerprint,
            PhotoLimitFingerprint2,
        ).forEach { fingerprint ->
            runCatching {
                fingerprint.method.apply {
                    val instructions = implementation!!.instructions.toList()

                    // find the const that holds the limit value (small positive int)
                    val limitIndex = instructions.indexOfFirst { instruction ->
                        (instruction.opcode == Opcode.CONST_16 ||
                         instruction.opcode == Opcode.CONST_4 ||
                         instruction.opcode == Opcode.CONST) &&
                        (instruction as? NarrowLiteralInstruction)
                            ?.narrowLiteral?.let { it in 1..9999 } == true
                    }

                    if (limitIndex != -1) {
                        // replace the limit constant with a very large value
                        replaceInstruction(limitIndex, "const/high16 v0, 0x7fff")
                        println("[vwfndr-patch] photo limit const replaced at index $limitIndex")
                    } else {
                        // fallback: replace entire method body, return MAX_INT
                        implementation!!.instructions.toList()
                            .indices.forEach { _ -> implementation!!.instructions.removeAt(0) }
                        addInstructions(
                            0,
                            """
                                const/high16 v0, 0x7fff
                                return v0
                            """
                        )
                        println("[vwfndr-patch] photo limit method body replaced (fallback)")
                    }
                }
            }.onFailure {
                println("[vwfndr-patch] ${fingerprint::class.simpleName} not resolved — skipping")
            }
        }
    }
}
