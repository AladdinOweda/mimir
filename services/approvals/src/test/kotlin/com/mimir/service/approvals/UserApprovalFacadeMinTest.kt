package com.mimir.service.approvals

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserApprovalFacadeMinTest {
    @Test
    fun grant_unknown_request_returns_null() {
        val f = UserApprovalFacade()
        assertNull(f.grant("no-such-request"))
    }

    @Test
    fun deny_unknown_returns_false() {
        assertFalse(UserApprovalFacade().deny("no-such-request"))
    }

    @Test
    fun request_grant_issues_token_once() {
        val f = UserApprovalFacade()
        val id = f.request("act", "res", "scope", "why")
        assertNotNull(f.grant(id))
        assertNull(f.grant(id)) // already consumed
    }
}
