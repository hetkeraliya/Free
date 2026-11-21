package app.template.patches.vwfndr

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.literal
import app.morphe.patcher.opcode
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/**
 * Finds the method responsible for returning whether the user has purchased premium.
 * Targets any method that:
 *   - returns a boolean (Z)
 *   - takes no parameters
 *   - contains a string referencing purchase/premium state
 */
object PurchaseCheckFingerprint : Fingerprint(
    returnType = "Z",
    accessFlags = listOf(AccessFlags.PUBLIC),
    parameters = listOf(),
    filters = listOf(
        string("isPurchased"),
    )
)

object PurchaseCheckFingerprint2 : Fingerprint(
    returnType = "Z",
    accessFlags = listOf(AccessFlags.PUBLIC),
    parameters = listOf(),
    filters = listOf(
        string("premium"),
    )
)

object PurchaseCheckFingerprint3 : Fingerprint(
    returnType = "Z",
    accessFlags = listOf(AccessFlags.PUBLIC),
    parameters = listOf(),
    filters = listOf(
        string("isSubscribed"),
    )
)

/**
 * Finds the Google Play billing purchase verification method.
 * Looks for methods that check "PURCHASED" state string from Play Billing.
 */
object BillingStateFingerprint : Fingerprint(
    returnType = "Z",
    filters = listOf(
        string("PURCHASED"),
        opcode(Opcode.MOVE_RESULT, MatchAfterImmediately()),
    )
)

object BillingStateFingerprint2 : Fingerprint(
    returnType = "Z",
    filters = listOf(
        string("purchase_state"),
    )
)

/**
 * Finds the feature gate — a method that blocks UI/functionality
 * behind a purchase check using a conditional branch.
 */
object FeatureGateFingerprint : Fingerprint(
    returnType = "V",
    filters = listOf(
        string("feature_locked"),
        opcode(Opcode.IF_EQZ, MatchAfterImmediately()),
    )
)

object FeatureGateFingerprint2 : Fingerprint(
    returnType = "V",
    filters = listOf(
        string("pro_only"),
        opcode(Opcode.IF_EQZ, MatchAfterImmediately()),
    )
)

object FeatureGateFingerprint3 : Fingerprint(
    returnType = "V",
    filters = listOf(
        string("upgrade_required"),
        opcode(Opcode.IF_NEZ, MatchAfterImmediately()),
    )
)

/**
 * Finds the photo capture limit — an integer constant checked
 * against the current photo count.
 */
object PhotoLimitFingerprint : Fingerprint(
    returnType = "I",
    accessFlags = listOf(AccessFlags.PUBLIC),
    parameters = listOf(),
    filters = listOf(
        string("maxPhotos"),
        opcode(Opcode.IF_GE, MatchAfterImmediately()),
    )
)

object PhotoLimitFingerprint2 : Fingerprint(
    returnType = "I",
    accessFlags = listOf(AccessFlags.PUBLIC),
    parameters = listOf(),
    filters = listOf(
        string("capture_limit"),
    )
)

/**
 * Finds C2PA / Content Credentials embedding method.
 */
object ContentCredentialsFingerprint : Fingerprint(
    returnType = "V",
    filters = listOf(
        string("c2pa"),
    )
)

object ContentCredentialsFingerprint2 : Fingerprint(
    returnType = "V",
    filters = listOf(
        string("content_credentials"),
    )
)
