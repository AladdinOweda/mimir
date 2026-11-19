package com.mimir.contracts

import com.mimir.contracts.aihub.AiHubCapabilityIds
import com.mimir.contracts.bot.BotCapabilityIds
import com.mimir.contracts.collab.CollabCapabilityIds
import com.mimir.contracts.computer.ComputerCapabilityIds
import com.mimir.contracts.ie.IeCapabilityIds
import com.mimir.contracts.process.ProcessCapabilityIds
import com.mimir.contracts.spe.SpeCapabilityIds
import com.mimir.contracts.uft.UftCapabilityIds
import com.mimir.contracts.vault.VaultCapabilityIds
import com.mimir.contracts.verify.VerifyCapabilityIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Stable capability ID strings — engines must not drift. */
class ContractIdsStabilityTest {
    @Test
    fun process_and_vault() {
        assertEquals("process.exec.argv", ProcessCapabilityIds.EXEC_ARGV)
        assertEquals("vault.put", VaultCapabilityIds.PUT)
    }

    @Test
    fun computer_collab_ie() {
        assertEquals("computer.observe", ComputerCapabilityIds.OBSERVE)
        assertEquals("collab.group.open", CollabCapabilityIds.GROUP_OPEN)
        assertEquals("ie.execute", IeCapabilityIds.EXECUTE)
        assertTrue(ComputerCapabilityIds.ALL.size >= 5)
    }

    @Test
    fun aihub_uft_spe_bot_verify() {
        assertEquals("aihub.endpoint.register", AiHubCapabilityIds.REGISTER_ENDPOINT)
        assertEquals("uft.transfer.create", UftCapabilityIds.TRANSFER_CREATE)
        assertEquals("spe.spec.create", SpeCapabilityIds.SPEC_CREATE)
        assertEquals("bot.profile.create", BotCapabilityIds.PROFILE_CREATE)
        assertEquals("verify.submit", VerifyCapabilityIds.SUBMIT)
    }
}
