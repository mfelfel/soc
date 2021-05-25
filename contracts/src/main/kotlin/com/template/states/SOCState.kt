package com.template.states

import com.template.contracts.SOCContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// *********
// * State *
// *********
@BelongsToContract(SOCContract::class)
data class SOCState(val title: String,
                    val description: String,
                    val location: String,
                    val industryParticipant: Party,
                    val complainant: Party,
                    val aer: Party) : ContractState {
    override val participants: List<AbstractParty> = listOf(complainant, industryParticipant, aer)
}





