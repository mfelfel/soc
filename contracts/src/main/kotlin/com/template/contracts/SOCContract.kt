package com.template.contracts

import com.template.states.SOCState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.requireThat

// ************
// * Contract *
// ************
class SOCContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.SOCContract"
    }

    // Our create command.
    class Create : CommandData

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        val output = tx.outputsOfType<SOCState>().first()
        when (command.value) {
            is Commands.Create -> requireThat {
//                "No inputs should be consumed when issuing an SOC.".using(tx.inputStates.isEmpty())
//                "The location must be valid".using(output.location.length == 13)
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Create : Commands
    }
}