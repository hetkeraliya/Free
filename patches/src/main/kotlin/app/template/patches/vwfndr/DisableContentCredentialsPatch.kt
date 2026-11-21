package app.template.patches.vwfndr

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.patch
import app.template.patches.shared.Constants.COMPATIBILITY_VWFNDR

@Suppress("unused")
val disableContentCredentialsPatch = patch(
    name = "Disable Content Credentials",
    description = "Removes C2PA Content Credentials embedding from DNG and JPEG files captured by VWFNDR + MBL.",
    default = false
) {
    compatibleWith(COMPATIBILITY_VWFNDR)

    execute {
        listOf(
            ContentCredentialsFingerprint,
            ContentCredentialsFingerprint2,
        ).forEach { fingerprint ->
            runCatching {
                fingerprint.method.apply {
                    // wipe method body, inject immediate return
                    implementation!!.instructions.toList()
                        .indices.forEach { _ -> implementation!!.instructions.removeAt(0) }
                    addInstructions(0, "return-void")
                    println("[vwfndr-patch] C2PA method neutered: ${this.name}")
                }
            }.onFailure {
                println("[vwfndr-patch] ${fingerprint::class.simpleName} not resolved — skipping")
            }
        }
    }
}
